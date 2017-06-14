package com.stgo.taostyle.web;

import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;
import org.hsqldb.lib.HashMap;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.stgo.taostyle.backend.security.TaoEncrypt;
import com.stgo.taostyle.backend.security.UserContextService;
import com.stgo.taostyle.config.spring.SpringApplicationContext;
import com.stgo.taostyle.domain.Cart;
import com.stgo.taostyle.domain.Customize;
import com.stgo.taostyle.domain.Feature;
import com.stgo.taostyle.domain.MediaUpload;
import com.stgo.taostyle.domain.Person;
import com.stgo.taostyle.domain.Product;
import com.stgo.taostyle.domain.Service;
import com.stgo.taostyle.domain.TextContent;
import com.stgo.taostyle.domain.UserAccount;
import com.stgo.taostyle.domain.orders.MainOrder;
import com.stgo.taostyle.domain.orders.Material;
import com.stgo.taostyle.domain.orders.TaxonomyMaterial;

@Controller
public class MainPageController extends BaseController {

    @Inject
    private UserContextService userContextService;

    // @Inject
    // private ServerInfo serverInfo; //we don't want to save the image to datafolder anymore.

    // /* in case some client wnat to display the login on main page.*/
    // @RequestMapping(value = "/admin",method = RequestMethod.GET)
    // public String admin(Model model, HttpServletRequest request) {
    // model.addAttribute("display_loginForm", "true");
    // return index(model, request);
    // }

    // ==================================view part=====================================
    /**
     * Entry 1 normal user normal visiting goes to here.
     */
    @RequestMapping(value = "/{identity}")
    public String clientComing(
            Model model,
            HttpServletRequest request,
            @PathVariable("identity")
            String identity) {
        TaoDebug.info("Entry 1, start to switching user to {}:", identity);
        String returnString = systemCommandCheck(model, request, identity);
        if (returnString != null) {
            TaoDebug.info("it's a system command, returning string : {}", identity);
            return returnString;
        }
        if (hasNotLoggedIn(request)) {
            dirtFlagCommonText = TaoUtil.switchClient(request, identity);
        } else {
            TaoDebug.info("it's in login status, stop switching user: {}", identity);
        }

        return showFromFlashpage(model, request);
    }

    private boolean hasNotLoggedIn(
            HttpServletRequest request) {
        Object userRole = request.getSession().getAttribute(CC.user_role);
        return userRole == null || "".equals(userRole);
    }

    /**
     * Entry2 visiting directly into a page instead of visiting from beginning. Didn't use full menu key string, because
     * don't want other request like "/" and "/login" to go here.
     */
    @RequestMapping(value = "/{client}/menu_{menuIdxString}")
    public String showWebPageWithClientInfo(
            Model model,
            HttpServletRequest request,
            @PathVariable("client")
            String client,
            @PathVariable("menuIdxString")
            String menuIdxString) {
        TaoDebug.info("Entry2, start to showWebPageWithClientInfo client: {}, menuIdxString:{}", client, menuIdxString);
        if (hasNotLoggedIn(request)) {
            dirtFlagCommonText = TaoUtil.switchClient(request, client);
        }
        return buildPageForMenu(model, request, menuIdxString);
    }

    /**
     * Entry 3
     * 
     * @param model
     * @param request
     * @param client
     * @param systemCommand
     * @return
     */
    @RequestMapping(value = "/{client}/{systemCommand}")
    public String responseSystemCommand(
            Model model,
            HttpServletRequest request,
            @PathVariable("client")
            String client,
            @PathVariable("systemCommand")
            String systemCommand) {

        TaoDebug.info("Entry3, start to responseSystemCommand client: {}, systemCommand:{}", client, systemCommand);
        if (hasNotLoggedIn(request))
            dirtFlagCommonText = TaoUtil.switchClient(request, client);

        String returnString = systemCommandCheck(model, request, systemCommand);
        if (returnString != null)
            return returnString;

        return "login";
    }

    /**
     * default user normal visiting goes to here directly, others reach here from setClient().
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showFromFlashpage(
            Model model,
            HttpServletRequest request) {
        makesureSessionInitialized(request);// if first time visit, session is till empty, then initialise it.

        Object flash_1_URL = request.getSession().getAttribute(CC.flash_1_URL);

        TaoDebug.info("Entry4, showFromFlashpage, flash_1_URL is: {}", flash_1_URL);

        if (flash_1_URL != null && !StringUtils.isBlank(flash_1_URL.toString()))
            return "flashpage";
        else
            return buildPageForMenu(model, request, null);// if No Flash paged set, then got to main page directly.
    }

    /**
     * Didn't use full menu key string, because don't want other request like "/" and "/login" to go here.
     * 
     * @param model
     * @param request
     * @param menuIdxString
     * @return
     */
    @RequestMapping(value = "/menu_{menuIdxString}")
    public String buildPageForMenu(
            Model model,
            HttpServletRequest request,
            @PathVariable("menuIdxString")
            String menuIdxString) {
        TaoDebug.info(request, "Entry5, start to buildWebPage under {}, for person: {}, path:{}", menuIdxString);
        makesureSessionInitialized(request);

        String langPrf = TaoUtil.getLangPrfWithDefault(request);
        Person person = TaoUtil.getCurPerson(request);
        makesureCommonTextInitialized(model, request, langPrf, person);

        // get every number out from the menuIndex which looks like: 2_1_1 or 2_1 or 2
        if (menuIdxString == null) {
            Object menuIdx = request.getSession().getAttribute(CC.menuIdx);
            menuIdxString = menuIdx == null ? "1" : menuIdx.toString();
        }
        int[] menuIdxes = TaoUtil.splitMenuIdxToAry(menuIdxString);
        if (menuIdxes[1] != 0) {
            model.addAttribute("currentSubMenu", "accordion-element-" + menuIdxes[1]);
        }

        TaoDebug.info(request, "start to initSubPage for: menu_{}, for person: {}, path:{}", menuIdxString);
        return TaoUtil.initSubPage(request, model, langPrf, menuIdxes[0], menuIdxes[1], menuIdxes[2], person);
    }

    // to make sure it is initialised. first time visit or the connection suffered disconnection.
    private void makesureSessionInitialized(
            HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object app_name = session.getAttribute(CC.app_name);
        if (app_name == null || session.getAttribute(CC.CLIENT) == null) {
            Person person = TaoUtil.getCurPerson(request);
            TaoDebug.info("start to makesureSessionInitialized, cur person is: {}", person.getName());
            TaoUtil.reInitSession(request.getSession(), person);
        }

        String tableID = request.getParameter("t");
        session.setAttribute(CC.tableID, tableID);
    }

