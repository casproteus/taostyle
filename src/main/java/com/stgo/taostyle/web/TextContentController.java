package com.stgo.taostyle.web;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.stgo.taostyle.domain.Person;
import com.stgo.taostyle.domain.TextContent;

@RequestMapping("/textcontents")
@Controller
@RooWebScaffold(path = "textcontents", formBackingObject = TextContent.class)
@RooWebJson(jsonObject = TextContent.class)
public class TextContentController {

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(
            @Valid
            TextContent textContent,
            BindingResult bindingResult,
            Model uiModel,
            HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, textContent);
            return "textcontents/update";
        }
        uiModel.asMap().clear();
        Person person = TaoUtil.getCurPerson(request);
        textContent.setPerson(person);
        textContent.merge();
        return "redirect:/textcontents/" + encodeUrlPathSegment(textContent.getId().toString(), request);
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(
            @Valid
            TextContent textContent,
            BindingResult bindingResult,
            Model uiModel,
            HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, textContent);
            return "textcontents/create";
        }
        uiModel.asMap().clear();
        Person person = TaoUtil.getCurPerson(request);
        textContent.setPerson(person);
        textContent.persist();
        return "redirect:/textcontents/" + encodeUrlPathSegment(textContent.getId().toString(), request);
    }
}
