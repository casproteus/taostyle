package com.stgo.taostyle.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.stgo.taostyle.backend.security.TaoEncrypt;
import com.stgo.taostyle.domain.Customize;
import com.stgo.taostyle.domain.Feature;
import com.stgo.taostyle.domain.MediaUpload;
import com.stgo.taostyle.domain.MultiLanguageabl;
import com.stgo.taostyle.domain.Person;
import com.stgo.taostyle.domain.Service;
import com.stgo.taostyle.domain.TextContent;
import com.stgo.taostyle.domain.UserAccount;

@Controller
public class TaoUtil {

    private static Person advPerson = Person.findPersonByName(CC.ADV_USER);

    public static Person getAdvPerson() {
        if (advPerson == null) {
            advPerson = Person.findPersonByName(CC.ADV_USER);
            if (advPerson == null) {
                advPerson = new Person();
                advPerson.setName(CC.ADV_USER);
                advPerson.setPassword("PQlS9ShB//w=");
                advPerson.setRecordStatus(0);
                advPerson.setVersion(0);
                advPerson.persist();
                initializeForAdvPerson(advPerson);
            }
        }
        return advPerson;
    }

    public static boolean hasNotLoggedIn(
            HttpServletRequest request) {
        Object userRole = request.getSession().getAttribute(CC.user_role);
        return userRole == null || "".equals(userRole);
    }

    // to deal with the case like http://tuyi.sharethegoodones.com
    public static String getAppNameInRequest(
            HttpServletRequest request) {
        if (request.getRequestURL() == null) {
            TaoDebug.warn(TaoDebug.getSB(request.getSession()), "request.getRequestURL() menthod returned null!", "");
            return null;
        }

        String app_Name = request.getRequestURL().toString().toLowerCase();
        TaoDebug.info(TaoDebug.getSB(request.getSession()), "Client side url got in request is:{}", app_Name);
        int p = app_Name.indexOf(CC.DOMAIN_NAME);
        if (p > -1) {
            app_Name = app_Name.substring(0, p);

            if (app_Name.startsWith("http://")) {
                app_Name = app_Name.substring(7);
            }
            if (app_Name.startsWith("https://")) {
                app_Name = app_Name.substring(8);
            }
            if (!"".equals(app_Name) && !"www.".equals(app_Name) && !"test.".equals(app_Name)) {
                return app_Name.substring(0, app_Name.length() - 1);
            }
        } else {
            p = app_Name.indexOf(CC.DOMAIN_NAME_2);
            if (p > -1) {
                app_Name = app_Name.substring(0, p);

                if (app_Name.startsWith("http://")) {
                    app_Name = app_Name.substring(7);
                }
                if (app_Name.startsWith("https://")) {
                    app_Name = app_Name.substring(8);
                }
                if (!"".equals(app_Name) && !"www.".equals(app_Name) && !"test.".equals(app_Name)) {
                    return app_Name.toString();
                }
            }
        }
        return null;
    }

    private static void initializeForAdvPerson(
            Person person) {
        for (CC.customizes obj : CC.customizes.values()) {
            Customize customize = new Customize();
            customize.setCusKey(obj.name());
            customize.setCusValue(obj.getValue());
            customize.setPerson(person);
            customize.persist();
        }
    }

    private static int defaultNumOfMenu = 30;

    public static byte[] EMPTY_LINE = new byte[] { -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0,
            7, -48, 0, 0, 0, 1, 8, 6, 0, 0, 0, -122, -3, 53, 125, 0, 0, 0, 4, 103, 65, 77, 65, 0, 0, -79, -114, 124, -5,
            81, -109, 0, 0, 0, 32, 99, 72, 82, 77, 0, 0, 122, 37, 0, 0, -128, -125, 0, 0, -7, -1, 0, 0, -128, -24, 0, 0,
            117, 48, 0, 0, -22, 96, 0, 0, 58, -105, 0, 0, 23, 111, -105, -87, -103, -44, 0, 0, 0, 34, 73, 68, 65, 84,
            120, -100, -20, -63, 1, 1, 0, 0, 8, -128, -96, -6, 63, -38, -122, 4, 108, 53, 0, 0, 0, 0, 0, 0, 0, -16, -35,
            9, 48, 0, -93, 56, 2, -1, 29, 4, -21, -60, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126 };
    public static byte[] EMPTY_POINT = new byte[] { -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0,
            0, 1, 0, 0, 0, 1, 8, 6, 0, 0, 0, 31, 21, -60, -119, 0, 0, 0, 4, 103, 65, 77, 65, 0, 0, -79, -114, 124, -5,
            81, -109, 0, 0, 0, 32, 99, 72, 82, 77, 0, 0, 122, 37, 0, 0, -128, -125, 0, 0, -7, -1, 0, 0, -128, -24, 0, 0,
            117, 48, 0, 0, -22, 96, 0, 0, 58, -105, 0, 0, 23, 111, -105, -87, -103, -44, 0, 0, 0, 16, 73, 68, 65, 84,
            120, -100, 98, -8, -1, -1, 63, 3, 64, -128, 1, 0, 8, -4, 2, -2, 51, -93, -30, -103, 0, 0, 0, 0, 73, 69, 78,
            68, -82, 66, 96, -126 };

    public static boolean switchClient(
            HttpServletRequest request,
            String clientName) {
        // check the case if url is like http://tuyi.sharethegoodones.com
        String app_name_inRequest = TaoUtil.getAppNameInRequest(request);
        if (app_name_inRequest != null) {
            TaoDebug.info(TaoDebug.getSB(request.getSession()), "app_name found in request is:{}", app_name_inRequest);
            clientName = app_name_inRequest;
        }

        TaoDebug.info(request, "start to switchClient to client : {} from current person: {}", clientName);
        Person currentPerson = TaoUtil.getCurPerson(request);
        if (currentPerson != null && currentPerson.getName().equals(clientName)) {
            TaoDebug.info(TaoDebug.getSB(request.getSession()), "stopped switching, because they are same person!");
        } else {
            currentPerson = Person.findPersonByName(clientName);
            if (currentPerson != null) {
                request.getSession().setAttribute(CC.CLIENT, currentPerson);
                TaoUtil.reInitSession(request.getSession(), currentPerson);
                return true;
            } else { // means null in session and null in database.
                TaoDebug.warn(TaoDebug.getSB(request.getSession()), "no such a person in db:{}, will stop switching",
                        clientName);
            }
        }
        return false;
    }

