package org.thingsboard.rule.engine.wechat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeComText {

    private String content;
}
