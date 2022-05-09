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
package org.thingsboard.server.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Charsets;
import io.bit3.jsass.Compiler;
import io.bit3.jsass.Options;
import io.bit3.jsass.Output;
import io.bit3.jsass.OutputStyle;
import io.bit3.jsass.importer.Import;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.AdminSettings;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.whitelabel.Palette;
import org.thingsboard.server.common.data.whitelabel.PaletteSettings;
import org.thingsboard.server.dao.settings.AdminSettingsService;
import org.thingsboard.server.queue.util.TbCoreComponent;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api")
public class WhiteLabelController extends BaseController{

    @Autowired
    private AdminSettingsService adminSettingsService;

    private static final String SCSS_EXTENSION = ".scss";

    private static final String SCSS_CLASSPATH_PATTERN = "classpath:scss/*.scss";

    private static final String APP_THEME_SCSS = "app-theme.scss";

    private static final String LOGIN_THEME_SCSS = "login-theme.scss";

    private static final String KEY="whiteLabel";

    private Map<String, Import> importMap;

    private String scssAppTheme;

    private String scssLoginTheme;

    private Options options;


    private Compiler compiler;

    @PostConstruct
    public void init() throws Exception {
        initCompiler();
    }


    private void initCompiler() throws Exception {
        this.importMap = new HashMap<>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] scssResources = resolver.getResources(SCSS_CLASSPATH_PATTERN);
        for (Resource scssResource : scssResources) {
            String scssContent = StreamUtils.copyToString(scssResource.getInputStream(), Charsets.UTF_8);
            String fileName = scssResource.getFilename();
            if (APP_THEME_SCSS.equals(fileName)) {
                this.scssAppTheme = scssContent;
            } else if (LOGIN_THEME_SCSS.equals(fileName)) {
                this.scssLoginTheme = scssContent;
            } else if (fileName != null) {
                URI scssFileUri = scssResource.getURI();
                Import scssImport = new Import(scssFileUri, scssFileUri, scssContent);
                String path = fileName.substring(0, fileName.length() - SCSS_EXTENSION.length());
                this.importMap.put(path, scssImport);
            }
        }
        this.compiler = new Compiler();
        this.options = new Options();
        this.options.setImporters(Collections.singleton((url, previous) -> Collections.singletonList(this.importMap.get(url))));
        this.options.setOutputStyle(OutputStyle.COMPRESSED);
    }

    @RequestMapping(value = "/noauth/whiteLabel", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public JsonNode getWhiteLabeling() {
        JsonNode jsonNode = null;
        AdminSettings adminSettings = adminSettingsService.findAdminSettingsByKey(TenantId.SYS_TENANT_ID, KEY);
        if(adminSettings != null) {
            jsonNode = adminSettings.getJsonValue();
        }
        return jsonNode;
    }


    @PreAuthorize("hasAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/whiteLabel/whiteLabelParams", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public JsonNode save(@RequestBody JsonNode jsonNode) throws ThingsboardException {
//        accessControlService.checkPermission(getCurrentUser(), Resource.ADMIN_SETTINGS, Operation.WRITE);
        AdminSettings adminSettings = adminSettingsService.findAdminSettingsByKey(TenantId.SYS_TENANT_ID, KEY);
        if(adminSettings == null) {
            adminSettings = new AdminSettings();
            adminSettings.setKey(KEY);
        }
        adminSettings.setJsonValue(jsonNode);
        return adminSettingsService.saveAdminSettings(TenantId.SYS_TENANT_ID, adminSettings).getJsonValue();
    }

    @RequestMapping(value = "/noauth/loginThemeCss", method = RequestMethod.POST,produces = {"plain/text"})
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String loginThemeCss(@RequestBody PaletteSettings paletteSettings) throws ThingsboardException {
        try {
            return generateThemeCss(paletteSettings, true);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @RequestMapping(value = "/noauth/appThemeCss", method = RequestMethod.POST,produces = {"plain/text"})
    @ResponseStatus(value = HttpStatus.OK)
    public String appThemeCss(@RequestBody PaletteSettings paletteSettings) throws ThingsboardException {
        try {
            return generateThemeCss(paletteSettings, false);
        } catch (Exception e) {
            throw handleException(e);
        }
    }



    private String generateThemeCss(PaletteSettings paletteSettings, boolean loginTheme) throws Exception {
        String primaryPaletteName = getPaletteName(paletteSettings.getPrimaryPalette(), true);
        String primaryColors = "";
        String accentPaletteName = getPaletteName(paletteSettings.getAccentPalette(), false);
        String accentColors = "";
        String targetTheme = loginTheme ? this.scssLoginTheme : this.scssAppTheme;
        targetTheme = targetTheme.replaceAll("\\{\\{primary-palette\\}\\}", primaryPaletteName);
        targetTheme = targetTheme.replaceAll("\\{\\{primary-colors\\}\\}", primaryColors);
        targetTheme = targetTheme.replaceAll("\\{\\{accent-palette\\}\\}", accentPaletteName);
        targetTheme = targetTheme.replaceAll("\\{\\{accent-colors\\}\\}", accentColors);
        Output output = this.compiler.compileString(targetTheme, this.options);
        return output.getCss();
    }

    private String getPaletteName(Palette palette, boolean primary) {
        if (palette == null || palette.getType() == null){
            return primary ? "tb-primary" : "tb-accent";
        }
        return palette.getType();
    }
}
