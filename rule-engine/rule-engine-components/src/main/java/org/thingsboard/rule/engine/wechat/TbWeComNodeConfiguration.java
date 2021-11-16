package org.thingsboard.rule.engine.wechat;

import lombok.Data;
import org.thingsboard.rule.engine.api.NodeConfiguration;

import java.util.Collections;
import java.util.List;

@Data
public class TbWeComNodeConfiguration implements NodeConfiguration {


    private String agentId;

    private String corpId;

    private String corpSecret;

    private List<String> users;

    private String contentTemplate;

    @Override
    public NodeConfiguration defaultConfiguration() {
        TbWeComNodeConfiguration configuration = new TbWeComNodeConfiguration();
        configuration.setCorpId("");
        configuration.setCorpSecret("");
        configuration.setAgentId("");
        configuration.setUsers(Collections.emptyList());
        configuration.setContentTemplate("");
        return configuration;
    }
}