    // PkeyString must be page persition like *_n_n_n...
    public static boolean isTHUMNeeded(
            HttpServletRequest request,
            String keyString,
            Person person) {
        if (keyString.indexOf('_') == 2) { // check if the key has lang prefix.
            keyString = keyString.substring(3);
        }
        if (keyString.startsWith("product_")) {
            return !(keyString.endsWith("_2d") || keyString.endsWith("_3d"));
        }

        Object isThumNeeded = request.getSession().getAttribute(CC.THUMNEEDED_ + keyString);
        if (isThumNeeded == null) {
            return keyString.startsWith("gallery_") || keyString.startsWith("service_");
        } else {
            return "YES".equalsIgnoreCase(isThumNeeded.toString());
        }
    }

    // PkeyString must be page persition like *_n_n_n...
    public static boolean isLangPrfNeededForMedia(
            HttpServletRequest request,
            String keyString) {
        if (keyString.indexOf('_') == 2) { // check if the key has lang prefix.
            keyString = keyString.substring(3);
        }
        Object tCustomize = request.getSession().getAttribute(CC.LANGUAGE_PRF_NEEDED_ + keyString);
        return tCustomize != null && "YES".equalsIgnoreCase(tCustomize.toString());
    }

    // so be noticed that the menu_1 must be for main page. if you have to make it have sub menu, then start from menu_1
    // the pkeyString can be stat with "menu_", but still can not be string like "en_menu_", or will cause confusing.
    public static void getIndexFromString(
            String pKeyString) {
        String[] idxs = pKeyString.split("_");
        int idx = 0;
        int tSubIdx = 0;
        int tSubSubIdx = 0;
        for (int i = 0; i < idxs.length; i++) {
            if (StringUtils.isNumeric(idxs[i])) {
                if (idx == 0)
                    idx = Integer.parseInt(idxs[i]);
                else if (tSubIdx == 0)
                    tSubIdx = Integer.parseInt(idxs[i]);
                else
                    tSubSubIdx = Integer.parseInt(idxs[i]);
            }
        }
    }

    public static String getContentType(
            HttpServletRequest request,
            int idx,
            int subIdx,
            int subSubIdx) {

        StringBuilder sb = new StringBuilder("CONTENTTYPE_" + idx);
        if (subIdx > 0) {
            sb.append("_" + subIdx);
            if (subSubIdx > 0)
                sb.append("_" + subSubIdx);
        }
        String tContentTypeKey = sb.toString();

        Object tCustomize = request.getSession().getAttribute(tContentTypeKey);

        while (tCustomize == null) {
            int pos = tContentTypeKey.lastIndexOf('_');
            if (pos < 0)
                break;

            tContentTypeKey = tContentTypeKey.substring(0, pos);
            tCustomize = request.getSession().getAttribute(tContentTypeKey);
        }

        return tCustomize == null ? CC.HTML : tCustomize.toString();
    }

    public static String getLangPrfWithDefault(
            HttpServletRequest request) {
        Object lang = request.getParameter(CC.LANG) != null ? request.getParameter(CC.LANG)
                : request.getSession().getAttribute(CC.LANG);

        if (lang == null) {
            return getDefalutLang(request) + "_";
        } else {
            return lang.toString() + "_";
        }
    }

    public static String getDefalutLang(
            HttpServletRequest request) {
        Object lang = null;
        lang = request.getSession().getAttribute(CC.DEFAULT_LANG);
        if (lang == null && "true".equals(request.getSession().getAttribute("en"))) {
            lang = "en";
        }
        if (lang == null && "true".equals(request.getSession().getAttribute("zh"))) {
            lang = "zh";
        }
        if (lang == null && "true".equals(request.getSession().getAttribute("fr"))) {
            lang = "fr";
        }
        if (lang == null && "true".equals(request.getSession().getAttribute("it"))) {
            lang = "it";
        }
        return lang == null ? "en" : lang.toString();
    }

