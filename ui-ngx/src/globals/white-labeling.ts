import {environment as env} from "@env/environment";


export const whiteLabeling = {
  logoImageUrl: "assets/logo_title_white.svg",
  logoImageHeight: 42,
  faviconUrl: "thingsboard.ico",
  appTitle: env.appTitle,
  paletteSettings: null,
  helpLinkBaseUrl: null,
  enableHelpLinks: true,
  whiteLabelingEnabled: true,
  showNameVersion: false,
  platformName: "thingsboard",
  platformVersion: env.tbVersion,
  customCss: null
}
