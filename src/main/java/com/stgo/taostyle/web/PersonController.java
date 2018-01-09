package com.stgo.taostyle.web;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.stgo.taostyle.backend.security.TaoEncrypt;
import com.stgo.taostyle.backend.security.UserContextService;
import com.stgo.taostyle.domain.Person;

@RequestMapping("/people")
@Controller
@RooWebScaffold(path = "people", formBackingObject = Person.class)
@RooWebJson(jsonObject = Person.class)
public class PersonController {

    @Inject
    private UserContextService userContextService;

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(
            Model uiModel,
            HttpServletRequest request) {
        Person person = TaoUtil.getCurPerson(request);
        populateEditForm(uiModel, person != null ? person : new Person());
        return "people/create";
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(
            @Valid Person person,
            BindingResult bindingResult,
            Model uiModel,
            HttpServletRequest httpServletRequest) {

        if (isItHacking(person, httpServletRequest)) {
            return "login";
        }

        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, person);
            return "people/create";
        }
        uiModel.asMap().clear();
        String password = person.getPassword();
        if (!StringUtils.isBlank(password)) {
            String encryptedPassword = TaoEncrypt.encryptPassword(password);
            Person personInDB = Person.findPersonByName(person.getName());
            if (!personInDB.getPassword().equals(encryptedPassword)) {
                personInDB.setPassword(encryptedPassword);
                personInDB.persist();
            }
        }
        return "redirect:/people/" + encodeUrlPathSegment(person.getId().toString(), httpServletRequest);
    }

    private boolean isItHacking(
            Person person,
            HttpServletRequest request) {
        Person curPerson = (Person) request.getSession().getAttribute(CC.CLIENT);
        if (curPerson == null || person == null || !curPerson.getName().equals(person.getName())) {
            TaoDebug.warn(TaoDebug.getSB(request.getSession()),
                    "unexpected visit to PersonController, curPerson: {}, client: {}", curPerson, person);
            return true;
        }
        return false;
    }

    private boolean isItHacking(
            long id,
            HttpServletRequest request) {
        Person curPerson = (Person) request.getSession().getAttribute(CC.CLIENT);
        if (curPerson == null || curPerson.getId() != id) {
            TaoDebug.warn(TaoDebug.getSB(request.getSession()),
                    "unexpected visit to PersonController, curPerson: {}, person id: {}", curPerson, id);
            return true;
        }
        return false;
    }

    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(
            @PathVariable("id") Long id,
            Model uiModel,
            HttpServletRequest httpServletRequest) {

        if (isItHacking(id, httpServletRequest)) {
            return "login";
        }

        uiModel.addAttribute("person", Person.findPerson(id));
        uiModel.addAttribute("itemId", id);
        return "people/show";
    }

    @RequestMapping(produces = "text/html")
    public String list(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sortFieldName", required = false) String sortFieldName,
            @RequestParam(value = "sortOrder", required = false) String sortOrder,
            Model uiModel,
            HttpServletRequest httpServletRequest) {
        String name = userContextService.getCurrentUserName();
        if (!"stgo".equals(name) && !CC.ADV_USER.equals(name)) {
            return "login";
        }

        if (StringUtils.isBlank(sortFieldName)) {
            sortFieldName = "name";
        }
        if (StringUtils.isBlank(sortOrder)) {
            sortOrder = "ASC";
        }

        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("people", Person.findPersonEntries(firstResult, sizeNo, sortFieldName, sortOrder));
            float nrOfPages = (float) Person.countPeople() / sizeNo;
            uiModel.addAttribute("maxPages",
                    (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("people", Person.findAllPeople(sortFieldName, sortOrder));
        }
        return "people/list";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(
            @PathVariable("id") Long id,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            Model uiModel,
            HttpServletRequest request) {

        Person curPerson = TaoUtil.getCurPerson(request);
        if (!"stgo".equals(curPerson.getName()) && !CC.ADV_USER.equals(curPerson.getName())) {
            return "login";
        }

        Person person = Person.findPerson(id);
        TaoDbUtil.cleanPersonRecords(person);

        //
        person.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/people";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(
            @Valid Person person,
            BindingResult bindingResult,
            Model uiModel,
            HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, person);
            return "people/update";
        }
        uiModel.asMap().clear();
        person.merge();
        return "redirect:/people/" + encodeUrlPathSegment(person.getId().toString(), httpServletRequest);
    }
}