    public static String initSubPage(
            HttpServletRequest request,
            Model model,
            String langPrf,
            int idx,
            int subIdx,
            int subSubIdx,
            Person person) {

        TaoDebug.info(request, "start to initLeftMenuBar for: menu_{}, for person: {}", idx);
        initLeftMenuBar(request, model, "menu_" + idx + "_", langPrf, person);
        String menuIdx = null;
        String returnPath = null;
        switch (getContentType(request, idx, subIdx, subSubIdx)) {
            case CC.HTML:
                TaoDebug.info(TaoDebug.getSB(request.getSession()),
                        "start to initHTMLSubPage for: submenu_{}, for client: {}", subIdx, person.getName());
                menuIdx = initHTMLSubPage(request, model, langPrf, idx, subIdx, subSubIdx, person);
                returnPath = "/generalHTMLPage";
                break;
            case CC.GALLERY:
                TaoDebug.info(TaoDebug.getSB(request.getSession()),
                        "start to initGallerySubPage for: submenu_{}, for client: {}", subIdx, person.getName());
                menuIdx = initGallerySubPage(request, model, langPrf, idx, subIdx, subSubIdx, person);
                returnPath = "/generalGalleryPage";
                break;
            case CC.CATALOG:
                TaoDebug.info(TaoDebug.getSB(request.getSession()),
                        "start to initCatalogSubPage for: submenu_{}, for client: {}", subIdx, person.getName());
                menuIdx = initCatalogSubPage(model, langPrf, idx, subIdx, subSubIdx, person);
                returnPath = "/generalCatalogPage";
                break;
            case CC.FEATURE:
                TaoDebug.info(TaoDebug.getSB(request.getSession()),
                        "start to initFeatureSubPage for: submenu_{}, for client: {}", subIdx, person.getName());
                menuIdx = initFeatureSubPage(request, model, langPrf, idx, subIdx, subSubIdx, person);
                returnPath = "/generalFeaturePage";
                break;
            case CC.PRODUCT:
                TaoDebug.info(TaoDebug.getSB(request.getSession()),
                        "start to initProductSubPage for: submenu_{}, for client: {}", subIdx, person.getName());
                menuIdx = initProductSubPage(model, langPrf, idx, subIdx, subSubIdx, person);
                returnPath = "/generalProductPage";
                break;
            case CC.SERVICE:
                TaoDebug.info(TaoDebug.getSB(request.getSession()),
                        "start to initServiceSubPage for: submenu_{}, for client: {}", subIdx,
                        request.getSession().getAttribute(CC.CLIENT));
                menuIdx = initServiceSubPage(request, model, langPrf, idx, subIdx, subSubIdx, person);
                returnPath = "/generalServicePage";
                break;
            case CC.LOCATION:
                TaoDebug.info(TaoDebug.getSB(request.getSession()),
                        "start to initServiceSubPage for: submenu_{}, for client: {}", subIdx,
                        request.getSession().getAttribute(CC.CLIENT));
                menuIdx = initLocationPage(request.getSession(), model, langPrf, idx, subIdx, subSubIdx, person);
                returnPath = "/contactus";
                break;
            default:
                TaoDebug.info(TaoDebug.getSB(request.getSession()),
                        "start to initModelUiForMainPage for: SUBmenu:{}, for client: {}", subIdx,
                        request.getSession().getAttribute(CC.CLIENT));
                menuIdx = initModelUiForMainPage(request, model, langPrf, idx, subIdx, subSubIdx, person);
                returnPath = "index";
        }

        request.getSession().setAttribute(CC.menuIdx, menuIdx);
        TaoDebug.info(request, "before return to page, returnPath:{}, person: {}", returnPath);

        return returnPath;
    }

    private static String initProductSubPage(
            Model model,
            String langPrf,
            int pMenuIdx,
            int pSubMenuIdx,
            int pSubSubMenuIdx,
            Person person) {

        String menuIdx = completeMenuIdx(pMenuIdx, pSubMenuIdx, pSubSubMenuIdx, langPrf, person);
        String key = "product_" + menuIdx;

        TextContent tContent = TextContent.findContentsByKeyAndPerson(langPrf + key + "_content", person);
        model.addAttribute("subpageContent", tContent == null ? "" : tContent.getContent());
        long serviceAmount = MediaUpload.countMediaUploadsByKeyAndPerson(key, person) / 2;
        model.addAttribute("serviceAmount", serviceAmount);

        model.addAttribute("descriptions", prepareDescriptions(langPrf + key, serviceAmount, person));
        return menuIdx;
    }

    private static String initFeatureSubPage(
            HttpServletRequest request,
            Model model,
            String langPrf,
            int pMenuIdx,
            int pSubMenuIdx,
            int pSubSubMenuIdx,
            Person person) {
        HttpSession session = request.getSession();
        String menuIdx = completeMenuIdx(pMenuIdx, pSubMenuIdx, pSubSubMenuIdx, langPrf, person);

        List<Feature> features = Feature.findAllFeaturesByMenuIdxAndPerson(menuIdx, person);
        if (features != null) {
            List<List<String>> imageKeyLists = new ArrayList<List<String>>();
            List<List<String>> descriptions = new ArrayList<List<String>>();
            List<List<String>> visibleStatusList = new ArrayList<List<String>>();
            List<String> refList = new ArrayList<String>();
            List<String> menus = new ArrayList<String>();
            String menuPRF = langPrf + "menu_";
            String selectedItems = (String) session.getAttribute(CC.selectedItems);
            for (Feature feature : features) {
                // get all visible images.
                String itemToShow = feature.getItemsToShow();
                String[] itemsStrs = StringUtils.split(itemToShow, ',');
                String ref = feature.getRefMenuIdx();
                refList.add(ref);
                String menuKey = menuPRF + ref;
                TextContent textContent = TextContent.findContentsByKeyAndPerson(menuKey, person);
                menus.add(textContent.getContent());
                List<String> visibleStatus = new ArrayList<String>();
                if (isAboveManager(session) || isPrinter(session)) {
                    List<String> imageKeys = MediaUpload.listAllMediaUploadsKeyByKeyAndPerson("service_" + ref, person);
                    imageKeys = TaoImage.stripThumOrderAndValidate(imageKeys);
                    imageKeyLists.add(imageKeys);
                    fillInDescriptions(langPrf, person, descriptions, imageKeys);

                    boolean isEmpty = true;
                    if (isPrinter(session)) {
                        UserAccount userAccount = (UserAccount) session.getAttribute(CC.currentUser);
                        String nameStrInService = "," + TaoEncrypt.stripUserName(userAccount.getLoginname()) + ",";

                        for (String item : imageKeys) {
                            if (item.startsWith("service_")) {
                                item = item.substring(8);
                            }
                            Service service = Service.findServiceByCatalogAndPerson(item, person);
                            if (service != null && service.getC3() != null) {
                                if (service.getC3().contains(nameStrInService)) {
                                    isEmpty = false;
                                    visibleStatus.add("1");
                                } else {
                                    visibleStatus.add(null);
                                }
                            } else {
                                visibleStatus.add(null);
                            }
                        }
                    } else {
                        for (String item : imageKeys) {
                            if (itemToShow.contains(item)) {
                                isEmpty = false;
                                visibleStatus.add("1");
                            } else {
                                visibleStatus.add(null);
                            }
                        }
                    }
                    visibleStatusList.add(isEmpty ? null : visibleStatus);
                } else {
                    List<String> imageKeys = Arrays.asList(itemsStrs);
                    imageKeyLists.add(imageKeys);
                    fillInDescriptions(langPrf, person, descriptions, imageKeys);
                    boolean isEmpty = true;
                    if (selectedItems != null) {
                        for (String item : imageKeys) {
                            item = "," + strip(item) + ",";
                            int number = countStr(item, selectedItems);
                            if (number > 0) {
                                isEmpty = false;
                                visibleStatus.add(String.valueOf(number));
                            } else {
                                visibleStatus.add(null);
                            }
                        }
                        visibleStatusList.add(isEmpty ? null : visibleStatus);
                    } else {
                        visibleStatusList.add(null);
                    }
                }
            }

            model.addAttribute("features", features);
            model.addAttribute("imageKeys", imageKeyLists);
            model.addAttribute("descriptions", descriptions);
            model.addAttribute("visibleStatusList", visibleStatusList);

            String featurePrf = langPrf + "feature_" + menuIdx + "_";
            List<TextContent> textContents =
                    TextContent.findAllMatchedTextContents(featurePrf + "%", CC.TextContentKey, person);
            List<String> groupTitles = new ArrayList<String>();
            for (TextContent textContent : textContents) {
                if (textContent.getPosInPage().endsWith("_groupTitle")) {
                    groupTitles.add(textContent.getContent());
                }
            }
            model.addAttribute("groupTitles", groupTitles);

            // available menu to remove.
            model.addAttribute("refForDelete", refList);
            model.addAttribute("menus", menus);
            model.addAttribute("show_status_message", session.getAttribute(langPrf + CC.show_status_message1));
        }

        if (isAboveManager(session)) {
            // available menu to ref.
            List<String> menusForRef = fetchAllMenuByType(CC.SERVICE, request, langPrf, person);
            model.addAttribute("menusForRef", menusForRef);
        }
        return menuIdx;
    }