    // ================================user management==================================
    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signupForm(
            Model model,
            HttpServletRequest request) {
        List<MainOrder> mainOrders = MainOrder.findMainOrderByCON(request.getSession().getId());
        model.addAttribute("mainOrderList", mainOrders);
        return "signup";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String signup(
            Model model,
            HttpServletRequest request) {
        String loginname = request.getParameter("loginname");
        if (CC.ADMIN.equalsIgnoreCase(loginname)) {
            Person person = new Person();

            if (Person.findPersonByName(CC.ADMIN) == null) { // if not exist, then create the super user.
                person.setName(CC.ADMIN);
            } else { // user the password as name to create a new client.
                String userName = request.getParameter("password");
                if (Person.findPersonByName(userName) != null) {
                    model.addAttribute("SignUp_ErrorMessage", "This name is occupied, please try an other one!");
                    return "signup";
                } else if (userName.contains(CC.SAVED_NAME_STR)) {
                    model.addAttribute("SignUp_ErrorMessage", "'*' is not allowed, please try an other one!");
                    return "signup";
                } else {
                    person.setName(userName);
                }
            }
            person.setPassword(TaoEncrypt.encryptPassword(request.getParameter("password")));
            person.persist();
            initializeToStyle1(request, person);

            String tel = request.getParameter("tel");
            if (!StringUtils.isBlank(tel)) {
                createACustomize(request, "contact_phone", tel.trim(), person);
            }

            String email = request.getParameter("");
            if (!StringUtils.isBlank(email)) {
                createACustomize(request, "email", email.trim(), person);
            }

            createACustomize(request, "app_WebsiteAddress", "http://www.shareTheGoodOnes.com/" + person.getName(),
                    person);

        } else {
            if (loginname.contains(CC.SAVED_NAME_STR)) {
                model.addAttribute("SignUp_ErrorMessage", "'*' is not allowed in name, please choose an other one.");
                return "signup";
            }
            Person person = TaoUtil.getCurPerson(request);
            if (person == null) {
                model.addAttribute("SignUp_ErrorMessage", "session closed, please refresh page and try again!");
                return "signup";
            }
            // same with client is not allowed, otherwise when login, don't know which one.
            if (person.getName().equalsIgnoreCase(loginname)) {
                model.addAttribute("SignUp_ErrorMessage", "This name is occupied, please try an other one!");
                return "signup";
            }

            String enrichedName = TaoEncrypt.enrichName(loginname, person);
            // make sure no existing
            if (UserAccount.findUserAccountByName(enrichedName) != null) {
                model.addAttribute("SignUp_ErrorMessage", "This name is occupied, please try an other one!");
                return "signup";
            }

            UserAccount tUserAccount = new UserAccount();
            tUserAccount.setPerson(person);
            tUserAccount.setLoginname(enrichedName);
            String password = request.getParameter("password");
            tUserAccount.setPassword(TaoEncrypt.encryptPassword(password));
            tUserAccount.setTel(request.getParameter("tel"));
            tUserAccount.setEmail(request.getParameter("email"));
            tUserAccount.setCompanyname(request.getParameter("companyname"));
            tUserAccount.setSecuritylevel(CC.ROLE_CLIENT);
            tUserAccount.persist();
        }
        return buildPageForMenu(model, request, null);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(
            Model model,
            HttpServletRequest request) {
        Person person = TaoUtil.getCurPerson(request);
        String loginname = request.getParameter("loginname");
        if (person.getName().equalsIgnoreCase(loginname)) {
            model.addAttribute("SignUp_ErrorMessage", "This name is occupied, please try an other one!");
            return "signup";
        }

        String email = request.getParameter("email");
        if (StringUtils.isBlank(loginname)) {
            loginname = email;
        }

        String password = request.getParameter("password");
        password = StringUtils.isEmpty(password) ? person.getName() : password;
        String enrichedLoginName = TaoEncrypt.enrichName(request, loginname);
        if (UserAccount.findUserAccountByName(enrichedLoginName) != null) {
            model.addAttribute("SignUp_ErrorMessage", "This name is occupied, please try an other one!");
            return "signup";
        } else {
            UserAccount tUserAccount = new UserAccount();
            tUserAccount.setPerson(person);
            tUserAccount.setLoginname(enrichedLoginName);
            tUserAccount.setPassword(TaoEncrypt.encryptPassword(password));
            tUserAccount.setTel(request.getParameter("tel"));
            tUserAccount.setEmail(email);
            tUserAccount.setCompanyname(request.getParameter("companyname"));
            tUserAccount.setSecuritylevel(CC.ROLE_CLIENT);
            tUserAccount.persist();
            model.addAttribute("email", tUserAccount.getEmail());
            model.addAttribute("loginname", loginname);
        }
        // send out an email.
        Object managerEmail = request.getSession().getAttribute(CC.app_ManagerEmail);
        if (managerEmail != null) {
            String content =
                    "<p>Thank you for registering with us.</p> <p>Please be noticed that your login name with us is :<b>"
                            + loginname
                            + "</b></p><p> Your temporal password is :<b>"
                            + password
                            + "</b></p><p>You can login and modify your password anytime. </p><p>Thanks!</p><p align='center'> ----Customer Care Center</p>";
            TaoEmail.sendMessage(managerEmail.toString(), "Register Conformation!", email, content, null);
        }
        // notice the user go and check their email box.
        return buildPageForMenu(model, request, null);
    }

    @RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
    public String updateUserInfo(
            Model model,
            HttpServletRequest request) {

        Long id = Long.valueOf(request.getParameter("id"));
        UserAccount tUserAccount = UserAccount.findUserAccount(id);
        String loginname = request.getParameter("loginname");
        if (loginname.contains(CC.SAVED_NAME_STR)) {
            model.addAttribute("Update_ErrorMessage", "'*' is not allowed in name, please choose an other one!");
            return SpringApplicationContext.getApplicationContext()
                    .getBean("userAccountController", UserAccountController.class)
                    .updateForm(tUserAccount.getId(), model);// with error message.
        }

        String enrichedName = TaoEncrypt.enrichName(request, loginname);
        if (!enrichedName.equalsIgnoreCase(tUserAccount.getLoginname())
                && UserAccount.findUserAccountByName(enrichedName) != null) {
            model.addAttribute("Update_ErrorMessage", "The name is already used, please choose an other one!");
            return SpringApplicationContext.getApplicationContext()
                    .getBean("userAccountController", UserAccountController.class)
                    .updateForm(tUserAccount.getId(), model);// with error message.
        }
        tUserAccount.setLoginname(enrichedName);

        String password = request.getParameter("password");
        if (!tUserAccount.getPassword().equals(password)) {
            password = TaoEncrypt.encryptPassword(password);
        }
        tUserAccount.setPassword(password);

        tUserAccount.setTel(request.getParameter("tel"));
        tUserAccount.setFax(request.getParameter("fax"));
        tUserAccount.setEmail(request.getParameter("email"));
        tUserAccount.setCompanyname(request.getParameter("companyname"));
        if (request.getParameter("address") != null) {
            tUserAccount.setCel(request.getParameter("cel"));
            tUserAccount.setAddress(request.getParameter("address"));
            tUserAccount.setCity(request.getParameter("city"));
            tUserAccount.setPostcode(request.getParameter("postcode"));
            tUserAccount.setDescription(request.getParameter("description"));
        }
        if (request.getParameter("securitylevel") != null)
            tUserAccount.setSecuritylevel(request.getParameter("securitylevel"));

        try {
            tUserAccount.setCredit(request.getParameter("credit") == null ? 0 : Integer.valueOf(request
                    .getParameter("credit")));
            tUserAccount.setBalance(request.getParameter("balance") == null ? 0 : Integer.valueOf(request
                    .getParameter("balance")));
        } catch (Exception e) {
            model.addAttribute("Update_ErrorMessage",
                    "Error Occured when trying to update the infomation into database, Please check if the NUMBERs are Valid!");
            return SpringApplicationContext.getApplicationContext()
                    .getBean("userAccountController", UserAccountController.class)
                    .updateForm(tUserAccount.getId(), model);// with error message.
        }

        tUserAccount.persist();
        return buildPageForMenu(model, request, null);
    }

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String dashboard(
            Model model,
            HttpServletRequest request) {
        String tUserName = userContextService.getCurrentUserName();
        Person person = TaoUtil.getCurPerson(request);
        UserAccount userAccount = UserAccount.findUserAccountByName(TaoEncrypt.enrichName(tUserName, person));
        // @NOTE: if beautify the name here, then when the following mainOrder.persist() excuted, useraccount's
        // loginName will be updated into db. DKW!
        // userAccount.setLoginname(TaoEncrypt.stripUserName(tUserName));
        model.addAttribute("useraccount", userAccount);
        model.addAttribute("itemId", userAccount.getId());
        model.addAttribute("todoList", MediaUpload.findMediaByAudientAndPerson(userAccount, person));
        model.addAttribute("infoList", MediaUpload.findMediaByAuthor(userAccount));
        List<MainOrder> mainOrders = null;
        String securityLevel = userAccount.getSecuritylevel();
        if (CC.ROLE_MANAGER.equalsIgnoreCase(securityLevel)) {
            mainOrders = MainOrder.findMainOrdersByPerson(person, "desc");
        } else if (CC.ROLE_PRINTER.equalsIgnoreCase(securityLevel)) {
            request.setAttribute("show_footArea", "");
            request.setAttribute("show_foot", "false");
            request.setAttribute("show_AboveMenu", "");
            request.setAttribute("show_Menu", "");

            mainOrders = MainOrder.findMainOrdersByStatusAndPerson(CC.STATUS_TO_PRINT, person, "asc");
            if (mainOrders != null) {
                for (MainOrder mainOrder : mainOrders) {
                    String returnPath = showDetailOrder(mainOrder, model, request);
                    if (CC.STATUS_FULL.equals(returnPath)) {
                        mainOrder.setRecordStatus(CC.STATUS_PRINTED);
                        mainOrder.persist(); // DKW! when this excuted, useraccount will be persist into db.
                        continue;
                    } else if (CC.STATUS_MINE_ARE_FULL.equals(returnPath)) {
                        continue;
                    } else {
                        return returnPath;
                    }
                }
            }
            // if not return yet, means nothing to print for now.
            model.addAttribute("materials", null);
            return "printpreview";
        } else if (CC.ROLE_CLIENT.equalsIgnoreCase(securityLevel)) {
            mainOrders = MainOrder.findMainOrdersByClientAndPerson(userAccount, person, "desc");
        } else {
            mainOrders = MainOrder.findUnCompletedMainOrdersByPerson(person, "desc");
        }
        model.addAttribute("mainOrderList", mainOrders);

        return "dashboard";
    }

    // shouldn't use "$" in dencity, should use an specific column for "$".
    // we have to user material, instead of service, because it's a record, will be there even if the product deleted.
    @RequestMapping(value = "/showDetailOrder/{mainOrderID}", method = RequestMethod.GET)
    public String showDetailOrder(
            @PathVariable("mainOrderID")
            Long mainOrderId,
            Model model,
            HttpServletRequest request) {
        if (mainOrderId > -1) {
            MainOrder mainOrder = MainOrder.findMainOrder(mainOrderId);
            return showDetailOrder(mainOrder, model, request);
        } else {
            model.addAttribute("materials", null);
            return "printpreview";
        }
    }

    private String showDetailOrder(
            MainOrder mainOrder,
            Model model,
            HttpServletRequest request) {
        if (mainOrder != null) {
            UserAccount currentUser = (UserAccount) request.getSession().getAttribute(CC.currentUser);

            List<Material> materials = null;
            if (currentUser != null && CC.ROLE_PRINTER.equals(currentUser.getSecuritylevel())) {
                String curPrinterName = "," + TaoEncrypt.stripUserName(currentUser.getLoginname()) + ",";
                String processedPrinters = mainOrder.getColorCard();
                boolean processed = processedPrinters != null && processedPrinters.contains(curPrinterName);
                materials = Material.findAllMaterialsByMainOrder(mainOrder);
                boolean isAllFull = true;
                for (int i = materials.size() - 1; i >= 0; i--) {
                    Material material = materials.get(i);
                    int status = material.getRecordStatus();
                    if (status < (CC.LEVEL_FULL - 5)) {
                        isAllFull = false;
                    }
                    if (!processed) {
                        String printersStr = material.getMenFu();
                        if (printersStr == null || !printersStr.contains(curPrinterName)) {
                            materials.remove(material);
                        }
                    }
                }
                if (isAllFull) {
                    return CC.STATUS_FULL;
                } else if (processed) {
                    return CC.STATUS_MINE_ARE_FULL;
                }
            } else {
                materials = Material.findAllMaterialsByMainOrder(mainOrder);
            }

            model.addAttribute("materials", materials);

            model.addAttribute("sizeTable", mainOrder.getSizeTable());
            model.addAttribute("mainOrderID", mainOrder.getId());
            model.addAttribute("targetTime", TaoUtil.formateDate(request, mainOrder.getDelieverdate()));
            model.addAttribute("contactPhone", mainOrder.getClient());// actually client's cellphone
            model.addAttribute("total", mainOrder.getPayCondition());
            if (currentUser != null) {
                model.addAttribute("printStyle", currentUser.getFax());
            }
        } else {
            model.addAttribute("materials", null);
        }
        return "printpreview";
    }

    @RequestMapping(value = "/careerApply", method = RequestMethod.GET)
    public String careerApplyForm(
            Model model,
            HttpServletRequest request) {
        Person person = TaoUtil.getCurPerson(request);
        TaoUtil.initTexts(model, "text_on_career_", 1, TaoUtil.getLangPrfWithDefault(request), person);
        return "careerApply";
    }

    // this is for normal careerApply, user regest first then past/upload resume. while it's not used now because the
    // client need a specific way.
    @RequestMapping(value = "/careerApply", method = RequestMethod.POST)
    public String careerApply(
            Model model,
            HttpServletRequest request) {
        String tUserName = userContextService.getCurrentUserName();
        // if not throgh the main page, then the menu may not initialized yet.
        if (request.getSession().getAttribute("menuContent") == null) {
            return "redirect:/menu_1";
        }

        UserAccount userAccount = UserAccount.findUserAccountByName(TaoEncrypt.enrichName(request, tUserName));

        String content =
                "<p align='center'><a href='http://localhost/taostyle/dashboard'>" + "check uploaded resume" + "</a>";

        TaoEmail.sendMessage("tao@sharethegoodones.com", "Career Application", userAccount.getEmail(),
                request.getParameter("content") + content, null);

        model.addAttribute(
                "notice",
                "Application is submitted successfully! Please feel free go on uploading more documents, or input in the editor directly.");
        return "careerApply";
    }

    @RequestMapping(value = "/sendEmail", method = RequestMethod.GET)
    public String sendEmailForm(
            Model model,
            HttpServletRequest request) {
        Person person = TaoUtil.getCurPerson(request);
        TaoUtil.initTexts(model, "text_on_email_", 1, TaoUtil.getLangPrfWithDefault(request), person);
        return "sendEmail";
    }

    @RequestMapping(value = "/sendEmail", method = RequestMethod.POST)
    public String sendEmail(
            Model model,
            HttpServletRequest request) {
        Person person = TaoUtil.getCurPerson(request);
        Object managerEmail = request.getSession().getAttribute(CC.app_ManagerEmail);
        if (managerEmail != null) {
            List<UserAccount> userAccounts = UserAccount.findAllUserAccountsByPerson(person);
            for (UserAccount userAccount : userAccounts) {
                String email = userAccount.getEmail();
                if (!StringUtils.isBlank(email)) {
                    try {
                        TaoEmail.sendMessage(managerEmail.toString(), request.getParameter("subject"), email,
                                request.getParameter("content"), null);
                    } catch (Exception e) {
                        System.out.println("Exception when sending email to :" + email);
                    }
                }
            }
        }
        return "redirect:/menu_1";
    }

    // =============================changing the text on page===========================
    @RequestMapping(value = "/changeContentForm", method = RequestMethod.GET)
    public String changeContentForm(
            Model model,
            HttpServletRequest request) {
        Person person = TaoUtil.getCurPerson(request);

        String key = request.getParameter("position");
        String langPrf = TaoUtil.getLangPrfWithDefault(request);

        model.addAttribute("isRichText", "true");
        model.addAttribute("position", key);
        TextContent textContent = TextContent.findContentsByKeyAndPerson(langPrf + key, person);
        model.addAttribute("content", textContent == null ? "" : textContent.getContent());
        return "richEditor";
    }

    @RequestMapping(value = "/changeTextForm", method = RequestMethod.POST)
    public String changeTextForm(
            Model model,
            HttpServletRequest request) {
        model.addAttribute("position", request.getParameter("position"));
        model.addAttribute("replacingContent", request.getParameter("content"));
        return buildPageForMenu(model, request, null);
    }

    @RequestMapping(value = "/changeText", method = RequestMethod.POST)
    public String changeText(
            Model model,
            HttpServletRequest request) {
        // Note: to make it stronger, if the language sign already in the positionString, remove it.
        String tPosStr = request.getParameter("position");
        if (tPosStr.indexOf("_") == 2) // @notice: this mean we can not use any word with length of 2 exception for
                                       // language, or will cause bug.
            tPosStr = tPosStr.substring(3);

        Person person = TaoUtil.getCurPerson(request);
        String key = TaoUtil.getLangPrfWithDefault(request) + tPosStr;
        TextContent tContent = TextContent.findContentsByKeyAndPerson(key, person);
        if (tContent == null) {
            tContent = new TextContent();
            tContent.setPerson(person);
            tContent.setPosInPage(key);
        }
        String content = request.getParameter("content");
        if (isContentValid(request, tPosStr, content, person)) {
            tContent.setContent(content);
            tContent.persist();
        }
        // in case the text in common area modified, and language not modified, so it will not update.
        checkIfDirtFlagNeeded(tPosStr);
        return buildPageForMenu(model, request, null);
    }

    private boolean isContentValid(
            HttpServletRequest request,
            String posStr,
            String description,
            Person person) {
        Object need_calculate_price = request.getSession().getAttribute("need_calculate_price");
        if ("true".equals(need_calculate_price) && (posStr.startsWith("service_") || posStr.startsWith("product_"))
                && posStr.endsWith("_description")) {
            String langPrf = TaoUtil.getLangPrfWithDefault(request);
            String moneyLetter = CC.money.valueOf(langPrf.substring(0, 2)).getValue();

            String productName = TaoUtil.fetchProductName(description, moneyLetter);
            if (StringUtils.isBlank(productName)) {
                return false;
            }
            String payCondition = TaoUtil.fetchProductPrice(description, moneyLetter);
            try {
                Float.valueOf(payCondition);
            } catch (Exception e) {
                return false;
            }

            // if it's valid, then update service and product record.
            if (posStr.startsWith("service_")) {
                posStr = posStr.substring(8);
                if (posStr.endsWith("_description")) {
                    posStr = posStr.substring(0, posStr.length() - 12);
                }
                Service service = Service.findServiceByCatalogAndPerson(posStr, person);
                service.setName(productName);
                service.setDescription(payCondition);
                service.persist();
            } else if (posStr.startsWith("product_")) {
                posStr = posStr.substring(8);
                if (posStr.endsWith("_description")) {
                    posStr = posStr.substring(0, posStr.length() - 12);
                }
                Product product = Product.findProductByCatalogAndPerson(posStr.substring(8), person);
                product.setName(productName);
                product.setDescription(payCondition);
                product.persist();
            }
        }
        return true;
    }

    /*
     * currently only menu, and footer need to update common text.
     */
    private void checkIfDirtFlagNeeded(
            String key) {
        if (key.startsWith("menu_") || key.startsWith("footer_") || key.startsWith("text_footContent")) {
            dirtFlagCommonText = true;
        }
    }

    // =============================changing images on page=============================
    @RequestMapping(value = "/{client}/getImage/{id}")
    public void getImage(
            @PathVariable("client")
            String client,
            @PathVariable("id")
            String id,
            HttpServletRequest request,
            HttpServletResponse response) {
        TaoDebug.info(request, "start to getImage, client : {}, id:{}, person:{}, path:{}", client, id);
        // specially for some explore like safari on iphone........
        // TODO:may be can remove now, we use taoutil.getCurPerson now.
        if (request.getSession().getAttribute(CC.CLIENT) == null) {
            Person person = Person.findPersonByName(client);
            request.getSession().setAttribute(CC.CLIENT, person);
        }
        requestForImage(id, request, response);
    }

    @RequestMapping(value = "/getImage")
    public void getImage(
            HttpServletRequest request) {
        Person person = TaoUtil.getCurPerson(request);
        if (person == null) {
            TaoDebug.info(request, "***Error***caught a 'getImage' request, person is null, person is:{}, path:{}");
        } else {
            TaoDebug.info(request, "***Error***caught a 'getImage' request, person is {}, path:{}");
        }
    }

    @RequestMapping(value = "/uncaughtException")
    public void uncaughtException(
            HttpServletRequest request) {
        TaoDebug.info(request, "***Error***caught a 'uncaughtException', person : {}, path{}");
    }

    @RequestMapping(value = "/getImage/{filePath}")
    public void requestForImage(
            @PathVariable("filePath")
            String filePath,
            HttpServletRequest request,
            HttpServletResponse response) {

        Person person = TaoUtil.getCurPerson(request);
        if (person == null) { // some explore like safari could happen.
            TaoDebug.info(request, "***Error***unexpectecd non client in getImage! person:{} ,path:{}");
        }
        filePath =
                TaoUtil.isLangPrfNeededForMedia(request, filePath) ? TaoUtil.getLangPrfWithDefault(request) + filePath
                        : filePath;
        TaoDebug.info("start to findMediaByKeyAndPerson, person : {}, filePath:{}, path:{}", person, filePath,
                request.getContextPath());
        MediaUpload tMedia = MediaUpload.findMediaByKeyAndPerson(filePath, person);
        try {
            if (tMedia != null && tMedia.getContent() != null) {
                byte[] imageBytes = tMedia.getContent();
                response.getOutputStream().write(imageBytes);
                response.getOutputStream().flush();

            } else {
                byte[] imageBytes = filePath.startsWith("menu_") ? TaoUtil.EMPTY_LINE : TaoUtil.EMPTY_POINT;
                response.getOutputStream().write(imageBytes);
                response.getOutputStream().flush();

                // String pathToWeb = request.getServletPath();
                // File tFile = new File();
                //
                // BufferedImage bi = ImageIO.read(tFile);
                // OutputStream out = response.getOutputStream();
                // ImageIO.write(bi, "jpg", out);
                // out.close();
            }
        } catch (Exception e) {
            System.out.println("Exception occured when fetching img of ID:" + filePath + "! " + e);
        }
    }

    @RequestMapping(value = "/changeImgForm", method = RequestMethod.POST)
    public String changeImgForm(
            Model model,
            HttpServletRequest request) {
        model.addAttribute("position", request.getParameter("position"));
        model.addAttribute("imageIndex", request.getParameter("imageIndex"));
        model.addAttribute("operation", request.getParameter("operation"));

        if (StringUtils.isBlank(request.getParameter("operation"))) {
            model.addAttribute("form_url_img", CC.singleUploadUri);
        } else {
            model.addAttribute("form_url_img", CC.multiUploadUri);
        }

        model.addAttribute("mediaUpload", new MediaUpload());
        // return index(model, request);
        return buildPageForMenu(model, request, null);
    }

    // hard code here, because As we know only the 2.3,4,5 are disigned to display galarry.
    @RequestMapping(value = CC.singleUploadUri, method = RequestMethod.POST, produces = "text/html")
    public String createSingleItem(
            @Valid
            MediaUpload mediaUpload,
            BindingResult bindingResult,
            Model model,
            @RequestParam("content")
            CommonsMultipartFile[] contents,
            HttpServletRequest request) {
        CommonsMultipartFile content = contents[0];
        String keyString = request.getParameter("position"); // can be category, big-slid, product's pratNo...
        FileItem tFileItem = content.getFileItem();
        Person person = TaoUtil.getCurPerson(request);

        // check size, no big file allowed except for administrator.----------------------
        String returnPageOnError = checkSize(model, keyString, tFileItem, person);
        if (returnPageOnError != null) {
            return returnPageOnError;
        }

        // Initialise the meiaUpload.
        String tKeyStr =
                TaoUtil.isLangPrfNeededForMedia(request, keyString) ? TaoUtil.getLangPrfWithDefault(request)
                        + keyString : keyString;
        String fileName = tFileItem.getName();
        MediaUpload media = getOrInitializeMediaUpload(fileName, tKeyStr, person);

        // check if it's delete command.---------------------------------------------------
        if (checkIfIsDeleteCommand(fileName, media)) { // delete a image not allowed in service :(
            if (!keyString.startsWith("service_"))
                media.remove();
            return buildPageForMenu(model, request, null);
        }

        // ------------if need to save to disk-------------
        // File tDiskFile = new File(serverInfo.getDataPath() + tKeyString + tFormat);
        // content.transferTo(tDiskFile);

        processFileType(fileName, media);

        // save file or image to db----------------------------------------------------------------
        String pageOnError = null;
        if ("personalDocument".equals(keyString) || "emailBackGround".equals(keyString)) { // save document to db
            pageOnError = saveDocumentToDB(model, content, fileName, media, keyString, person);
        } else if ("careerApply".equals(keyString)) { // save document to db
            TaoEmail.sendJobApplyEmail(content, request, person);
            return saveJobApplicationToDB(model, content, fileName, media, request, person);
        } else if (keyString.contains("service_")) {
            String keyForTempralStopThum = CC.THUMNEEDED_ + tKeyStr;
            request.getSession().setAttribute(keyForTempralStopThum, "false");
            pageOnError = null;
            try {
                pageOnError = saveImageToDB(model, content, fileName, media, tKeyStr, request);
            } finally {
                request.getSession().removeAttribute(keyForTempralStopThum);
            }
        } else {// save image to db
            pageOnError = saveImageToDB(model, content, fileName, media, tKeyStr, request);
        }

        if ("logo".equals(keyString)) {
            List<MediaUpload> mediaUploads = MediaUpload.listAllMediaUploadsByKeyAndPerson("QRCode_", person);
            if (mediaUploads != null) {
                Object obj = request.getSession().getAttribute(CC.app_WebsiteAddress);
                if (obj != null) {
                    String url = obj.toString();

                    url = url.endsWith("/") ? url + "menu" : url + "/menu";
                    for (MediaUpload oldQRCode : mediaUploads) {
                        String fildPath = oldQRCode.getFilepath();
                        generateQRCode(null, fildPath, url + fildPath.substring(6), person);
                    }
                }
            }
        }
        return pageOnError != null ? pageOnError : buildPageForMenu(model, request, null);
    }

    // hard code here, because As we know only the 2.3,4,5 are disigned to display galarry.
    @RequestMapping(value = CC.multiUploadUri, method = RequestMethod.POST, produces = "text/html")
    public String createMultiItem(
            @Valid
            MediaUpload mediaUpload,
            BindingResult bindingResult,
            Model model,
            @RequestParam("content")
            CommonsMultipartFile[] contents,
            HttpServletRequest request) {

        Person person = TaoUtil.getCurPerson(request);
        // if idle too long time, the person will be null;
        if (person == null) {
            return buildPageForMenu(model, request, null);
        }

        String langPrf = TaoUtil.getLangPrfWithDefault(request);
        String keyString = request.getParameter("position");
        keyString = checkValidation(keyString, langPrf, person);
        String operation = request.getParameter("operation");

        String tKeyStr = TaoUtil.isLangPrfNeededForMedia(request, keyString) ? langPrf + keyString : keyString;

        List<MediaUpload> mediaUploads = MediaUpload.listAllMediaUploadsByKeyAndPerson(tKeyStr, person);
        long countOfExistingImage = mediaUploads != null ? mediaUploads.size() : 0;
        countOfExistingImage =
                TaoUtil.isTHUMNeeded(request, tKeyStr, person) ? TaoImage.validateThumNeededImageCount(tKeyStr,
                        mediaUploads) : countOfExistingImage;

        if (countOfExistingImage < 0) {
            return buildPageForMenu(model, request, null);
        }

        // !!!to be same with the number index of programming is also start from 1.
        long imageIndex;
        try {
            imageIndex = Integer.valueOf(request.getParameter("imageIndex"));
        } catch (Exception e) {
            imageIndex = countOfExistingImage;
        }
        if (imageIndex > countOfExistingImage) {
            imageIndex = countOfExistingImage;
        }

        if (CC.APPEND.equals(operation)) {
            imageIndex++;
            boolean isThumNeeded = TaoUtil.isTHUMNeeded(request, tKeyStr, person);
            TaoImage.makeRoomForNewImages(tKeyStr, imageIndex, countOfExistingImage, contents.length, isThumNeeded,
                    person);
        }

        boolean needCleanUp = false;
        long i = imageIndex;
        for (CommonsMultipartFile content : contents) {

            FileItem tFileItem = content.getFileItem();
            // check size, no big file allowed except for administrator.----------------------
            // while front end should already checked the size.....
            // String returnPageOnError = checkSize(uiModel, tKeyStr, tFileItem, person);
            // if (returnPageOnError != null) {
            // return returnPageOnError;
            // }

            String fileName = tFileItem.getName();

            MediaUpload media = initializeMediaUpload(fileName, tKeyStr + "_" + i, person);
            // only when selected replace, the delete magic word will work.
            if (CC.OVERWRITE.equals(operation) && checkIfIsDeleteCommand(fileName, media)) {
                media.remove();
                if (TaoUtil.isTHUMNeeded(request, tKeyStr, media.getPerson())) {
                    MediaUpload mediaThumb =
                            MediaUpload.findMediaByKeyAndPerson(media.getFilepath() + "_thum", media.getPerson());
                    if (mediaThumb != null) {
                        mediaThumb.remove();
                    }
                }
                // delete popup text.
                List<TextContent> textContents =
                        TextContent.findAllMatchedTextContents("%_" + media.getFilepath(), null, person);
                if (textContents != null) {
                    for (TextContent textContent : textContents) {
                        textContent.remove();
                    }
                }
                // delete description text.
                textContents =
                        TextContent.findAllMatchedTextContents("%_" + media.getFilepath() + "_description", null,
                                person);
                if (textContents != null) {
                    for (TextContent textContent : textContents) {
                        textContent.remove();
                    }
                }
                // service and product
                if (tKeyStr.startsWith("service_")) {
                    Service service = Service.findServiceByCatalogAndPerson(tKeyStr.substring(8), person);
                    if (service != null) {
                        service.remove();
                    }
                } else if (tKeyStr.startsWith("product_")) {
                    Product product = Product.findProductByCatalogAndPerson(tKeyStr.substring(8), person);
                    if (product != null) {
                        product.remove();
                    }
                }

                needCleanUp = true;
            } else {
                // ------------if need to save to disk-------------
                // File tDiskFile = new File(serverInfo.getDataPath() + tKeyString + tFormat);
                // content.transferTo(tDiskFile);

                processFileType(fileName, media);
                // save images to db----------------------------------------------------------------
                saveImageToDB(model, content, fileName, media, tKeyStr, request);

                // service and product
                if (tKeyStr.startsWith("service_")) {
                    Service service = new Service();
                    service.setC1(tKeyStr.substring(8) + "_" + i);
                    service.setPerson(person);
                    service.persist();
                } else if (tKeyStr.startsWith("product_")) {
                    Product product = new Product();// .findProductByCatalogAndPerson(tKeyStr.substring(8), person);
                    product.setC1(tKeyStr.substring(8) + "_" + i);
                    product.setPerson(person);
                    product.persist();
                }
            }
            i++;
        }

        if (needCleanUp) {
            TaoImage.vacumGellaryImages(person, tKeyStr);
        }
        return buildPageForMenu(model, request, null);
    }

    private String checkValidation(
            String key,
            String langPrf,
            Person person) {
        StringBuilder sb = new StringBuilder();
        String[] strings = StringUtils.split(key, '_');
        int[] menuIdxes = new int[3];
        for (int i = 0; i < strings.length; i++) {
            try {
                int number = Integer.valueOf(strings[i]);
                menuIdxes[0] = number;
                try {
                    menuIdxes[1] = Integer.valueOf(strings[i + 1]);
                    menuIdxes[2] = Integer.valueOf(strings[i + 2]);
                } catch (Exception e) {
                    // do nothing
                }
                break;
            } catch (Exception e) {
                sb.append(strings[i]);
                sb.append('_');
            }
        }
        sb.append(TaoUtil.completeMenuIdx(menuIdxes[0], menuIdxes[1], menuIdxes[2], langPrf, person));
        return sb.toString();
    }

    private MediaUpload getOrInitializeMediaUpload(
            String tFileName,
            String keyStr,
            Person person) {
        MediaUpload media = null;

        // the types that always generate new ones.
        if (!(keyStr.contains("personalDocument") || keyStr.contains("careerApply") || keyStr
                .contains("emailBackGround"))) {
            media = MediaUpload.findMediaByKeyAndPerson(keyStr, person);
        }

        return media != null ? media : createNewMediaUpload(keyStr, person);
    }

    private MediaUpload initializeMediaUpload(
            String tFileName,
            String keyStr,
            Person person) {

        MediaUpload tMedia = MediaUpload.findMediaByKeyAndPerson(keyStr, person);
        if (tMedia == null) {
            tMedia = createNewMediaUpload(keyStr, person);
        }

        return tMedia;
    }

    private MediaUpload createNewMediaUpload(
            String keyStr,
            Person person) {
        MediaUpload media;
        media = new MediaUpload();
        media.setPerson(person);
        media.setFilepath(keyStr);
        return media;
    }

    private boolean checkIfIsDeleteCommand(
            String fileName,
            MediaUpload media) {
        return fileName.startsWith(CC.cloak) && media.getId() != null;
    }

    private void processFileType(
            String tFileName,
            MediaUpload media) {
        media.setContentType(CC.DEFAULT_IMAGE_TYPE);
        int tPos = tFileName.lastIndexOf(".");
        if (tPos > 0 && tPos < tFileName.length() - 1 && tPos >= tFileName.length() - 5) {
            media.setContentType(tFileName.substring(tPos));
        }
    }

    private String saveImageToDB(
            Model uiModel,
            CommonsMultipartFile content,
            String fileName,
            MediaUpload media,
            String tKeyStr,
            HttpServletRequest request) {

        uiModel.asMap().clear();

        String tFormat = media.getContentType();

        BufferedImage inputImage = null;
        try {
            inputImage = ImageIO.read(content.getInputStream());
            // Image big = inputImage.getScaledInstance(256, 256,Image.SCALE_DEFAULT);

            BufferedImage bufferedImage =
                    ".png".equalsIgnoreCase(tFormat) ? inputImage : TaoImage.resizeImage(request, inputImage,
                            media.getFilepath()); // because when uploading gallery,
                                                  // the tKeyString is like
                                                  // gallery_".
            byte[] tContent = TaoImage.getByteFormateImage(bufferedImage, tFormat);
            media.setContent(tContent == null ? content.getBytes() : tContent);

            media.setFilesize(tContent == null ? content.getSize() : tContent.length);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("got exception when resizing the image!" + e);
            return buildPageForMenu(uiModel, request, null);
        }
        media.persist();

        // --------------------------for Thum image---------------------------
        if (TaoUtil.isTHUMNeeded(request, tKeyStr, media.getPerson())) {
            String filPathForThumb = media.getFilepath() + "_thum";

            MediaUpload mediaThumb = MediaUpload.findMediaByKeyAndPerson(filPathForThumb, media.getPerson());
            if (mediaThumb == null) {
                mediaThumb = new MediaUpload();
                mediaThumb.setPerson(media.getPerson());
                mediaThumb.setFilepath(filPathForThumb);
            }

            try {
                // Image big = inputImage.getScaledInstance(256, 256,Image.SCALE_DEFAULT);
                inputImage = TaoImage.resizeImage(request, inputImage, filPathForThumb);
                byte[] tContent = TaoImage.getByteFormateImage(inputImage, tFormat);

                mediaThumb.setContent(tContent); // ------------file content----------
                mediaThumb.setFilesize(tContent.length); // ------------file size-------------
                mediaThumb.setContentType(tFormat);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("got exception when resizing the Thumimage!" + e);
            }

            mediaThumb.persist();
        }
        return null;
    }

    private String saveDocumentToDB(
            Model uiModel,
            CommonsMultipartFile content,
            String fileName,
            MediaUpload media,
            String keyString,
            Person person) {
        if (StringUtils.isBlank(fileName)) {
            uiModel.addAttribute("notice", "Nothing will be uploaded because of no file was selected!");
            return "redirect:/dashboard";
        }
        // audient: leave to be null. all mediaUpload with publisher will be visible by GE anyway.
        // String pUserName = session.getAttribute(CC.app_ContentManager);
        // tMedia.setAudient(UserAccount.findUserAccountByName(pUserName));
        // publisher
        String tUserName = userContextService.getCurrentUserName();
        UserAccount userAccount = UserAccount.findUserAccountByName(TaoEncrypt.enrichName(tUserName, person));
        media.setPublisher(userAccount);
        // submitDate
        media.setSubmitDate(new Date());
        // filePath
        String tFilePath = TaoEncrypt.stripUserName(tUserName) + "_";
        int pos = fileName.indexOf("_");
        if (pos > 0) {
            String authorName = fileName.substring(0, pos);
            UserAccount author = UserAccount.findUserAccountByName(TaoEncrypt.enrichName(authorName, person));
            if (author != null) {
                tFilePath = ""; // the file name is already starts with a users name, then do not add name on to it.
            }
        }
        tFilePath = tFilePath + fileName;
        pos = tFilePath.indexOf(".");// remove the type from file path.
        if (pos > 0)
            tFilePath = tFilePath.substring(0, pos);

        pos = tFilePath.indexOf("_V");// remove the version from file path.
        if (pos > 0 && StringUtils.isNumeric(tFilePath.substring(pos + 2)))
            tFilePath = tFilePath.substring(0, pos);

        long tNum = MediaUpload.countMediaUploadsByKeyAndPerson(tFilePath, person);
        if (tNum > 0) {
            tFilePath = tFilePath + "_V" + tNum;
        }
        media.setFilepath(tFilePath);
        // status
        media.setStatus(CC.STATUS_NEW);
        // content and fileSize
        media.setContent(content != null ? content.getBytes() : null); // ------------file content----------
        media.setFilesize(content != null ? content.getSize() : 0); // ------------file size-------------
        media.setPerson(person);
        media.persist();

        uiModel.asMap().clear();
        if ("personalDocument".equals(keyString)) {
            return "redirect:/dashboard";
        } else {
            uiModel.addAttribute(
                    "notice",
                    fileName
                            + "is submitted successfully! Please feel free uploading more documents, or input in the editor directly.");
            return "sendEmail";
        }
    }

    private String saveJobApplicationToDB(
            Model uiModel,
            CommonsMultipartFile content,
            String fileName,
            MediaUpload media,
            HttpServletRequest request,
            Person person) {

        // always create new user, in case some one mistyped with client's email, thus, data will be ruined.
        UserAccount userAccount = createANewUserForJobApplier(request, person);
        media.setPublisher(userAccount);

        // leave audient to be null. all mediaUpload with publisher will be visible by GE anyway.
        // String pUserName = session.getAttributee(CC.app_ContentManager);
        // tMedia.setAudient(UserAccount.findUserAccountByName(pUserName));

        media.setSubmitDate(new Date());

        // filePath
        int pos = fileName.lastIndexOf('.');// remove the type from file path.
        if (pos > 0) {
            fileName = fileName.substring(0, pos) + "_V";
        }
        long tNum = MediaUpload.countMediaUploadsByKeyAndPerson(fileName, person);
        if (tNum >= 0) {
            fileName = fileName + tNum;
        }

        media.setFilepath(fileName);
        media.setStatus(CC.STATUS_NEW);
        media.setContent(content != null ? content.getBytes() : null); // ------------file content----------
        media.setFilesize(content != null ? content.getSize() : 0); // ------------file size-------------
        media.persist();

        uiModel.asMap().clear();
        uiModel.addAttribute("email", userAccount.getEmail());
        return buildPageForMenu(uiModel, request, null);
    }

    private UserAccount createANewUserForJobApplier(
            HttpServletRequest request,
            Person person) {

        String email = request.getParameter("email");
        String loginName =
                StringUtils.isBlank(email) ? String.valueOf(Calendar.getInstance().getTimeInMillis()) : email;
        loginName = TaoEncrypt.enrichName(loginName, person);
        String password = TaoEncrypt.encryptPassword(person.getName());
        UserAccount existingOne = UserAccount.findUserAccountByName(loginName);
        if (existingOne != null) {
            return existingOne;
        }

        UserAccount userAccount = new UserAccount();
        userAccount.setPerson(person);
        userAccount.setAddress(request.getParameter("address"));
        userAccount.setCel(request.getParameter("mobile"));
        userAccount.setCity(request.getParameter("city"));
        userAccount.setEmail(email);
        userAccount.setLoginname(loginName);
        userAccount.setPassword(password);
        userAccount.persist();
        return userAccount;
    }

    private String checkSize(
            Model uiModel,
            String keyString,
            FileItem tFileItem,
            Person person) {
        if (tFileItem.getSize() > 5000000) {
            if (person != null && CC.ADMIN.equals(person.getName())) {
                return null;
            }

            uiModel.addAttribute("notice", tFileItem.getName()
                    + "is NOT submitted! Please submit only file less than 5M.");
            if ("personalDocument".equals(keyString)) {
                return "dashboard";
            } else if ("careerApply".equals(keyString)) {
                return "careerApply";
            } else {
                return "sendEmail";
            }
        } else {
            return null;
        }
    }

    // products------------------------------------------
    // =====================================product=====================================
    @RequestMapping(value = "/updateProductInfo", method = RequestMethod.POST)
    public String updateProductInfo(
            Model uiModel,
            HttpServletRequest request) {
        Person person = TaoUtil.getCurPerson(request);
        Long id = Long.valueOf(request.getParameter("id"));
        String partNo = request.getParameter("partNo");
        Product tProduct = Product.findProductsByPartNoAndPerson(partNo, person);

        if (tProduct != null && tProduct.getId().longValue() != id.longValue()) {
            uiModel.addAttribute("Update_ErrorMessage",
                    "the PartNumber is already used by an other product, please check and input again.");
            uiModel.addAttribute("product", tProduct);
            return "products/update";
            // return SpringApplicationContext.getApplicationContext().getBean("userAccountController",
            // UserAccountController.class).updateForm(tUserAccount.getId(), model);//with error message.
        } else if (tProduct == null) {
            tProduct = Product.findProduct(id);
        }

        tProduct.setPartNo(partNo);
        tProduct.setName(request.getParameter("name"));
        tProduct.setProducedplace(request.getParameter("producedplace"));
        tProduct.setC1(request.getParameter("c1"));
        tProduct.setC2(request.getParameter("c2"));
        tProduct.setC3(request.getParameter("c3"));
        tProduct.setLitorweight(request.getParameter("litorweight"));
        tProduct.setUnit(request.getParameter("unit"));
        tProduct.setPackagesname(request.getParameter("packagesname"));
        tProduct.setItemquantity(request.getParameter("itemquantity"));

        String tNoNumException = null;
        String tIntStr = request.getParameter("lenth");
        if (StringUtils.isNumeric(tIntStr))
            tProduct.setLenth(Integer.parseInt(request.getParameter("lenth")));
        else
            tNoNumException = "lenth";

        tIntStr = request.getParameter("width");
        if (StringUtils.isNumeric(tIntStr))
            tProduct.setWidth(Integer.parseInt(request.getParameter("width")));
        else
            tNoNumException = "width";

        tIntStr = request.getParameter("height");
        if (StringUtils.isNumeric(tIntStr))
            tProduct.setHeight(Integer.parseInt(request.getParameter("height")));
        else
            tNoNumException = "height";

        tIntStr = request.getParameter("price1");
        if (StringUtils.isNumeric(tIntStr))
            tProduct.setPrice1(Integer.parseInt(request.getParameter("price1")));
        else
            tNoNumException = "price1";

        tIntStr = request.getParameter("price2");
        if (StringUtils.isNumeric(tIntStr))
            tProduct.setPrice2(Integer.parseInt(request.getParameter("price2")));
        else
            tNoNumException = "price2";

        tIntStr = request.getParameter("price3");
        if (StringUtils.isNumeric(tIntStr))
            tProduct.setPrice3(Integer.parseInt(request.getParameter("price3")));
        else
            tNoNumException = "price3";

        if (tNoNumException != null) {
            uiModel.addAttribute("Update_ErrorMessage", tNoNumException
                    + " was inputted with character, while only number allowed. Please input right number.");
            uiModel.addAttribute("product", tProduct);
            return "products/update";
        }
        // return SpringApplicationContext.getApplicationContext().getBean("userAccountController",
        // UserAccountController.class).updateForm(tUserAccount.getId(), model);//with error message.

        tProduct.setDescription(request.getParameter("description"));
        tProduct.persist();
        // return index(uiModel, request);
        String tURLStr = "menu" + request.getParameter("c1").substring(7);
        return "redirect:/" + tURLStr;
    }

    @RequestMapping(value = "/mycarts", produces = "text/html")
    public String getMyCarts(
            Model uiModel,
            Person person) {
        String userName = userContextService.getCurrentUserName();
        UserAccount userAccount = UserAccount.findUserAccountByName(TaoEncrypt.enrichName(userName, person));
        uiModel.addAttribute("carts", Cart.findCartsByUser(userAccount));
        return "carts/show";
    }

    // @NOTE:if put this method into product controller, then have to supply a method to resonponse for
    // "/products/products/searchProductUrl".
    // @NOTE:if don't use a new path like following, while use only the parameter instead, then can not pass compilation
    // when moved to MainPageController.java.
    @RequestMapping("/searchProductUrl")
    public String searchProduct(
            @RequestParam(value = "searchProducts", required = true)
            String search,
            Model uiModel,
            HttpServletRequest request) {
        if (StringUtils.isBlank(request.getParameter("searchContent"))) {
            uiModel.addAttribute("productAmount", 0);
            return "/generalCatalogPage";
        } else
            search = request.getParameter("searchContent").toString().toLowerCase();

        List<Product> tProducts = Product.findAllProducts();
        List tResultList = new ArrayList();
        for (int i = tProducts.size() - 1; i >= 0; i--) {
            Product tProduct = tProducts.get(i);
            StringBuilder tContent = new StringBuilder();

            String c2 = tProduct.getC2();
            if (StringUtils.isNotBlank(c2)) {
                tContent.append(CC.TEMP_SEP_PROD_FIELD_INFO);
                tContent.append(c2.toLowerCase());
            }

            String c3 = tProduct.getC3();
            if (StringUtils.isNotBlank(c3)) {
                tContent.append(CC.TEMP_SEP_PROD_FIELD_INFO);
                tContent.append(c3.toLowerCase());
            }

            String description = tProduct.getDescription();
            if (StringUtils.isNotBlank(description)) {
                tContent.append(CC.TEMP_SEP_PROD_FIELD_INFO);
                tContent.append(description.toLowerCase());
            }

            String name = tProduct.getName();
            if (StringUtils.isNotBlank(name)) {
                tContent.append(CC.TEMP_SEP_PROD_FIELD_INFO);
                tContent.append(name.toLowerCase());
            }

            String packagesname = tProduct.getPackagesname();
            if (StringUtils.isNotBlank(packagesname)) {
                tContent.append(CC.TEMP_SEP_PROD_FIELD_INFO);
                tContent.append(packagesname.toLowerCase());
            }

            String partNo = tProduct.getPartNo();
            if (StringUtils.isNotBlank(partNo)) {
                tContent.append(CC.TEMP_SEP_PROD_FIELD_INFO);
                tContent.append(partNo.toLowerCase());
            }

            String itemquantity = tProduct.getItemquantity();
            if (StringUtils.isNotBlank(itemquantity)) {
                tContent.append(CC.TEMP_SEP_PROD_FIELD_INFO);
                tContent.append(itemquantity.toLowerCase());
            }

            String producedplace = tProduct.getProducedplace();
            if (StringUtils.isNotBlank(producedplace)) {
                tContent.append(CC.TEMP_SEP_PROD_FIELD_INFO);
                tContent.append(producedplace.toLowerCase());
            }

            String unit = tProduct.getUnit();
            if (StringUtils.isNotBlank(unit)) {
                tContent.append(CC.TEMP_SEP_PROD_FIELD_INFO);
                tContent.append(unit.toLowerCase());
            }

            String lenth = String.valueOf(tProduct.getLenth());
            if (StringUtils.isNotBlank(lenth)) {
                tContent.append(CC.TEMP_SEP_PROD_FIELD_INFO);
                tContent.append(lenth);
            }

            String width = String.valueOf(tProduct.getWidth());
            if (StringUtils.isNotBlank(width)) {
                tContent.append(CC.TEMP_SEP_PROD_FIELD_INFO);
                tContent.append(width);
            }

            String litorweight = String.valueOf(tProduct.getLitorweight());
            if (StringUtils.isNotBlank(litorweight)) {
                tContent.append(CC.TEMP_SEP_PROD_FIELD_INFO);
                tContent.append(litorweight);
            }

            if (tContent.indexOf(search) > 0)
                tResultList.add(tProduct);
        }
        tResultList = TaoUtil.languageRelevantProcess(tResultList, TaoUtil.getLangPrfWithDefault(request));

        uiModel.addAttribute("products", tResultList);
        uiModel.addAttribute("productAmount", tResultList.size());

        return "/generalCatalogPage";
    }

    @RequestMapping(value = "/showProductJson/{id}", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(
            @PathVariable("id")
            Long id,
            @RequestParam(CC.LANG)
            String lang) {
        Product product = Product.findProduct(id);
        HttpHeaders headers = new HttpHeaders();
        if (lang.length() == 2)
            lang = lang + "_";
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (product == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        // set the display name for product
        String tName = product.getName();
        String tDescrip = product.getDescription();
        String[] tNames = tName.split("~");
        String[] tDescrips = tDescrip.split("~");
        boolean matched = false;
        for (int j = 0; j < tNames.length; j++) {
            if (tNames[j].toLowerCase().trim().startsWith(lang.toLowerCase())) {
                product.setLocalName(tNames[j].trim().substring(3));
                matched = true;
                break; // go and work on next product.
            }
        }
        // no match, then use the first name.
        if (!matched)
            product.setLocalName(tNames[0].indexOf("_") == 2 ? tNames[0].substring(3) : tNames[0]);

        matched = false;
        for (int j = 0; j < tDescrips.length; j++) {
            if (tDescrips[j].toLowerCase().trim().startsWith(lang.toLowerCase())) {
                product.setLocalDescription(tDescrips[j].trim().substring(3));
                matched = true;
                break; // go and work on next product.
            }
        }
        // no match, then use the first name.
        if (!matched)
            product.setLocalDescription(tDescrips[0].indexOf("_") == 2 ? tDescrips[0].substring(3) : tDescrips[0]);

        return new ResponseEntity<String>(product.toJson(), headers, HttpStatus.OK);
    }

    // ====================================feature=====================================
    @RequestMapping(value = "/deleteFeatureGroup", method = RequestMethod.POST)
    public String deleteFeatureGroup(
            Model model,
            HttpServletRequest request) {
        String refMenuIdx = request.getParameter("refMenuIdxForDelete");
        if (StringUtils.isBlank(refMenuIdx)) {
            return buildPageForMenu(model, request, null);
        }

        Person person = TaoUtil.getCurPerson(request);
        String menuIdx = request.getSession().getAttribute(CC.menuIdx).toString();

        List<Feature> features = Feature.findAllFeaturesByMenuIdxAndPerson(menuIdx, person);
        if (features == null) {
            return buildPageForMenu(model, request, null);
        }

        for (int i = features.size() - 1; i >= 0; i--) {
            Feature feature = features.get(i);
            if (refMenuIdx.equals(feature.getRefMenuIdx())) {
                feature.remove();
                break;
            }
        }

        // return "redirect:/menu_" + menuIdx;
        return buildPageForMenu(model, request, null);
    }

    @RequestMapping(value = "/{client}/updateFeature/{imageKey}", headers = "Accept=application/json",
            method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> updateFeature(
            @PathVariable(CC.CLIENT)
            String client,
            @PathVariable("imageKey")
            String imageKey,
            HttpServletRequest request) {

        if (hasNotLoggedIn(request)) {
            dirtFlagCommonText = TaoUtil.switchClient(request, client);
        }
        return updateFeature(imageKey, request);
    }

    @RequestMapping(value = "updateFeature/{imageKey}", headers = "Accept=application/json",
            method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> updateFeature(
            @PathVariable("imageKey")
            String imageKey,
            HttpServletRequest request) {
        Person person = TaoUtil.getCurPerson(request);
        String featureId = request.getParameter("featureId");
        Feature feature = Feature.findFeature(Long.valueOf(featureId));
        String itemsToShow = feature.getItemsToShow();
        int p = itemsToShow.indexOf(imageKey);
        if (p < 0) {
            if (!itemsToShow.endsWith(",")) {
                itemsToShow = itemsToShow + ",";
            }
            itemsToShow = itemsToShow + imageKey;

            String ref = feature.getRefMenuIdx();
            List<String> imageKeys = MediaUpload.listAllMediaUploadsKeyByKeyAndPerson("service_" + ref, person);
            imageKeys = TaoImage.stripThumOrderAndValidate(imageKeys);

            StringBuilder sb = new StringBuilder();
            for (String item : imageKeys) {
                if (itemsToShow.contains(item)) {
                    sb.append(item);
                    sb.append(',');
                }
            }
            itemsToShow = sb.substring(0, sb.length() - 1).toString();
        } else {
            itemsToShow = itemsToShow.substring(0, p) + itemsToShow.substring(p + imageKey.length());
            itemsToShow = itemsToShow.replace(",,", ",");
        }
        feature.setItemsToShow(itemsToShow);
        feature.persist();
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{client}/updatePrinter/{imageKey}", headers = "Accept=application/json",
            method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> updatePrinter(
            @PathVariable(CC.CLIENT)
            String client,
            @PathVariable("imageKey")
            String imageKey,
            HttpServletRequest request) {

        if (hasNotLoggedIn(request)) {
            dirtFlagCommonText = TaoUtil.switchClient(request, client);
        }
        return updatePrinter(imageKey, request);
    }

    @RequestMapping(value = "updatePrinter/{imageKey}", headers = "Accept=application/json",
            method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> updatePrinter(
            @PathVariable("imageKey")
            String imageKey,
            HttpServletRequest request) {
        Person person = TaoUtil.getCurPerson(request);
        // String featureId = request.getParameter("featureId");
        // Feature feature = Feature.findFeature(Long.valueOf(featureId));
        // String itemsToShow = feature.getItemsToShow();

        Service service = Service.findServiceByCatalogAndPerson(imageKey, person);
        if (service == null) {
            service = new Service();
            service.setPerson(person);
            service.setC1(imageKey);
            service.setC3(",");
        }
        String curPrinterName = TaoEncrypt.stripUserName(userContextService.getCurrentUserName());
        String printersStr = service.getC3();// c3 is now used to pu printers string.
        if (printersStr == null) {
            printersStr = ",";
        }
        int p = printersStr.indexOf("," + curPrinterName + ",");
        if (p < 0) {
            if (!printersStr.endsWith(",")) {
                printersStr = printersStr + ",";
            }
            printersStr = printersStr + curPrinterName + ",";
        } else {
            printersStr = printersStr.substring(0, p) + printersStr.substring(p + curPrinterName.length() + 1);
        }
        service.setC3(printersStr);
        service.persist();
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{client}/updateSelection/{imageKey}", headers = "Accept=application/json",
            method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> updateSelection(
            @PathVariable(CC.CLIENT)
            String client,
            @PathVariable("imageKey")
            String imageKey,
            HttpServletRequest request) {

        if (hasNotLoggedIn(request)) {
            dirtFlagCommonText = TaoUtil.switchClient(request, client);
        }
        return updateSelection(imageKey, request);
    }

    @RequestMapping(value = "updateSelection/{imageKey}", headers = "Accept=application/json",
            method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> updateSelection(
            @PathVariable("imageKey")
            String imageKey,
            HttpServletRequest request) {
        Person person = TaoUtil.getCurPerson(request);
        String newItemStr = "," + imageKey + ",";
        String selectedItems = (String) request.getSession().getAttribute(CC.selectedItems);

        Service service = Service.findServiceByCatalogAndPerson(imageKey, person);
        float total = Float.valueOf(service.getDescription());
        if (selectedItems == null) {
            selectedItems = newItemStr;
            try {
                request.getSession().setAttribute(CC.itemNumber, 1);
                request.getSession().setAttribute(CC.totalPrice, (float) (Math.round(total * 100)) / 100);
            } catch (Exception e) {
                TaoDebug.info(request, "service dosen't have price yet!", imageKey);
            }
        } else {
            float currentPrice = (Float) request.getSession().getAttribute(CC.totalPrice);
            int p = selectedItems.indexOf(newItemStr);
            if (p < 0) {
                try {
                    request.getSession().setAttribute(CC.itemNumber,
                            (Integer) request.getSession().getAttribute(CC.itemNumber) + 1);
                    request.getSession().setAttribute(CC.totalPrice,
                            (float) (Math.round((currentPrice + total) * 100)) / 100);
                } catch (Exception e) {
                    TaoDebug.info(request, "service dosen't have price yet!", imageKey);
                }
                selectedItems = selectedItems + imageKey + ",";

            } else {
                try {
                    request.getSession().setAttribute(CC.itemNumber,
                            (Integer) request.getSession().getAttribute(CC.itemNumber) - 1);
                    request.getSession().setAttribute(CC.totalPrice,
                            (float) (Math.round((currentPrice - total) * 100)) / 100);
                } catch (Exception e) {
                    TaoDebug.info(request, "service dosen't have price yet!", imageKey);
                }
                selectedItems = selectedItems.substring(0, p) + selectedItems.substring(p + imageKey.length() + 1);
            }
        }
        request.getSession().setAttribute(CC.selectedItems, selectedItems);

        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{client}/updateGeo/{latitude}", headers = "Accept=application/json",
            method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> updateGeo(
            @PathVariable(CC.CLIENT)
            String client,
            @PathVariable(CC.latitude)
            String latitude,
            HttpServletRequest request) {

        if (hasNotLoggedIn(request)) {
            dirtFlagCommonText = TaoUtil.switchClient(request, client);
        }
        return updateGeo(latitude, request);
    }

    @RequestMapping(value = "updateGeo/{latitude}", headers = "Accept=application/json", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> updateGeo(
            @PathVariable(CC.latitude)
            String latitude,
            HttpServletRequest request) {
        request.getSession().setAttribute(CC.latitude, latitude);
        request.getSession().setAttribute(CC.longitude, request.getParameter(CC.longitude));

        return new ResponseEntity<String>(HttpStatus.OK);
    }

    // ===================Operations for dashboard==========================================================

    @RequestMapping(value = "/showSelection")
    public String showSelection(
            Model model,
            HttpServletRequest request) {
        Person person = TaoUtil.getCurPerson(request);
        String langPrf = TaoUtil.getLangPrfWithDefault(request);
        String menuPRF = langPrf + "menu_";
        TaoDebug.info("start to initFeatureSubPage for showSelection, for client: {}", person.getName());

        HttpSession session = request.getSession();
        String selectedItems = (String) session.getAttribute(CC.selectedItems);
        String[] imageKeyStrs = StringUtils.split(selectedItems, ',');
        if (imageKeyStrs == null) {
            return "/generalFeaturePage";
        }

        List<Feature> features = new ArrayList<Feature>();
        List<List<String>> imageKeyLists = new ArrayList<List<String>>();
        List<List<String>> descriptions = new ArrayList<List<String>>();
        List<List<String>> visibleStatusList = new ArrayList<List<String>>();
        List<String> groupTitles = new ArrayList<String>();

        HashMap map = new HashMap();
        for (String imageKeyStr : imageKeyStrs) {
            int p = imageKeyStr.lastIndexOf('_');
            String menuIdx = imageKeyStr.substring(8, p);
            if (menuIdx.endsWith("_0")) {
                menuIdx = menuIdx.substring(0, menuIdx.length() - 2);
                if (menuIdx.endsWith("_0")) {
                    menuIdx = menuIdx.substring(0, menuIdx.length() - 2);
                }
            }
            List<String> list = (List<String>) map.get(menuIdx);
            if (list == null) {
                list = new ArrayList<String>();
                map.put(menuIdx, list);
            }
            list.add(imageKeyStr);
        }
        // to make it in order.
        List<String> menusForRef = TaoUtil.fetchAllMenuByType(CC.SERVICE, request, langPrf, person);
        long i = 0;
        for (String menuSring : menusForRef) {
            List<String> keys = (List<String>) map.get(menuSring);
            if (keys != null) {
                // imageKeyLists
                imageKeyLists.add(keys);

                // descriptions
                TaoUtil.fillInDescriptions(langPrf, person, descriptions, keys);

                // visibleStatusList
                List<String> visibleStatus = new ArrayList<String>();
                for (String item : keys) {
                    visibleStatus.add("true");
                }
                visibleStatusList.add(visibleStatus);

                // features
                Feature feature = new Feature();
                feature.setId(i);
                feature.setMenuIdx(menuSring);
                features.add(feature);
                i++;

                // groupTitles
                TextContent textContent = TextContent.findContentsByKeyAndPerson(menuPRF + menuSring, person);
                groupTitles.add(textContent.getContent());
            }
        }

        model.addAttribute("features", features);
        model.addAttribute("imageKeys", imageKeyLists);
        model.addAttribute("descriptions", descriptions);
        model.addAttribute("visibleStatusList", visibleStatusList);
        model.addAttribute("groupTitles", groupTitles);

        // left menu bar
        String pKey = (String) session.getAttribute(CC.default_feature_menu);
        int p = pKey.indexOf('_', 5);
        if (p > 5) {
            pKey = pKey.substring(0, p + 1);
        }
        List<List<String>> subMenu = TaoUtil.prepareMenuContent(pKey, langPrf, person);
        model.addAttribute("subMenu", subMenu);
        return "/generalFeaturePage";
    }

    @RequestMapping(value = "/{client}/createAnOrder/{orderedItems}", headers = "Accept=application/json",
            method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> createAnOrder(
            @PathVariable("client")
            String client,
            @PathVariable("orderedItems")
            String orderedItems,
            Model model,
            HttpServletRequest request) {

        if (hasNotLoggedIn(request)) {
            dirtFlagCommonText = TaoUtil.switchClient(request, client);
        }
        return createAnOrder(orderedItems, model, request);
    }

    @RequestMapping(value = "createAnOrder/{orderedItems}", headers = "Accept=application/json",
            method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> createAnOrder(
            @PathVariable("orderedItems")
            String orderedItems,
            Model model,
            HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (TaoSecurity.isHecker(request)) {
            session.setAttribute(CC.totalPrice, 0.00);
            session.setAttribute(CC.itemNumber, 0);
            session.setAttribute(CC.selectedItems, null);
            TextContent textContent = new TextContent();
            textContent.setContent("Reported!");
            return new ResponseEntity<String>(textContent.toJson(), HttpStatus.ALREADY_REPORTED);
        }
        Person person = TaoUtil.getCurPerson(request);
        UserAccount curUser = (UserAccount) session.getAttribute(CC.currentUser);
        orderedItems = (String) session.getAttribute(CC.selectedItems);
        String[] items = StringUtils.split(orderedItems, ",");
        if (items.length == 0) {
            if (curUser == null || !CC.ROLE_EMPLOYEE.equals(curUser.getSecuritylevel())) {
                TextContent textContent = new TextContent();
                textContent.setContent("Nothing selected yet!");
                return new ResponseEntity<String>(textContent.toJson(), HttpStatus.BAD_REQUEST);
            }
        }
        String source = request.getParameter("source");
        // format:var source = tableID + "," + name + "," + phoneNumber + "," + address + "," + arrive;
        String[] params = StringUtils.split(source, ",");

        String sizeTable = params[0];
        String tel = params[0];
        String name = params[1];

        UserAccount client = null;
        if (curUser != null) {
            client = (UserAccount) curUser;
        } else if (tel.length() > 6) {
            client = UserAccount.findUserAccountsByNameAndTell(name, tel);
        } else {
            // means, user has logged in, so was not asked to input name, tel..while when he click send, it's already
            // time out... throw new RuntimeException("time out!, please log in again.");
            // now even logged in user should also input address and new tell, because might ordered for his mother.
        }

        String delieverdate = null;
        String langPrf = TaoUtil.getLangPrfWithDefault(request);
        String moneyLetter = CC.money.valueOf(langPrf.substring(0, 2)).getValue();

        MainOrder sourcdAndNewMainOrder = new MainOrder();
        sourcdAndNewMainOrder.setPerson(person);
        sourcdAndNewMainOrder.setClientSideOrderNumber(session.getId());
        sourcdAndNewMainOrder.setContactPerson(null);// it will be set value when a employee processed.
        sourcdAndNewMainOrder.setClient(client);// it will be null if a non registered user put an order.
        sourcdAndNewMainOrder.setSizeTable(sizeTable);
        if (StringUtils.isBlank(delieverdate)) {
            sourcdAndNewMainOrder.setDelieverdate(new Date());
        } else {
            try {
                sourcdAndNewMainOrder.setDelieverdate(new SimpleDateFormat("HH:mm:SS").parse(delieverdate));
            } catch (Exception e) {
                // do nothing.
            }
        }
        sourcdAndNewMainOrder.setRecordStatus(0);
        sourcdAndNewMainOrder.setRemark(null); // like medium or well down, spicy or super spicy, will be add when
                                               // employee
        // process.
        sourcdAndNewMainOrder.persist();

        // -----------persist the materials-----------------
        float total = 0;
        List<Material> materials = new ArrayList<Material>();
        for (String item : items) {
            String key = langPrf + "service_" + item + "_description";

            // todo: should get the infomation from Service table.
            TextContent textContent = TextContent.findContentsByKeyAndPerson(key, person);
            String description = textContent != null ? textContent.getContent() : "";
            String productName = TaoUtil.fetchProductName(description, moneyLetter);
            String payCondition = TaoUtil.fetchProductPrice(description, moneyLetter);
            total += Float.valueOf(payCondition);

            String menfu = "";
            Service service = Service.findServiceByCatalogAndPerson(item, person);
            if (service != null && service.getC3() != null) {
                menfu = service.getC3().trim();
            }
            if (!menfu.startsWith(",")) {
                menfu = "," + menfu;
            }
            if (!menfu.endsWith(",")) {
                menfu = menfu + ",";
            }

            Material material = new Material();
            material.setLocation(item);
            material.setMainOrder(sourcdAndNewMainOrder);
            material.setPortionName(productName);
            // this one could be added by employee....material.setRemark(remark);
            material.setDencity(moneyLetter + payCondition);
            material.setMenFu(menfu);
            material.persist();
            materials.add(material);
        }

        // ----------------re-update the mainOrder-----------------------
        total = (float) (Math.round(total * 100)) / 100;
        if (total == 0) {//
            sourcdAndNewMainOrder.setRecordStatus(5);// means this is actually a customer changed place and employee
            sourcdAndNewMainOrder.persist(); // create a new one to merge.
        } else {
            sourcdAndNewMainOrder.setPayCondition(moneyLetter + String.valueOf(total)); // actual deal price.
            if ("true".equals(request.getSession().getAttribute("auto_merge_rec"))) {
                MainOrder existingSameSourceMainOrder = searchSameSourceMainOrder(sourcdAndNewMainOrder, person);
                if (existingSameSourceMainOrder != null && existingSameSourceMainOrder.getRecordStatus() == 0) {
                    // add the new order into existing one.
                    sourcdAndNewMainOrder =
                            doCombineAction(request, sourcdAndNewMainOrder, materials, existingSameSourceMainOrder);
                } else {
                    sourcdAndNewMainOrder.persist();
                }
            } else {
                sourcdAndNewMainOrder.persist();
            }
        }

        // -----------------log the visiting-------------------
        TaxonomyMaterial taxonomyMaterial = new TaxonomyMaterial();
        taxonomyMaterial.setItemName(request.getRemoteHost());// computer name if not set, then will be ip address
        taxonomyMaterial.setLocation(request.getRemoteAddr());// ip address
        taxonomyMaterial.setMainOrder(sourcdAndNewMainOrder);
        taxonomyMaterial.setPerson(person);
        taxonomyMaterial.setRemark(request.getRequestURI());// what ordered e.g.
                                                            // /taostyle/thai/createAnOrder/3_2_0_3,3_2_0_1,3_1_0_1
        taxonomyMaterial.setLogtime(new Date());
        taxonomyMaterial.setSpecification((String) session.getAttribute(CC.latitude));
        taxonomyMaterial.setQuality((String) session.getAttribute(CC.longitude));
        taxonomyMaterial.persist();

        // resection selection status.
        session.setAttribute(CC.totalPrice, 0.00);
        session.setAttribute(CC.itemNumber, 0);
        session.setAttribute(CC.selectedItems, null);
        session.setAttribute(CC.latitude, null);
        session.setAttribute(CC.longitude, null);

        // ----------------return-----------------------
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    private MainOrder searchSameSourceMainOrder(
            MainOrder sourcdAndNewMainOrder,
            Person person) {
        List<MainOrder> tList =
                MainOrder.finMainOrdersBySizeTableAndPerson(sourcdAndNewMainOrder.getSizeTable(), person);
        MainOrder mainOrder = null;
        if (tList != null && tList.size() > 1) {
            mainOrder = tList.get(1);
        }

        Date now = new Date();
        int curMin = now.getMinutes();
        if (mainOrder != null) {
            Date date = mainOrder.getDelieverdate();
            if (now.getHours() == date.getHours()) {
                int min = date.getMinutes();
                int sec = date.getSeconds();
                if (min == curMin) {
                    return mainOrder;
                } else if (min + 1 == curMin) {
                    if (now.getSeconds() < date.getSeconds()) {
                        return mainOrder;
                    }
                }
            } else if (now.getHours() == date.getHours() + 1) {
                if (curMin == 0 && now.getSeconds() < date.getSeconds()) {
                    return mainOrder;
                }
            }
        }
        return null;
    }

    @RequestMapping(value = "/{client}/updateAnOrderStatus/{mainOrderID}", headers = "Accept=application/json",
            method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> updateAnOrderStatus(
            @PathVariable("client")
            String client,
            @PathVariable("mainOrderID")
            String mainOrderID,
            Model model,
            HttpServletRequest request) {

        if (hasNotLoggedIn(request)) {
            dirtFlagCommonText = TaoUtil.switchClient(request, client);
        }
        return updateAnOrderStatus(mainOrderID, model, request);
    }

    @RequestMapping(value = "updateAnOrderStatus/{mainOrderID}", headers = "Accept=application/json",
            method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> updateAnOrderStatus(
            @PathVariable("mainOrderID")
            String mainOrderID,
            Model model,
            HttpServletRequest request) {

        UserAccount user = (UserAccount) request.getSession().getAttribute(CC.currentUser);

        String[] ids = mainOrderID.split(",");
        for (String orderId : ids) {

            Long id = Long.valueOf(orderId);
            MainOrder mainOrder = MainOrder.findMainOrder(id);
            String recordStatus = request.getParameter("recordStatus");
            int status = Integer.valueOf(recordStatus);
            if (status == CC.STATUS_PRINTED) {
                // not really setting the status, but up the level of each material. and put name to mainOrder.
                // later on when someone fetching order to print, will update the order to "PRINTED".
                // (to avoid multi-thread issue)
                List<Material> materials = Material.findAllMaterialsByMainOrderIdAndPrinter(mainOrder, user);
                String printerName = TaoEncrypt.stripUserName(user.getLoginname());
                for (Material material : materials) {
                    String printersStr = material.getMenFu();
                    String[] ary = printersStr.split(",");
                    int step = CC.LEVEL_FULL / (ary.length - 1);// @NOTE:7 and 9 will be trouble number. don't let a
                    // dish link to7 or 9 printer.
                    material.setRecordStatus(material.getRecordStatus() + step);
                    material.persist();
                }
                String printerStr = mainOrder.getColorCard();
                if (printerStr == null) {
                    printerStr = "," + printerName + ",";
                } else {
                    printerStr = printerStr + printerName + ",";
                }
                mainOrder.setColorCard(printerStr);
            } else {
                mainOrder.setRecordStatus(status);
                mainOrder.setContactPerson(user);
            }
            mainOrder.persist();
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{client}/combineAnOrder/{mainOrderID}", headers = "Accept=application/json",
            method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> combineAnOrder(
            @PathVariable("client")
            String client,
            @PathVariable("mainOrderID")
            String mainOrderID,
            Model model,
            HttpServletRequest request) {

        if (hasNotLoggedIn(request)) {
            dirtFlagCommonText = TaoUtil.switchClient(request, client);
        }
        return combineAnOrder(mainOrderID, model, request);
    }

    @RequestMapping(value = "combineAnOrder/{mainOrderID}", headers = "Accept=application/json",
            method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> combineAnOrder(
            @PathVariable("mainOrderID")
            String mainOrderID,
            Model model,
            HttpServletRequest request) {

        Person person = TaoUtil.getCurPerson(request);
        Long id = Long.valueOf(mainOrderID);
        MainOrder sourceMainOrder = MainOrder.findMainOrder(id);
        List<Material> materials = Material.findAllMaterialsByMainOrder(sourceMainOrder);

        String targetOrderInfo = request.getParameter("targetOrder");

        MainOrder mainOrder = MainOrder.findMainOrder(Long.valueOf(targetOrderInfo));
        mainOrder = doCombineAction(request, sourceMainOrder, materials, mainOrder);
        mainOrder.persist();
        sourceMainOrder.remove();

        return new ResponseEntity<String>(HttpStatus.OK);
    }

    private MainOrder doCombineAction(
            HttpServletRequest request,
            MainOrder sourceMainOrder,
            List<Material> materials,
            MainOrder mainOrder) {
        Object user = request.getSession().getAttribute(CC.currentUser);
        mainOrder.setContactPerson((UserAccount) user);

        updateMaterialMainOrderId(materials, mainOrder);
        addjustPayconditionAndLogs(mainOrder, sourceMainOrder);
        mainOrder.setRecordStatus(mainOrder.getRecordStatus() - 100);
        return mainOrder;
    }

    private void updateMaterialMainOrderId(
            List<Material> materials,
            MainOrder mainOrder) {
        for (Material material : materials) {
            material.setMainOrder(mainOrder);
            material.persist();
        }
    }

    private void addjustPayconditionAndLogs(
            MainOrder mainOrder,
            MainOrder sourceMainOrder) {
        String price = sourceMainOrder.getPayCondition().substring(1);
        String price2 = mainOrder.getPayCondition().substring(1);
        float newPrice = Float.valueOf(price) + Float.valueOf(price2);
        mainOrder.setPayCondition("$" + newPrice);
        // disappear itself
        List<TaxonomyMaterial> taxonomyMaterials =
                TaxonomyMaterial.findAllTaxonomyMaterialsByMainOrder(sourceMainOrder.getId());
        for (TaxonomyMaterial taxonomyMaterial : taxonomyMaterials) {
            taxonomyMaterial.setMainOrder(mainOrder);
            taxonomyMaterial.persist();
        }
    }

    @RequestMapping(value = "/{client}/cleanHistoricalMainOrder/", headers = "Accept=application/json",
            method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> cleanHistoricalMainOrder(
            @PathVariable("client")
            String client,
            Model model,
            HttpServletRequest request) {

        if (hasNotLoggedIn(request)) {
            dirtFlagCommonText = TaoUtil.switchClient(request, client);
        }
        return cleanHistoricalMainOrder(model, request);
    }

    @RequestMapping(value = "cleanHistoricalMainOrder/", headers = "Accept=application/json",
            method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> cleanHistoricalMainOrder(
            Model model,
            HttpServletRequest request) {

        Person person = TaoUtil.getCurPerson(request);

        List<MainOrder> mainOrders = MainOrder.findCompletedMainOrdersByPerson(person, "desc");
        for (MainOrder mainOrder : mainOrders) {
            deleteAnOrder(mainOrder);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    public void deleteAnOrder(
            MainOrder mainOrder) {
        List<Material> materials = Material.findAllMaterialsByMainOrder(mainOrder);
        for (Material material : materials) {
            material.remove();
        }
        mainOrder.remove();
    }

    @RequestMapping(value = "/{client}/generateQRCodes/", headers = "Accept=application/json",
            method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> generateQRCodes(
            @PathVariable("client")
            String client,
            Model model,
            HttpServletRequest request) {

        return generateQRCodes(model, request);
    }

    @RequestMapping(value = "generateQRCodes/", headers = "Accept=application/json", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> generateQRCodes(
            Model model,
            HttpServletRequest request) {

        Person person = TaoUtil.getCurPerson(request);

        HttpSession session = request.getSession();
        String menuIdx = (String) session.getAttribute(CC.default_feature_menu);

        // start
        String table = (String) session.getAttribute("start_table");
        String customer = (String) session.getAttribute("start_customer");
        int tableStart = 1;
        int customerStart = 1;
        try {
            tableStart = Integer.valueOf(table);
        } catch (Exception e) {
        }
        try {
            customerStart = Integer.valueOf(customer);
        } catch (Exception e) {
        }
        // limit
        table = (String) session.getAttribute("limit_table");
        customer = (String) session.getAttribute("limit_customer");
        int tableLimit = 0;
        int customerLimit = 0;
        try {
            tableLimit = Integer.valueOf(table);
            tableLimit = tableLimit > 200 ? 200 : tableLimit;
        } catch (Exception e) {
        }
        try {
            customerLimit = Integer.valueOf(customer);
            customerLimit = customerLimit > 100 ? 100 : customerLimit;
        } catch (Exception e) {
        }

        String textFix = composeQRCodeText(request, menuIdx);
        String text = null;
        for (int i = tableStart; i <= tableLimit; i++) {
            if (customerLimit > 0) {
                for (int j = customerStart; j <= customerLimit; j++) {
                    String filePath = "QRCode_temp_" + i + "_" + j;
                    text = textFix + "?t=" + i + "_" + j;
                    generateQRCode(request, filePath, text, person);
                }
            } else {
                String filePath = "QRCode_temp_" + i;
                text = textFix + "?t=" + i;
                generateQRCode(request, filePath, text, person);
            }
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    private String composeQRCodeText(
            HttpServletRequest request,
            String menuIdx) {
        HttpSession session = request.getSession();

        String app_WebsiteAddress = (String) session.getAttribute(CC.app_WebsiteAddress);
        if (StringUtils.isBlank(app_WebsiteAddress)) {
            if (session.getAttribute(CC.app_name) != null) {
                app_WebsiteAddress = "http://" + session.getAttribute(CC.app_name) + ".com/";
            } else {
                TaoDebug.error("app_name not set, so cannot do {}! ", "composeQRCodeText");
                return null;
            }
        } else if (!app_WebsiteAddress.startsWith("http://")) {
            app_WebsiteAddress = "http://" + app_WebsiteAddress;
        }
        String menuPrf = app_WebsiteAddress.endsWith("/") ? "menu_" : "/menu_";
        if (menuIdx.startsWith("menu_")) {
            menuIdx = menuIdx.substring(5);
        }
        String text = app_WebsiteAddress + menuPrf + menuIdx;
        return text;
    }

    // ====================================services=====================================
    @RequestMapping(value = "/{client}/textcontentJSon/{keyStr}", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJsonByKey1(
            @PathVariable(CC.CLIENT)
            String client,
            @PathVariable("keyStr")
            String keyStr,
            HttpServletRequest request) {

        if (hasNotLoggedIn(request)) {
            dirtFlagCommonText = TaoUtil.switchClient(request, client);
        }
        return showJsonByKey(keyStr, request);
    }

    @RequestMapping(value = "textcontentJSon/{keyStr}", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJsonByKey(
            @PathVariable("keyStr")
            String keyStr,
            HttpServletRequest request) {

        makesureSessionInitialized(request);// if first time visit, session is till empty, then initialise it.

        HttpSession session = request.getSession();
        Object langPrf = TaoUtil.getLangPrfWithDefault(request);
        Person person = TaoUtil.getCurPerson(request);
        TextContent textContent = TextContent.findContentsByKeyAndPerson(langPrf + keyStr, person);
        if (textContent == null) {
            textContent = new TextContent();
            textContent.setPosInPage(langPrf + keyStr);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(textContent.toJson(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = CC.resetQRCode, method = RequestMethod.POST, produces = "text/html")
    public String generateQRCode(
            Model model,
            @RequestParam(CC.position)
            String menuIdx,
            HttpServletRequest request) {
        String filePath = "QRCode_" + menuIdx;

        String text = composeQRCodeText(request, menuIdx);

        Person person = TaoUtil.getCurPerson(request);

        generateQRCode(request, filePath, text, person);
        return buildPageForMenu(model, request, null);
    }

    private void generateQRCode(
            HttpServletRequest request,
            String filePath,
            String text,
            Person person) {
        if (StringUtils.isBlank(text)) {
            TaoDebug.error("non-null parameter expected! param name : {}", "text");
            return;
        }
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); // routine code.

        MediaUpload media = MediaUpload.findMediaByKeyAndPerson(filePath, person);
        if (media == null) {
            media = new MediaUpload();
        }

        String format = ".jpg";
        media.setContentType(format);
        media.setFilepath(filePath);

        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 300, 300, hints);
            BufferedImage image = TaoImage.toBufferedImage(bitMatrix);
            // the following line could die if you debug over it:(
            image = TaoImage.resizeImage(request, image, media.getFilepath());
            byte[] tContent = TaoImage.getByteFormateImage(image, format);

            MediaUpload logo = MediaUpload.findMediaByKeyAndPerson("logo", person);
            byte[] composedPic = logo != null ? TaoImage.composePic(image, logo.getContent()) : null;

            media.setContent(composedPic != null ? composedPic : tContent);
            media.setFilesize(media.getContent().length);
        } catch (Exception e) {
            TaoDebug.error("got exception when resizing the image! e:{}", e);
        }
        media.setPerson(person);
        media.persist();
    }

    // ==================================alive check====================================
    @RequestMapping(value = "stgocheck/{keyStr}", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> stgocheck(
            @PathVariable("keyStr")
            String keyStr,
            HttpServletRequest request) {
        if (!keyStr.equals("stgo"))
            return null;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        Person person = TaoUtil.getAdvPerson();
        Customize customize = Customize.findCustomizeByKeyAndPerson(keyStr, person);
        // for first time, put in how long it has run.
        if (customize == null) {
            customize = new Customize();
            customize.setPerson(person);
            customize.setCusKey("stgo");
            customize.setCusValue("s");
            customize.persist();
        }
        return new ResponseEntity<String>(customize.toJson(), headers, HttpStatus.OK);
    }

    /*
     * seems like when no image set for generalServicePage, it will send out request like getImage/{id} (id is empty).
     * in that case, it will be considered as a client, and go into systemCommandCheck.
     */
    private String systemCommandCheck(
            Model model,
            HttpServletRequest request,
            String client) {
        if ("login".equals(client) || "signup".equals(client)) {
            return client;
        } else if ("customizes".equals(client)) {
            CustomizeController customizeController =
                    SpringApplicationContext.getApplicationContext().getBean("customizeController",
                            CustomizeController.class);
            return customizeController.list(null, null, "cusKey", "ASC", model, request);
        } else if ("style2".equals(client)) {
            String tUserName = userContextService.getCurrentUserName();
            Person person = TaoUtil.getCurPerson(request);
            if (person.getName().equalsIgnoreCase(tUserName)) {
                initializeToStyle2(request, person);
            }
        }
        return null;
    }

    private void initializeToStyle1(
            HttpServletRequest request,
            Person person) {
        // ============customizes==================
        // app_name
        createACustomize(request, CC.app_name, person.getName(), person);
        // language
        createACustomize(request, "show_AboveMenu", "true", person);
        createACustomize(request, "lang1", "fr", person);
        createACustomize(request, "lang2", "en", person);
        // contentTypes
        createACustomize(request, "CONTENTTYPE_1", "HTML", person);
        createACustomize(request, "CONTENTTYPE_2", "FEATURE", person);
        createACustomize(request, "CONTENTTYPE_3", "SERVICE", person);
        createACustomize(request, "CONTENTTYPE_10", "LOCATION", person);
        // location
        createACustomize(request, "MAP_POS_X2", "45.497634", person);
        createACustomize(request, "MAP_POS_Y2", "-73.554010", person);
        // hide something
        createACustomize(request, "show_footArea", "", person);
        // ================textContent=================
        generateTextContent("en_menu_1", "ABOUT " + person.getName().toUpperCase(), person);
        generateTextContent("en_menu_2", "FEATURE", person);
        generateTextContent("en_menu_3", "CATALOGUE", person);
        generateTextContent("en_menu_10", "CONTACT", person);

        generateTextContent("fr_menu_1", "A PROPOSE" + person.getName().toUpperCase(), person);
        generateTextContent("fr_menu_2", "RECOMMANDE", person);
        generateTextContent("fr_menu_3", "CATALOGUE", person);
        generateTextContent("fr_menu_10", "CONTACT", person);

        generateTextContent("zh_menu_1", "关于" + person.getName().toUpperCase(), person);
        generateTextContent("zh_menu_2", "今日推荐", person);
        generateTextContent("zh_menu_3", "产品目录", person);
        generateTextContent("zh_menu_10", "联系我们", person);

        // mainpage(html)
        TextContent textContent =
                TextContent.findContentsByKeyAndPerson("fr_html_1_0_0_Content", TaoUtil.getAdvPerson());
        if (textContent != null) {
            generateTextContent("fr_html_1_0_0_Content", textContent.getContent(), person);
        }
        textContent = TextContent.findContentsByKeyAndPerson("en_html_1_0_0_Content", TaoUtil.getAdvPerson());
        if (textContent != null) {
            generateTextContent("en_html_1_0_0_Content", textContent.getContent(), person);
        }
        textContent = TextContent.findContentsByKeyAndPerson("zh_html_1_0_0_Content", TaoUtil.getAdvPerson());
        if (textContent != null) {
            generateTextContent("zh_html_1_0_0_Content", textContent.getContent(), person);
        }

        // footer
        String footerContent =
                "© 2017 " + person.getName()
                        + ". powered by <a ref='http://www.shareTheGoodOnes.com/for_demo'>ShareTheGoodOnes.com</a>";
        generateTextContent("en_footer_copyright", footerContent, person);
        generateTextContent("fr_footer_copyright", footerContent, person);
        generateTextContent("zh_footer_copyright", footerContent, person);
        // contact us
        generateTextContent("en_text_Contact_Titles_2", "Contact", person);
        generateTextContent("fr_text_Contact_Titles_2", "Contact", person);
        generateTextContent("zh_text_Contact_Titles_2", "联系我们", person);

        textContent = TextContent.findContentsByKeyAndPerson("en_text_Contact_Contents_3", TaoUtil.getAdvPerson());
        if (textContent != null) {
            generateTextContent("en_text_Contact_Contents_3", textContent.getContent(), person);
        }
        textContent = TextContent.findContentsByKeyAndPerson("fr_text_Contact_Contents_3", TaoUtil.getAdvPerson());
        if (textContent != null) {
            generateTextContent("fr_text_Contact_Contents_3", textContent.getContent(), person);
        }
        textContent = TextContent.findContentsByKeyAndPerson("zh_text_Contact_Contents_3", TaoUtil.getAdvPerson());
        if (textContent != null) {
            generateTextContent("zh_text_Contact_Contents_3", textContent.getContent(), person);
        }

        // ================logo=================
        MediaUpload logo = MediaUpload.findMediaByKeyAndPerson("logo", TaoUtil.getAdvPerson());
        if (logo != null) {
            MediaUpload mediaUpload = new MediaUpload();
            mediaUpload.setContent(logo.getContent());
            mediaUpload.setContentType(logo.getContentType());
            mediaUpload.setFilesize(logo.getFilesize());
            mediaUpload.setPerson(person);
            mediaUpload.setFilepath("logo");
            mediaUpload.persist();
        }
        // ================bell================
        MediaUpload bell = MediaUpload.findMediaByKeyAndPerson("bell", TaoUtil.getAdvPerson());
        if (bell != null) {
            MediaUpload mediaUpload = new MediaUpload();
            mediaUpload.setContent(bell.getContent());
            mediaUpload.setContentType(bell.getContentType());
            mediaUpload.setFilesize(bell.getFilesize());
            mediaUpload.setPerson(person);
            mediaUpload.setFilepath("bell");
            mediaUpload.persist();
        }
        // ================qrcode=================
        createACustomize(request, "show_QRCode", "true", person);
        // app_WebsiteAddress
        String url = "http://www.shareTheGoodOnes.com/" + person.getName();
        generateQRCode(null, "QRCode_2", url + "/menu_2", person);
    }

    private void generateTextContent(
            String posInPage,
            String content,
            Person person) {
        TextContent textContent = TextContent.findContentsByKeyAndPerson(posInPage, person);
        textContent = textContent == null ? new TextContent() : textContent;
        textContent.setContent(content);
        textContent.setPosInPage(posInPage);
        textContent.setPerson(person);
        textContent.persist();
    }

    // it's goog for restaurant: bigger horizontal image and bigger space for layout.
    private void initializeToStyle2(
            HttpServletRequest request,
            Person person) {
        createACustomize(request, "img_service_h", "720", person);
        createACustomize(request, "img_service_w", "960", person);
        createACustomize(request, "img_service_thum_h", "230", person);
        createACustomize(request, "img_service_thum_w", "270", person);
        createACustomize(request, "service_number_lg", "4", person);
        createACustomize(request, "service_number_md", "6", person);
        createACustomize(request, "service_number_sm", "6", person);
        createACustomize(request, "service_number_xs", "12", person);
        createACustomize(request, "show_service_bell", "true", person);
        createACustomize(request, "show_service_cBox", "true", person);
        createACustomize(request, "need_calculate_price", "true", person);
        createACustomize(request, "app_ContentManager", "true", person);// when someone is promoted to be a manager,
                                                                        // shall we set his name here?

    }

    private void createACustomize(
            HttpServletRequest request,
            String cusKey,
            String value,
            Person person) {
        Customize customize = Customize.findCustomizeByKeyAndPerson(cusKey, person);
        customize = customize == null ? new Customize() : customize;
        customize.setCusKey(cusKey);
        customize.setCusValue(value);
        customize.setPerson(person);
        customize.persist();
        request.getSession().setAttribute(cusKey, value);
    }
}
