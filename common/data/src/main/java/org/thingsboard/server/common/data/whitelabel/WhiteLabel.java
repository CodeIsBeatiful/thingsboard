package org.thingsboard.server.common.data.whitelabel;

import lombok.Data;

@Data
public class WhiteLabel {
    private String logoImageUrl;

    private Integer logoImageHeight;

    private String logoImageChecksum;

    private String faviconUrl;

    private String faviconChecksum;

    private String appTitle;

    private PaletteSettings paletteSettings;

    private String helpLinkBaseUrl;

    private boolean enableHelpLinks;

    protected boolean whiteLabelingEnabled;

    private boolean showNameVersion;

    private String platformName;

    private String platformVersion;

    private String customCss;

}