    private static String strip(
            String key) {
        int i = key.indexOf('_');
        if (i > 3) {
            key = key.substring(i + 1);
        }
        return key;
    }

    public static List<String> fetchAllMenuByType(
            String menuType,
            HttpServletRequest request,
            String langPrf,
            Person person) {
        String menuKey = langPrf + "menu_";
        int posOfIdxStart = menuKey.length();
        List<String> menusForRef = TextContent.findAllMatchedContent(menuKey, CC.TextContentKey, person);
        for (int i = menusForRef.size() - 1; i >= 0; i--) {
            String menuStr = menusForRef.get(i).substring(posOfIdxStart);

            int[] menuIdxes = TaoUtil.splitMenuIdxToAry(menuStr);

            String contentTyep = TaoUtil.getContentType(request, menuIdxes[0], menuIdxes[1], menuIdxes[2]);
            if (!menuType.equals(contentTyep)) {
                menusForRef.remove(i);
            } else {
                menusForRef.set(i, menuStr);
            }
        }
        return menusForRef;
    }

    private static boolean isAboveManager(
            HttpSession session) {
        Object userRole = session.getAttribute(CC.user_role);
        if (userRole != null) {
            String role = userRole.toString();
            return role.contains(CC.ROLE_ADMIN) || role.contains(CC.ROLE_MANAGER);
        } else {
            return false;
        }
    }

    private static boolean isPrinter(
            HttpSession session) {
        Object userRole = session.getAttribute(CC.user_role);
        if (userRole != null) {
            String role = userRole.toString();
            return role.contains(CC.ROLE_PRINTER);
        } else {
            return false;
        }
    }

    public static void fillInDescriptions(
            String langPrf,
            Person person,
            List<List<String>> descriptions,
            List<String> imageKeys) {
        List<String> list = new ArrayList<String>();
        for (String key : imageKeys) {
            list.add(TextContent.findTextByKeyAndPerson(langPrf + key + "_description", person));
        }
        descriptions.add(list);
    }

    public static int[] splitMenuIdxToAry(
            String menuIdxString) {
        int[] menuIdxes = new int[3];
        for (int i = 0; i < 3; i++) {
            int septatorIdx = menuIdxString.indexOf("_"); // get the menuIdx
            if (septatorIdx > 0) {
                menuIdxes[i] = Integer.valueOf(menuIdxString.substring(0, septatorIdx));
                menuIdxString = menuIdxString.substring(septatorIdx + 1);
            } else {
                menuIdxes[i] = Integer.valueOf(menuIdxString.substring(0));
                break;
            }
        }
        return menuIdxes;
    }

    private static String initLocationPage(
            HttpSession session,
            Model model,
            String langPrf,
            int pMenuIdx,
            int pSubMenuIdx,
            int pSubSubMenuIdx,
            Person person) {
        String completeMenuIdx = completeMenuIdx(pMenuIdx, pSubMenuIdx, pSubSubMenuIdx, langPrf, person);
        TaoUtil.initSubpageContent(model, "location_" + completeMenuIdx, langPrf, person);
        TaoUtil.prepareContents(model, "text_Contact_Titles", langPrf, person);
        int length = TaoUtil.prepareContents(model, "text_Contact_Contents", langPrf, person);
        Object[] Xs = new Object[length];
        Object[] Ys = new Object[length];
        for (int i = 0; i < length; i++) {
            Xs[i] = session.getAttribute(CC.MAP_POS_X + i);
            Ys[i] = session.getAttribute(CC.MAP_POS_Y + i);
        }
        model.addAttribute(CC.MAP_POS_X, Xs);
        model.addAttribute(CC.MAP_POS_Y, Ys);

        return completeMenuIdx;
    }

