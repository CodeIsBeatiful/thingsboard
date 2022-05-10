/**
 * Copyright © 2016-2022 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
