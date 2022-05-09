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
