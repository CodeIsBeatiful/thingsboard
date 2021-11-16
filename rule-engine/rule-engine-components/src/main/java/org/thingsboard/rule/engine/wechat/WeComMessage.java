package org.thingsboard.rule.engine.wechat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeComMessage {

    private String touser;

    private String msgtype;

    private String agentid;

    private int safe;

}
