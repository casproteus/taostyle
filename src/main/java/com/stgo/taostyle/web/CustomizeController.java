package com.stgo.taostyle.web;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.stgo.taostyle.domain.Customize;
import com.stgo.taostyle.domain.Person;

@RequestMapping("/customizes")
@Controller
@RooWebScaffold(path = "customizes", formBackingObject = Customize.class)
@RooWebJson(jsonObject = Customize.class)
public class CustomizeController {

    @RequestMapping(produces = "text/html")
    public String list(
            @RequestParam(value = "page", required = false)
            Integer page,
            @RequestParam(value = "size", required = false)
            Integer size,
            @RequestParam(value = "sortFieldName", defaultValue = "cusKey", required = false)
            String sortFieldName,
            @RequestParam(value = "sortOrder", defaultValue = "ASC")
            String sortOrder,
            Model uiModel,
            HttpServletRequest request) {

        Person person = TaoUtil.getCurPerson(request);
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("customizes", Customize.findOrderedCustomizeEntriesByPerson(firstResult, sizeNo,
                    sortFieldName, sortOrder, person));
            float nrOfPages = (float) Customize.countCustomizesByPerson(person) / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1
                    : nrOfPages));
        } else {
            uiModel.addAttribute("customizes",
                    Customize.findAllOrderedCustomizesByPerson(sortFieldName, sortOrder, person));
        }
        return "customizes/list";
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(
            @Valid
            Customize customize,
            BindingResult bindingResult,
            Model uiModel,
            HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, customize);
            return "customizes/create";
        }
        uiModel.asMap().clear();
        Person person = TaoUtil.getCurPerson(request);
        customize.setPerson(person);
        customize.persist();
        request.getSession().setAttribute(customize.getCusKey(), customize.getCusValue());
        return "redirect:/customizes/" + encodeUrlPathSegment(customize.getId().toString(), request);
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(
            @Valid
            Customize customize,
            BindingResult bindingResult,
            Model uiModel,
            HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, customize);
            return "customizes/update";
        }
        uiModel.asMap().clear();
        Person person = TaoUtil.getCurPerson(request);
        customize.setPerson(person);
        customize.merge();
        request.getSession().setAttribute(customize.getCusKey(), customize.getCusValue());
        return "redirect:/customizes/" + encodeUrlPathSegment(customize.getId().toString(), request);
    }

    void populateEditForm(
            Model uiModel,
            Customize customize) {
        uiModel.addAttribute("customize", customize);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(
            @PathVariable("id")
            Long id,
            @RequestParam(value = "page", required = false)
            Integer page,
            @RequestParam(value = "size", required = false)
            Integer size,
            Model uiModel,
            HttpServletRequest request) {
        Customize customize = Customize.findCustomize(id);
        customize.remove();
        request.getSession().removeAttribute(customize.getCusKey());
        uiModel.asMap().clear();
        return "redirect:/customizes";
    }
}
