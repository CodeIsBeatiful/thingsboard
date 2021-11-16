package org.thingsboard.rule.engine.wechat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.thingsboard.rule.engine.api.*;
import org.thingsboard.rule.engine.api.util.TbNodeUtils;
import org.thingsboard.server.common.data.plugin.ComponentType;
import org.thingsboard.server.common.msg.TbMsg;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RuleNode(
        type = ComponentType.EXTERNAL,
        name = "wechat company",
        configClazz = TbWeComNodeConfiguration.class,
        nodeDescription = "use wechat-company to send message",
        nodeDetails = "use wechat-company to send message,use js to process message and send to ",
//        uiResources = {"static/rulenode/custom-nodes-config.js"},//指定前端资源文件
//        configDirective = "tbActionNodeCompanyWxConfig",//指定前端资源文件中的组件
        icon = "")
public class TbWeComNode implements TbNode {


    private volatile String accessToken;

    private TbWeComNodeConfiguration config;

    protected AsyncRestTemplate asyncRestTemplate;

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String WE_COM_API_URL = "https://qyapi.weixin.qq.com/cgi-bin/";

    private static final String WE_COM_ACCESS_TOKEN_GET_URL = WE_COM_API_URL + "gettoken?corpid={corpId}&corpsecret={corpSecret}";

    private static final String WE_COM_MESSAGE_SEND_URL = WE_COM_API_URL + "message/send?access_token={accessToken}";

    private AtomicBoolean accessTokenExpired = new AtomicBoolean(true);


    @Override
    public void init(TbContext ctx, TbNodeConfiguration configuration) throws TbNodeException {
        this.config = TbNodeUtils.convert(configuration, TbWeComNodeConfiguration.class);
        this.asyncRestTemplate = new AsyncRestTemplate();
        setAccessToken(true);
    }

    public String getAccessToken() {
        while (accessTokenExpired.get()) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                log.warn("get access token sleep interrupt");
            }
        }
        return accessToken;
    }

    private void setAccessToken(boolean sync) throws TbNodeException {
        Map<String, String> vars = new HashMap<>();
        vars.put("corpId", config.getCorpId());
        vars.put("corpSecret", config.getCorpSecret());
        ListenableFuture<ResponseEntity<JsonNode>> future = asyncRestTemplate.getForEntity(WE_COM_ACCESS_TOKEN_GET_URL, JsonNode.class, vars);
        if (sync) {
            ResponseEntity<JsonNode> jsonNodeResponseEntity;
            try {
                jsonNodeResponseEntity = future.get();
                if (!jsonNodeResponseEntity.hasBody()) {
                    throw new TbNodeException("get access token failed");
                }
                JsonNode body = jsonNodeResponseEntity.getBody();
                if (body.get("errcode").asInt() != 0) {
                    throw new TbNodeException("get access token failed, errCode is not zero, errMsg:" + body.get("errmsg").asText());
                }
                if (accessTokenExpired.compareAndSet(true, false)) {
                    accessToken = body.get("access_token").asText();
                }
            } catch (InterruptedException e) {
                throw new TbNodeException(e);
            } catch (ExecutionException e) {
                throw new TbNodeException(e);
            }
        } else {
            future.addCallback(new ListenableFutureCallback<>() {
                @Override
                public void onSuccess(ResponseEntity<JsonNode> jsonNodeResponseEntity) {
                    if (!jsonNodeResponseEntity.hasBody()) {
                        throw new RuntimeException("get access token failed");
                    }
                    JsonNode body = jsonNodeResponseEntity.getBody();
                    if (body.get("errcode").asInt() != 0) {
                        throw new RuntimeException("get access token failed, errCode is not zero, errMsg:" + body.get("errmsg").asText());
                    }
                    if (accessTokenExpired.compareAndSet(true, false)) {
                        accessToken = body.get("access_token").asText();
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {
                    throw new RuntimeException(throwable);
                }
            });
        }

    }

    @Override
    public void onMsg(TbContext ctx, TbMsg msg) throws ExecutionException, InterruptedException, TbNodeException {
        send(ctx, msg, fromTemplate(config.getContentTemplate(), msg));
        ctx.ack(msg);
    }

    private void send(TbContext ctx, TbMsg msg, String content) throws TbNodeException {
        WeComTextMessage weComTextMessage = WeComTextMessage.builder()
                .agentId(config.getAgentId())
                .toUser(String.join(",", config.getUsers()))
                .msgType("text")
                .text(WeComText.builder().content(content).build())
                .build();
        Map<String, String> vars = new HashMap<>();
        vars.put("accessToken", getAccessToken());
        HttpEntity<String> httpEntity;
        try {
            TbMsg newMsg = ctx.newMsg(msg.getQueueName(), msg.getType(), msg.getOriginator(), msg.getCustomerId(), msg.getMetaData(), mapper.writeValueAsString(weComTextMessage));
            httpEntity = new HttpEntity<>(newMsg.getData());
            ListenableFuture<ResponseEntity<JsonNode>> responseEntityListenableFuture = asyncRestTemplate.postForEntity(WE_COM_MESSAGE_SEND_URL, httpEntity, JsonNode.class, vars);
            responseEntityListenableFuture.addCallback(new ListenableFutureCallback<>() {

                @Override
                public void onSuccess(ResponseEntity<JsonNode> responseEntity) {
                    if (!responseEntity.hasBody()) {
                        ctx.tellFailure(newMsg, new Throwable("send message failed"));
                        return;
                    }
                    JsonNode body = responseEntity.getBody();
                    int errCode = body.get("errcode").asInt();
                    //access token expired
                    if (errCode == 42001) {
                        try {
                            if (accessTokenExpired.get()) {
                                setAccessToken(false);
                            }
                            send(ctx, msg, content);
                        } catch (TbNodeException e) {
                            ctx.tellFailure(newMsg, new Throwable(e));
                            return;
                        }
                    } else if (errCode != 0) {
                        ctx.tellFailure(newMsg, new Throwable("send message failed, errCode:" + errCode + " errMsg:" + body.get("errmsg").asText()));
                    } else {
                        ctx.tellSuccess(newMsg);
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {
                    ctx.tellFailure(newMsg, throwable);
                }
            });
        } catch (JsonProcessingException e) {
            throw new TbNodeException(e);
        }

    }

    private String fromTemplate(String template, TbMsg msg) {
        if (!StringUtils.isEmpty(template)) {
            return TbNodeUtils.processPattern(template, msg);
        } else {
            return null;
        }
    }


    @Override
    public void destroy() {

    }
}
