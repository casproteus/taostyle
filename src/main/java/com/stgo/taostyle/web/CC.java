package com.stgo.taostyle.web;

public interface CC {
    String ADV_USER = "for_demo";
    String TextContentKey = "posInPage";
    String MediaUploadKey = "filepath";
    String user_role = "user_role";
    String ROLE_CLIENT = "ROLE_CLIENT";
    String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";
    String ROLE_ADMIN = "ROLE_ADMIN";
    String ROLE_MANAGER = "ROLE_MANAGER";
    // --------------------------------------
    String HTML = "HTML";

    String GALLERY = "GALLERY";
    String CATALOG = "CATALOG";
    String FEATURE = "FEATURE";
    String PRODUCT = "PRODUCT";
    String SERVICE = "SERVICE";
    String LOCATION = "LOCATION";

    String ADMIN = "sharethegoodones";
    String CLIENT = "client";
    String debugFlag = "debugFlag";
    String debugInfo = "debugInfo";

    String app_name = "app_name";
    String app_ContentManager = "app_ContentManager";
    String app_ManagerEmail = "app_ManagerEmail";
    String app_WebsiteAddress = "app_WebsiteAddress";
    String DEFAULT_LANG = "DEFAULT_LANG";
    String flash_1_URL = "flash_1_URL";
    String LANG = "lang";
    String menuIdx = "menuIdx";
    String position = "position";
    String W = "w";
    String H = "h";

    String CALCULAT_EXIST_NEEDED_ = "CALCULAT_EXIST_NEEDED_";
    String LANGUAGE_PRF_NEEDED_ = "LANGUAGE_PRF_NEEDED_";
    String THUMNEEDED_ = "THUMNEEDED_";

    String TEMP_SEP_PROD_FIELD_INFO = "^";

    String DEFAULT_IMAGE_TYPE = ".jpg";

    String STATUS_NEW = "new submit";
    String STATUS_WAITING = "waiting for review";
    String STATUS_COMMENTED = "reviewed";
    String STATUS_DELETED = "deleted";
    int STATUS_TO_PRINT = 10;
    int STATUS_PRINTED =20;
    
    String APPEND = "append";
    String OVERWRITE = "overwrite";
    
    String singleUploadUri = "mediauploads";
    String multiUploadUri = "gellaryUploads";
    String resetQRCode = "resetQRCode";

    String MAP_POS_X = "MAP_POS_X";
    String MAP_POS_Y = "MAP_POS_Y";

    String showDescriptionsOnBottom = "showDescriptionsOnBottom";

    // --------------------------------
    String MinDiffForOrentationCheck = "minDiff_Orentation";
    String rotateDirection = "rotateDirection";

    String cloak = "cloak";
    String SAVED_NAME_STR = "*";
    
    public enum customizes {
        app_ContentManager("tao"),
        app_ManagerEmail("info@ShareTheGoodOnes.com"),
        background_app("E5E5E5"),
        background_foot("666666"),
        background_footArea("888888"),
        flashpage_h("528"),
        flashpage_w("845"),
        foreground("fff"),
        foreground_app("000"),
        foreground_foot("fff"),
        foreground_footArea("fff"),
        indicate_language_with_img(""),
        IsTextPositionAbsolute("commont-absolute"),
        link_log1(""),
        link_log2("http://doit-rightnow.rhcloud.com"),
        link_log3(""),
        margin_left("10"),
        margin_right("10"),
        show_1_imgObject(""),
        show_1_TextObject(""),
        show_3swicher(""),
        show_3_Object("true"),
        show_3_Object_inSmallerRow(""),
        show_3_Object_text("true"),
        show_4_Object(""),
        show_MainSlider("50"),
        show_Menu("0"),
        show_myDocs_toClient("false"),
        show_SmallSlider(""),
        showFacebookOnTop(""),
        showGooglePlusOnTop(""),
        showGoogleSearchOnTop(""),
        showSearchOnHomePage(""),
        showTextOnSlider("620"),
        showTwitterOnTop(""),
        background_menu("888888"),
        foreground_menu("fff"),
        flash_1_URL(""),
        background_leftbar("999999"),
        background_pop("999999"),
        background_subpage("999999"),
        footer_h("90"),
        forground_subpage("444444"),
        MAP_h("270"),
        Center_img_w("60%"),
        show_registerForm("true"),
        stgo("s"),
        MAP_w("285"),
        MAP_zoom("16"),
        MAP_POS_X3("31.2360716"),
        MAP_POS_Y3("121.511461"),
        margin_top_logo("0"),
        showDescriptionsOnBottom("true"),
        show_SubMenu("false"),
        service_number_lg("3"),
        service_number_md("3"),
        service_number_sm("6"),
        service_number_xs("6"),
        show_QRCode("true"),
        show_service_bell("true"),
        app_WebsiteAddress("http://www.shareTheGoodOnes.com"),
        app_name("ShareTheGoodOnes"),
        CONTENTTYPE_1("MAINPAGE"),
        CONTENTTYPE_2("SERVICE"),
        CONTENTTYPE_3("FEATURE"),
        CONTENTTYPE_10("LOCATION"),
        MAP_POS_X2("45.497743"),
        MAP_POS_Y2("-73.554106");

        private String cusValue;

        private customizes(String cusValue) {
            this.cusValue = cusValue;
        }
        public String getValue(){
            return cusValue;
        }
    };
    
    public enum money {
        en("$"),
        fr("$"),
        zh("￥"),
        it("$");

        private String moneyLetter;

        private money(String moneyLetter) {
            this.moneyLetter = moneyLetter;
        }
        public String getValue(){
            return moneyLetter;
        }
    };
    //-------------------------------
    String tableID = "tableID";
}
