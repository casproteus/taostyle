package com.stgo.taostyle.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.stgo.taostyle.backend.security.TaoEncrypt;
import com.stgo.taostyle.domain.Person;
import com.stgo.taostyle.domain.UserAccount;

@RequestMapping("/useraccounts")
@Controller
@RooWebScaffold(path = "useraccounts", formBackingObject = UserAccount.class)
@RooWebJson(jsonObject = UserAccount.class)
public class UserAccountController extends BaseController {

    @RequestMapping(params = "currentUserID", produces = "text/html")
    public String updateUserAccountInfo(
            Model uiModel,
            @RequestParam(value = "currentUserID", required = false)
            Long currentUserID) {
        if (currentUserID == null) {
            // this might happen when session closed
            return "/";
        }
        populateEditForm(uiModel, UserAccount.findUserAccount(currentUserID));
        return "useraccounts/update";
    }

    @RequestMapping(params = "updateUserInfo", produces = "text/html")
    public String update(
            @Valid
            UserAccount userAccount,
            BindingResult bindingResult,
            Model uiModel,
            HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, userAccount);
            return "useraccounts/update";
        }
        uiModel.asMap().clear();
        userAccount.merge();
        return "redirect:/useraccounts/" + encodeUrlPathSegment(userAccount.getId().toString(), httpServletRequest);
    }

    @RequestMapping(produces = "text/html")
    public String list(
            @RequestParam(value = "page", required = false)
            Integer page,
            @RequestParam(value = "size", required = false)
            Integer size,
            @RequestParam(value = "sortFieldName", required = false)
            String sortFieldName,
            @RequestParam(value = "sortOrder", required = false)
            String sortOrder,
            Model uiModel,
            HttpServletRequest request) {
        Person person = TaoUtil.getCurPerson(request);
        List<UserAccount> useraccounts = null;
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            useraccounts =
                    UserAccount.findUserAccountEntriesByPerson(firstResult, sizeNo, sortFieldName, sortOrder, person);
            float nrOfPages = (float) UserAccount.countUserAccountsByPerson(person) / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1
                    : nrOfPages));
        } else {
            useraccounts = UserAccount.findAllUserAccountsByPerson(sortFieldName, sortOrder, person);
        }
        for (UserAccount useraccount : useraccounts) {
            useraccount.setLoginname(TaoEncrypt.stripUserName(useraccount.getLoginname()));
        }
        uiModel.addAttribute("useraccounts", useraccounts);
        return "useraccounts/list";
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(
            @Valid
            UserAccount userAccount,
            BindingResult bindingResult,
            Model uiModel,
            HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, userAccount);
            return "useraccounts/create";
        }
        uiModel.asMap().clear();
        userAccount.persist();
        return "redirect:/useraccounts/" + encodeUrlPathSegment(userAccount.getId().toString(), httpServletRequest);
    }

    @RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson(
            HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        Person person = TaoUtil.getCurPerson(request);
        try {
            List<UserAccount> result = UserAccount.findAllUserAccountsByPerson(person);
            return new ResponseEntity<String>(UserAccount.toJsonArray(result), headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("{\"ERROR\":" + e.getMessage() + "\"}", headers,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
