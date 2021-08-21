package org.thingsboard.server.common.data.whitelabel;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class Palette {
    private String type;

    private Map<String, String> colors;

    @JsonProperty("extends")
    private String _extends;


}