    private static String initModelUiForMainPage(
            HttpServletRequest request,
            Model model,
            String langPrf,
            int pMenuIdx,
            int pSubMenuIdx,
            int pSubSubMenuIdx,
            Person person) {
        // "infratherapie""web-slim""christmas"
        TaoUtil.initTextArray(model, "text_on_slid", 10, langPrf, person);

        // "infratherapie""web-slim""christmas"
        TaoUtil.initTexts(model, "text_promotion", 3, langPrf, person);
        TaoUtil.initTexts(model, "text_firstLineTitle", 3, langPrf, person);
        TaoUtil.initTexts(model, "text_firstLineContent", 3, langPrf, person);
        TaoUtil.initTexts(model, "text_subTitle", 4, langPrf, person);
        TaoUtil.initTexts(model, "text_categoryTitle", 4, langPrf, person);
        TaoUtil.initTexts(model, "text_categoryContent", 4, langPrf, person);
        TaoUtil.initTexts(model, "text_LogTitle", 3, langPrf, person);
        TaoUtil.initTexts(model, "text_logContent", 3, langPrf, person);

        model.addAttribute("slide_superBig_amount", findIndexOfTheLastSlidPicture(request, person, langPrf));
        String completedMenuIdx = completeMenuIdx(pMenuIdx, pSubMenuIdx, pSubSubMenuIdx, langPrf, person);
        TaoDebug.info(request, "start to return completedMenuIdx : {}, for person: {}", completedMenuIdx);
        return completedMenuIdx;
    }

    // initialise the left bar menu and main content.
    private static String initHTMLSubPage(
            HttpServletRequest request,
            Model model,
            String langPrf,
            int pMenuIdx,
            int pSubMenuIdx,
            int pSubSubMenuIdx,
            Person person) {

        String menuIdx = completeMenuIdx(pMenuIdx, pSubMenuIdx, pSubSubMenuIdx, langPrf, person);
        String prf2 = "html_" + menuIdx;

        TaoDebug.info(TaoDebug.getSB(request.getSession()), "start to initHTMLSubPage, prf2 is {}, langPrf is {}", prf2,
                langPrf);
        initSubpageContent(model, prf2, langPrf, person);
        return menuIdx;
    }

    /**
     * menuIdx's format must be like x_y_z, x_y_0 or x_0_0, because later will use like to find out all matching images.
     * 
     * @param pMenuIdx
     * @param pSubMenuIdx
     * @param pSubSubMenuIdx
     * @param langPrf
     * @param person
     * @return
     */
    public static String completeMenuIdx(
            int pMenuIdx,
            int pSubMenuIdx,
            int pSubSubMenuIdx,
            String langPrf,
            Person person) {
        String menuIdx = pMenuIdx + "_";
        if (pSubMenuIdx == 0) {
            pSubMenuIdx = hasSubMenu(langPrf, pMenuIdx, 0, person); // which one is the first not null item.
        }

        menuIdx = menuIdx + pSubMenuIdx + "_";
        if (pSubMenuIdx > 0) {
            if (pSubSubMenuIdx > 0) { // if it's initialising a sub page of sub menu, return relevant page directly.
                menuIdx = menuIdx + pSubSubMenuIdx;
            } else {
                int tIdx = hasSubMenu(langPrf, pMenuIdx, pSubMenuIdx, person); // which one is the first not null item.
                menuIdx = menuIdx + tIdx; // if it's the sub menu clicked, then check if it has sub sub menu, return
                                          // the page of first sub sub menu.
            }
        } else {
            menuIdx = menuIdx + "0";
        }

        return menuIdx;
    }

    // initialise the left bar menu and main content.
    private static String initGallerySubPage(
            HttpServletRequest request,
            Model model,
            String langPrf,
            int pMenuIdx,
            int pSubMenuIdx,
            int pSubSubMenuIdx,
            Person person) {

        String menuIdx = completeMenuIdx(pMenuIdx, pSubMenuIdx, pSubSubMenuIdx, langPrf, person);
        String key = "gallery_" + menuIdx;

        key = TaoUtil.isLangPrfNeededForMedia(request, key) ? langPrf + key : key;
        // for gallery should always divided by 2, because it always has thumb.
        long pictureAmount = MediaUpload.countMediaUploadsByKeyAndPerson(key, person) / 2;
        model.addAttribute("pictureAmount", pictureAmount);

        TaoDebug.info(TaoDebug.getSB(request.getSession()),
                "start to initGallerySubPage, key is {}, pictureAmount is {}", key, pictureAmount);
        return menuIdx;
    }

    // initialise the left bar menu and main content.
    private static String initCatalogSubPage(
            Model model,
            String langPrf,
            int pMenuIdx,
            int pSubMenuIdx,
            int pSubSubMenuIdx,
            Person person) {

        String menuIdx = completeMenuIdx(pMenuIdx, pSubMenuIdx, pSubSubMenuIdx, langPrf, person);
        String key = "catalog_" + menuIdx;

        // List tProducts = Product.findProductsByCatalogAndPerson(key, person);
        //
        // tProducts = languageRelevantProcess(tProducts, langPrf);
        //
        // model.addAttribute("products", tProducts);
        // model.addAttribute("productAmount", tProducts.size());

        // TaoDebug.info(TaoDebug.getSB(request.getSession()), "start to initCatalogSubPage, menukey is {},
        // productAmount is {}", key, tProducts.size());
        return menuIdx;
    }

