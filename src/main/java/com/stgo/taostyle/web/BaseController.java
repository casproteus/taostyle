package com.stgo.taostyle.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.ui.Model;

import com.stgo.taostyle.domain.Person;
import com.stgo.taostyle.domain.TextContent;

public class BaseController {

    protected boolean dirtFlagCommonText = false; // a dirty flag to force the page to refresh.

    protected void makesureCommonTextInitialized(
            Model model,
            HttpServletRequest request,
            String langPrf,
            Person person) {
        // get the session out
        HttpSession session = request.getSession();

        // if the language is switched, then reinit the texts on common area (header and footer)
        Object langInRequest = request.getParameter(CC.LANG); // language in request
        Object langInSession = session.getAttribute(CC.LANG); // language existing

        TaoDebug.info(TaoDebug.getSB(session),
                "start to makesureCommonTextInitialized, langParameter: {}; tLangExisting: {}, dirtFlagCommonText: {}, client:{}",
                langInRequest, langInSession, dirtFlagCommonText, person.getName());

        if (dirtFlagCommonText) {
            dirtFlagCommonText = false;
            reinitCommonText(session, langPrf, person);
        } else if (langInRequest != null) { // there's lang in request (user might clicked the language button)
            if (!langInRequest.equals(langInSession)) { // and the new language is different from old one.
                session.setAttribute(CC.LANG, langInRequest); // then set the new one into session.
                reinitCommonText(session, langPrf, person);
            }
        } else { // there's no language in request. (user is not clicking language button)
            if (langInSession == null) { // and no language set yet.
                session.setAttribute(CC.LANG, TaoUtil.getDefalutLang(request));
                reinitCommonText(session, langPrf, person);
            } else {// idle too long time, the content in menuContent might lost. not sure if it's possible?
                List<List<String>> menuContent = (List<List<String>>) session.getAttribute("menuContent");
                if (CollectionUtils.isEmpty(menuContent) || menuContent.get(0).size() == 0) {
                    reinitCommonText(session, langPrf, person);
                }
            }
        }
    }

    private void reinitCommonText(
            HttpSession session,
            String langPrf,
            Person person) {

        TaoDebug.info(TaoDebug.getSB(session), "start to reinitCommonText, lang is {}, client is: {}", langPrf,
                person.getName());

        List<List<String>> menuContent = TaoUtil.prepareMenuContent("menu_", langPrf, person);
        session.setAttribute("menuContent", menuContent);

        TaoUtil.initText(session, "text_footContent", 3, langPrf, person);
        TaoUtil.initText(session, "text_logContent", 3, langPrf, person);

        TextContent textContent = TextContent.findContentsByKeyAndPerson(langPrf + "footer_companyInfo", person);
        if (textContent != null) {
            session.setAttribute("footer_companyInfo", textContent.getContent());
        }

        textContent = TextContent.findContentsByKeyAndPerson(langPrf + "footer_copyright", person);
        if (textContent != null) {
            session.setAttribute("footer_copyright", textContent.getContent());
        }
    }
}
