package com.stgo.taostyle.config.spring;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import com.stgo.taostyle.domain.Person;
import com.stgo.taostyle.domain.UserAccount;
import com.stgo.taostyle.web.CC;
import com.stgo.taostyle.web.TaoDebug;
import com.stgo.taostyle.web.TaoUtil;

public class KeepCurrentUserInSessionLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        // to make the page stay on the client's web site, instead of displaying for_demo's web site.
        if (authentication == null) {
            TaoDebug.error("non-null authentication expected when {}", "onLogoutSuccess");
        } else {
            Person person = Person.findPersonByName(authentication.getName());
            if (person == null) {
                UserAccount userAccount = UserAccount.findUserAccountByName(authentication.getName());
                person = userAccount.getPerson();
            }
            person.setPassword("");
            request.getSession().setAttribute(CC.CLIENT, person);
            TaoUtil.reInitSession(request.getSession(), person);
        }
        super.handle(request, response, authentication);
    }
}