    // initialise the left bar menu and main content.
    private static String initServiceSubPage(
            HttpServletRequest request,
            Model model,
            String langPrf,
            int pMenuIdx,
            int pSubMenuIdx,
            int pSubSubMenuIdx,
            Person person) {
        String menuIdx = completeMenuIdx(pMenuIdx, pSubMenuIdx, pSubSubMenuIdx, langPrf, person);
        String key = "service_" + menuIdx;

        TextContent tContent = TextContent.findContentsByKeyAndPerson(langPrf + key + "_title", person);
        model.addAttribute("subpageContent", tContent == null ? "" : tContent.getContent());

        long serviceAmount = MediaUpload.countMediaUploadsByKeyAndPerson(key, person) / 2;
        model.addAttribute("serviceAmount", serviceAmount);

        model.addAttribute("descriptions", prepareDescriptions(langPrf + key, serviceAmount, person));

        // the selection status
        HttpSession session = request.getSession();
        if (!isAboveManager(session)) {
            List<String> visibleStatus = new ArrayList<String>();
            if (isPrinter(session)) {
                List<String> imageKeys = MediaUpload.listAllMediaUploadsKeyByKeyAndPerson(key, person);
                imageKeys = TaoImage.stripThumOrderAndValidate(imageKeys);

                boolean isEmpty = true;
                UserAccount userAccount = (UserAccount) session.getAttribute(CC.currentUser);
                String nameStrInService = "," + TaoEncrypt.stripUserName(userAccount.getLoginname()) + ",";

                for (String item : imageKeys) {
                    if (item.startsWith("service_")) {
                        item = item.substring(8);
                    }
                    Service service = Service.findServiceByCatalogAndPerson(item, person);
                    if (service != null && service.getC3() != null) {
                        if (service.getC3().contains(nameStrInService)) {
                            isEmpty = false;
                            visibleStatus.add("1");
                        } else {
                            visibleStatus.add(null);
                        }
                    } else {
                        visibleStatus.add(null);
                    }
                }

                model.addAttribute("visibleStatusList", isEmpty ? null : visibleStatus);
            } else {
                String selectedItems = (String) session.getAttribute(CC.selectedItems);
                if (selectedItems != null) {
                    List<String> visibleStatusList = new ArrayList<String>();
                    boolean isEmpty = true;
                    for (int i = 1; i <= serviceAmount; i++) {
                        String item = "," + menuIdx + "_" + i + ",";
                        int number = countStr(item, selectedItems);
                        if (number > 0) {
                            isEmpty = false;
                            visibleStatusList.add(String.valueOf(number));
                        } else {
                            visibleStatusList.add(null);
                        }
                    }
                    model.addAttribute("visibleStatusList", isEmpty ? null : visibleStatusList);
                }
            }
        }

        model.addAttribute("show_status_message", session.getAttribute(langPrf + CC.show_status_message1));
        TaoDebug.info(TaoDebug.getSB(request.getSession()),
                "completed initServiceSubPage, menukey is {}, serviceAmount is {}", key, serviceAmount);
        return menuIdx;
    }

    private static int countStr(
            String item,
            String selectedItems) {
        int i = 0;
        int p = selectedItems.indexOf(item);
        while (p > -1) {
            i++;
            selectedItems = selectedItems.substring(p + item.length() - 1);
            p = selectedItems.indexOf(item);
        }
        return i;
    }

    // to decide which part in name and description should be displayed according to the given language.
    public static List<MultiLanguageabl> languageRelevantProcess(
            List<MultiLanguageabl> pMultLanItems,
            String tLanguage) {

        if (tLanguage == null || "null".equals(tLanguage))
            tLanguage = "en_"; // in case that no language has been selected.

        if (!tLanguage.trim().endsWith("_"))
            tLanguage = tLanguage.trim() + "_"; // in case that "en" has been inputted as parameter.

        // set the name_display of the products, so it can display a language relevant name.
        for (int i = pMultLanItems.size() - 1; i >= 0; i--) {
            String tName = pMultLanItems.get(i).getName();
            String tDescript = pMultLanItems.get(i).getDescription();
            String[] tNames = tName.split("~");
            String[] tDescripts = tDescript.split("~");

            boolean matched = false;
            for (int j = 0; j < tNames.length; j++) {
                if (tNames[j].trim().toLowerCase().startsWith(tLanguage.toLowerCase())) {
                    pMultLanItems.get(i).setLocalName(tNames[j].trim().substring(3));
                    matched = true;
                    break; // go and work on next product.
                }
            }
            if (!matched) // no match, then use the first name.
                pMultLanItems.get(i).setLocalName(tNames[0].indexOf("_") == 2 ? tNames[0].substring(3) : tNames[0]);

            matched = false;
            for (int j = 0; j < tDescripts.length; j++) {
                if (tDescripts[j].trim().toLowerCase().startsWith(tLanguage.toLowerCase())) {
                    pMultLanItems.get(i).setLocalDescription(tDescripts[j].trim().substring(3));
                    matched = true;
                    break; // go and work on next product.
                }
            }
            if (!matched) // no match, then use the first name.
                pMultLanItems.get(i).setLocalDescription(
                        tDescripts[0].indexOf("_") == 2 ? tDescripts[0].substring(3) : tDescripts[0]);
        }
        return pMultLanItems;
    }

    private static List<String> prepareDescriptions(
            String key,
            long serviceAmount,
            Person person) {
        List<String> descriptions = new ArrayList<String>();
        HashMap<String, String> map = new HashMap<String, String>();
        List<TextContent> list = TextContent.findAllMatchedTextContents(key + "_%_description", null, person);
        for (TextContent textContent : list) {
            String posInPage = textContent.getPosInPage();
            if (posInPage.endsWith("_description")) {
                posInPage = posInPage.substring(0, posInPage.length() - 12);
                posInPage = posInPage.substring(posInPage.lastIndexOf('_') + 1);
                map.put(posInPage, textContent.getContent());
            }
        }
        for (int i = 0; i < serviceAmount; i++) {
            descriptions.add(map.get(String.valueOf(i + 1)));
        }
        return descriptions;
    }

    private static int findIndexOfTheLastSlidPicture(
            HttpServletRequest request,
            Person person,
            String langPrf) {
        int biggestNumber = 0;
        String slide_superBig = TaoUtil.isLangPrfNeededForMedia(request, "slide_superBig") ? langPrf + "slide_superBig"
                : "slide_superBig";
        List<MediaUpload> mediaUploads = MediaUpload.listAllMediaUploadsByKeyAndPerson(slide_superBig, person);
        for (MediaUpload mediaUpload : mediaUploads) {
            String filePath = mediaUpload.getFilepath();
            int index = Integer.valueOf(filePath.substring(filePath.lastIndexOf('_') + 1));

            biggestNumber = biggestNumber > index ? biggestNumber : index;
        }
        return biggestNumber;
    }

