package org.thingsboard.rule.engine.command;

import lombok.Data;
import org.thingsboard.rule.engine.api.NodeConfiguration;

import java.util.Collections;
import java.util.List;

@Data
public class TbSystemCommandNodeConfiguration implements NodeConfiguration {

    private List<String> messageNames;
    private String command;



    @Override
    public NodeConfiguration defaultConfiguration() {
        TbSystemCommandNodeConfiguration tbSystemCommandNodeConfiguration = new TbSystemCommandNodeConfiguration();
        tbSystemCommandNodeConfiguration.setMessageNames(Collections.emptyList());
        tbSystemCommandNodeConfiguration.setCommand("");
        return tbSystemCommandNodeConfiguration;
    }
}
