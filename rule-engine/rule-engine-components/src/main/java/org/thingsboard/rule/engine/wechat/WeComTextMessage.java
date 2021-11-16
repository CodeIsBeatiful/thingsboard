package org.thingsboard.rule.engine.wechat;


import lombok.Builder;
import lombok.Data;

@Data
public class WeComTextMessage extends WeComMessage{

    private WeComText text;

    public WeComTextMessage() {
        super();
    }

    @Builder
    public WeComTextMessage(String toUser, String msgType, String agentId, WeComText text, int safe) {
        super(toUser, msgType, agentId, safe);
        this.text = text;
    }


}