    /**
     * to check if a sub menu has sub sub menu.
     * 
     * @NOTE: for database, we use menu_x_y_z as key to get the string displayed on menu out. while in left bar, we can
     *        not use menu_x_y_z directly, because we can not use ${'menu_" + menuIdx + "_y_z" to generate the key
     *        string out. so, for work out: we used subMenu_x_y_z as key of model property instead of key of database to
     *        transfer the string on sub menu and "sub sub menu". and in back end, we can generate the key whatever we
     *        want.
     */
    private static int hasSubMenu(
            String pLanguage,
            int pMenuIdx,
            int pSubMenuIdx,
            Person person) {
        String tKey = pLanguage + "menu_" + pMenuIdx + "_";
        if (pSubMenuIdx != 0) {
            tKey = tKey + pSubMenuIdx + "_";
        }

        for (int i = 1; i <= defaultNumOfMenu; i++) {
            TextContent tContent = TextContent.findContentsByKeyAndPerson(tKey + i, person);
            if (tContent != null) {
                return i;
            }
        }
        return 0;
    }

    public static void reInitSession(
            HttpSession session,
            Person person) {
        // clean current configuration
        TaoDebug.info(TaoDebug.getSB(session), "start to reInitSession for client : {}", person.getName());
        if (session.getAttribute(CC.CLIENT) != null) {
            cleanSessionAttributes(session);
        }
        // Initialise with advPerson's configuration.
        List<Customize> customizes = Customize.findAllCustomizesByPerson(TaoUtil.getAdvPerson());
        for (Customize customize : customizes) {
            session.setAttribute(customize.getCusKey(), customize.getCusValue());
        }
        // overwrite with user's special configuration.
        if (!TaoUtil.getAdvPerson().getName().equals(person.getName())) {
            removeSensitiveDemoConfigurations(session);
            customizes = Customize.findAllCustomizesByPerson(person);

            TaoDebug.info(session, "start to add special attribute from person : {}");
            for (Customize customize : customizes) {
                session.setAttribute(customize.getCusKey(), customize.getCusValue());
            }
        }
        session.setAttribute(CC.CLIENT, person);
        TaoDebug.setDebugFlag((String) session.getAttribute(CC.debugFlag), person);
        session.setMaxInactiveInterval(-1);
    }

    private static void cleanSessionAttributes(
            HttpSession session) {
        TaoDebug.info(TaoDebug.getSB(session), "start to cleanSessionAttributes");

        Enumeration<String> enumeration = session.getAttributeNames();
        while (enumeration.hasMoreElements())
            session.removeAttribute(enumeration.nextElement());
    }

    private static void removeSensitiveDemoConfigurations(
            HttpSession session) {
        TaoDebug.info(TaoDebug.getSB(session), "start to removeSensitiveDemoConfigurations.");
        session.removeAttribute(CC.app_ContentManager);
        session.removeAttribute("app_ManagerEmail");
        session.removeAttribute("app_name");
        session.removeAttribute(CC.app_WebsiteAddress);
        session.removeAttribute("flash_1_URL");
        for (int i = 1; i < 10; i++) {
            session.removeAttribute("MAP_POS_X" + i);
            session.removeAttribute("CONTENTTYPE_" + i);
        }
    }

    /**
     * for left menu bar, it doesn't care about which menu nor which language, it always display the 100 value from
     * controller. which have key from leftMenuText_1~leftMenyText_10, and each one has 10 sub
     * leftMenuText_n_1~leftMenuText_n_10. for building the link to url, it need menuIdx which will be set into the
     * model by relevant controller.
     * 
     * @param: pKey
     *             are supposed to be like "menu_[n]_" .
     */
    private static void initLeftMenuBar(
            HttpServletRequest request,
            Model model,
            String pKey,
            String pLanguage,
            Person person) {
        TaoDebug.info(TaoDebug.getSB(request.getSession()), "start to initLeftMenuBar, pKey is: {}, pLang is {}", pKey,
                pLanguage);
        List<List<String>> subMenu = prepareMenuContent(pKey, pLanguage, person);
        model.addAttribute("subMenu", subMenu);
        int indexOfSecondDash = pKey.indexOf('_', 5);
        model.addAttribute("topIdx", pKey.substring(0, indexOfSecondDash + 1));
    }

    public static List<List<String>> prepareMenuContent(
            String keyPrf,
            String langPrf,
            Person person) {
        List<List<String>> listForReturn = new ArrayList<List<String>>();
        for (int i = 1; i <= defaultNumOfMenu; i++) {
            List<String> subMenus = new ArrayList<String>();
            if (!keyPrf.endsWith("_")) {
                keyPrf = keyPrf + "_";
            }
            String tKey = keyPrf + i;
            TextContent tContent = TextContent.findContentsByKeyAndPerson(langPrf + tKey, person);
            if (tContent != null && !"".equals(tContent.getContent())) {
                subMenus.add(tContent.getContent());
                if (!"".equals(tContent.getContent().trim())) {
                    for (int j = 1; j <= 30; j++) {
                        tContent = TextContent.findContentsByKeyAndPerson(langPrf + tKey + "_" + j, person);
                        if (tContent != null && StringUtils.isNotBlank(tContent.getContent())) {
                            for (int pos = subMenus.size(); pos < j; pos++) {
                                subMenus.add("");
                            }
                            subMenus.add(tContent.getContent());
                        }
                    }
                }
            }

            if (subMenus.size() != 0) {
                for (int pos = listForReturn.size() + 1; pos < i; pos++) {
                    listForReturn.add(new ArrayList<String>());
                }
                listForReturn.add(subMenus);
            }
        }
        return listForReturn;
    }

