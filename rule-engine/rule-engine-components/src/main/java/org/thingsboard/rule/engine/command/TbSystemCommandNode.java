package org.thingsboard.rule.engine.command;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.rule.engine.api.*;
import org.thingsboard.rule.engine.api.util.TbNodeUtils;
import org.thingsboard.server.common.data.plugin.ComponentType;
import org.thingsboard.server.common.msg.TbMsg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RuleNode(
        type = ComponentType.EXTERNAL,
        name = "system command",
        configClazz = TbSystemCommandNodeConfiguration.class,
        nodeDescription = "run system command",
        nodeDetails = "run system command, you can config data keys which used for system environments,if success, <code>result</code> must in data",
        uiResources = {"static/rulenode/custom-nodes-config.js"},//指定前端资源文件
        configDirective = "tbActionNodeSystemCommandConfig",//指定前端资源文件中的组件
        icon = "keyboard_command_key")
public class TbSystemCommandNode implements TbNode {

    private static final ObjectMapper mapper = new ObjectMapper();

    private TbSystemCommandNodeConfiguration config;

    private List<String> messageNamesList;
    private String command;


    @Override
    public void init(TbContext tbContext, TbNodeConfiguration configuration) throws TbNodeException {
        this.config = TbNodeUtils.convert(configuration, TbSystemCommandNodeConfiguration.class);
        messageNamesList = config.getMessageNames();
        command = config.getCommand();

    }

    @Override
    public void onMsg(TbContext ctx, TbMsg msg) {
        try {
            Map<String, Object> dataMap = genDataMap(msg);
            String result = execCommand(dataMap, command);
            //new msg and tell
            String data = "{\"result\":\"" + result + "\"}";
            TbMsg tbMsg = ctx.newMsg(msg.getQueueName(), msg.getType(), msg.getOriginator(), msg.getCustomerId(), msg.getMetaData(),data);
            ctx.tellSuccess(tbMsg);
            //need ack old msg
            ctx.ack(msg);
        } catch (IOException e) {
            ctx.tellFailure(msg, e);
        } catch (RuntimeException e) {
            ctx.tellFailure(msg,e);
        }
    }

    private Map<String, Object> genDataMap(TbMsg msg) throws JsonProcessingException {
        HashMap<String, Object> dataMap = new HashMap<>();
        JsonNode dataJsonNode = mapper.readTree(msg.getData());
        for (String messageName : messageNamesList) {
            JsonNode jsonNode = dataJsonNode.get(messageName);
            if (jsonNode != null && !jsonNode.isNull()) {
                if (jsonNode.isBoolean()) {
                    dataMap.put(messageName, jsonNode.asBoolean());
                } else if (jsonNode.isNumber()) {
                    dataMap.put(messageName, jsonNode.asDouble());
                } else {
                    dataMap.put(messageName, jsonNode.asText());
                }
            }
        }
        return dataMap;
    }

    private static String execCommand(Map<String, Object> dataMap, String command) {
        ProcessBuilder processBuilder = new ProcessBuilder("/bin/sh", "-c", command);
        dataMap.forEach((String key,Object obj)->{
            processBuilder.environment().put(key,String.valueOf(obj));
        });
        Process process = null;
        String result = "";
        String errorResult = "";
        try {
            process = processBuilder.start();
            //get input stream
            InputStream in = process.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            //get error stream
            InputStream error = process.getErrorStream();
            BufferedReader errorBr = new BufferedReader(new InputStreamReader(error));
            String lineStr;
            while ((lineStr = br.readLine()) != null) {
                result += lineStr + "\n";
            }
            if(result.length() > 0){
                result = result.substring(0,result.length()-1);
            }
            br.close();
            in.close();
            while ((lineStr = errorBr.readLine()) != null) {
                errorResult += lineStr + "\n";
            }
            if(errorResult.length() > 0){
                errorResult = errorResult.substring(0,errorResult.length()-1);
            }
            errorBr.close();
            error.close();
            int exitCode = process.waitFor();
            if(exitCode != 0){
                throw new RuntimeException(errorResult);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            if(process != null) {
                process.destroy();
            }
        }
        return result;
    }

    @Override
    public void destroy() {
    }
}