    private static void initSubpageContent(
            Model model,
            String key,
            String langPrf,
            Person person) {
        TextContent tContent = TextContent.findContentsByKeyAndPerson(langPrf + key + "_content", person);
        if (tContent != null)
            model.addAttribute("subpageContent", tContent.getContent());
        else
            model.addAttribute("subpageContent", "");
    }

    public static void initTexts(
            Model model,
            String pKey,
            int pNum,
            String LangPrf,
            Person person) {
        for (int i = 0; i <= pNum; i++) {
            String tKey = pKey + i;
            TextContent tContent = TextContent.findContentsByKeyAndPerson(LangPrf + tKey, person);
            if (tContent != null)
                model.addAttribute(tKey, tContent.getContent());
            else
                model.addAttribute(tKey, "");
        }
    }

    public static int prepareContents(
            Model model,
            String pKey,
            String LangPrf,
            Person person) {
        List<TextContent> textContents = TextContent.findAllMatchedTextContents(LangPrf + pKey + "_%", null, person);
        if (textContents != null) {
            String[] contents = new String[20];
            for (TextContent textContent : textContents) {
                String key = textContent.getPosInPage();
                int p = key.lastIndexOf('_');
                int i = Integer.valueOf(key.substring(p + 1));
                contents[i] = textContent.getContent();
            }
            Object[] validAry = vacuumArray(contents);
            if (validAry != null) {
                model.addAttribute(pKey, validAry);
                return validAry.length;
            } else {
                model.addAttribute(pKey, new String[0]);
                return 0;
            }
        }
        return 0;
    }

    public static Object[] vacuumArray(
            Object[] contents) {
        for (int i = contents.length - 1; i >= 0; i--) {
            if (contents[i] != null) {
                Object[] a = new Object[i + 1];
                System.arraycopy(contents, 0, a, 0, a.length);
                return a;
            }
        }
        return null;
    }

    public static void initText(
            HttpSession session,
            String pKey,
            int pNum,
            String langPrf,
            Person person) {
        for (int i = 1; i <= pNum; i++) {
            String tKey = pKey + i;
            TextContent tContent = TextContent.findContentsByKeyAndPerson(langPrf + tKey, person);
            if (tContent != null)
                session.setAttribute(tKey, tContent.getContent());
            else
                session.setAttribute(tKey, "");
        }
    }

    public static void initTextArray(
            Model model,
            String pKey,
            int pNum,
            String pLanguage,
            Person person) {
        String[] strArray = new String[pNum];
        for (int i = 0; i < pNum; i++) {
            String tKey = pKey + "_" + (i + 1);
            TextContent tContent = TextContent.findContentsByKeyAndPerson(pLanguage + tKey, person);
            if (tContent != null)
                strArray[i] = tContent.getContent();
            else
                strArray[i] = "";
        }
        model.addAttribute(pKey, strArray);
    }

    // will return 1st:current 2nd:get from_app_name 3nd:for_demo
    public static Person getCurPerson(
            HttpServletRequest request) {
        Person person = (Person) request.getSession().getAttribute(CC.CLIENT);
        if (person == null) {
            // trying to save it back--we normally have a copy of CC.client's value in app_name.
            Object app_name = request.getSession().getAttribute(CC.app_name);
            if (app_name != null) {
                person = Person.findPersonByName(app_name.toString());
            }
            // if the app_name property in session also changed, then have to use adv person :(
            if (person == null) {
                person = TaoUtil.getAdvPerson(); // This could have caused a issue.
                TaoDebug.warn(TaoDebug.getSB(request.getSession()),
                        "no name found in both CLIENT and app_name, have to get AdvPerson", "");
            }
            request.getSession().setAttribute(CC.CLIENT, person);
        }
        return person;
    }

    public static String fetchProductName(
            String description,
            String moneyLetter) {
        if (StringUtils.isBlank(description)) {
            return "";
        }
        String html = description.substring(0, description.indexOf(moneyLetter));
        return trimContentFromHTML(html);
    }

    public static String fetchProductPrice(
            String description,
            String moneyLetter) {
        if (StringUtils.isBlank(description)) {
            return "";
        }
        String html = description.substring(description.lastIndexOf(moneyLetter) + 1);
        return trimContentFromHTML(html);
    }

    // return the trimed content from html format text.
    public static String trimContentFromHTML(
            String html) {
        StringBuilder sb = new StringBuilder();
        while (true) {
            int start = html.indexOf("<");
            int end = html.indexOf(">");
            if (start < 0) {
                break;
            } else if (end > start) {
                sb.append(html.substring(0, start));
                html = html.substring(end + 1);
            }
        }

        html = sb.toString() + html;
        while (html.startsWith("&nbsp;")) {
            html = html.substring(6).trim();
        }
        while (html.endsWith("&nbsp;")) {
            html = html.substring(0, html.length() - 6).trim();
        }

        return html;
    }

    public static String formateDate(
            HttpServletRequest request,
            Date date) {
        if (date == null) {
            return null;
        }
        Object dateFormate = request.getSession().getAttribute("DateFormat");
        String format = dateFormate != null ? dateFormate.toString() : "hh:mm";
        SimpleDateFormat df5 = new SimpleDateFormat(format, Locale.getDefault());
        return df5.format(date);
    }

    public static String cleanUpTel(
            String tel) {
        tel = StringUtils.remove(tel, " ");
        tel = StringUtils.remove(tel, "(");
        tel = StringUtils.remove(tel, ")");
        tel = StringUtils.remove(tel, "-");
        tel = StringUtils.remove(tel, ".");
        tel = StringUtils.remove(tel, ",");

        tel = StringUtils.remove(tel, " ");
        tel = StringUtils.remove(tel, "（");
        tel = StringUtils.remove(tel, "）");
        tel = StringUtils.remove(tel, "—");
        tel = StringUtils.remove(tel, "。");
        tel = StringUtils.remove(tel, ",");
        return tel;
    }
}
