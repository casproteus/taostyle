package com.stgo.taostyle.web;

import java.util.List;

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
import com.stgo.taostyle.domain.Feature;
import com.stgo.taostyle.domain.MediaUpload;
import com.stgo.taostyle.domain.Person;
import com.stgo.taostyle.domain.TextContent;

@RequestMapping("/feature")
@Controller
@RooWebScaffold(path = "feature", formBackingObject = Feature.class)
@RooWebJson(jsonObject = Feature.class)
public class FeatureController {

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(
            HttpServletRequest request) {
        String langPrf = TaoUtil.getLangPrfWithDefault(request);

        Feature feature = new Feature();
        Person person = TaoUtil.getCurPerson(request);
        feature.setPerson(person);
        String menuIdx = request.getSession().getAttribute(CC.menuIdx).toString();
        feature.setMenuIdx(menuIdx);
        String refMenuIdx = request.getParameter("refMenuIdx");
        feature.setRefMenuIdx(refMenuIdx);

        StringBuilder posInPage = new StringBuilder(langPrf);
        int[] menuIdxes = TaoUtil.splitMenuIdxToAry(refMenuIdx);
        String menu = TaoUtil.completeMenuIdx(menuIdxes[0], menuIdxes[1], menuIdxes[2], posInPage.toString(), person);
        posInPage.append("service_").append(menu).append("_title");
        TextContent serviceTitle = TextContent.findContentsByKeyAndPerson(posInPage.toString(), person);
        TextContent groupTitle = new TextContent();
        groupTitle.setContent(serviceTitle == null ? "" : serviceTitle.getContent());
        groupTitle.setPerson(person);

        menuIdxes = TaoUtil.splitMenuIdxToAry(menuIdx);
        menu = TaoUtil.completeMenuIdx(menuIdxes[0], menuIdxes[1], menuIdxes[2], posInPage.toString(), person);
        String keyForGroupTitle = langPrf + "feature_" + menu + "_";
        List<String> keys = TextContent.findAllMatchedContent(keyForGroupTitle, CC.TextContentKey, person);
        for (int i = keys.size() - 1; i >= 0; i--) {
            if (!keys.get(i).endsWith("_groupTitle")) {
                keys.remove(i);
            }
        }
        groupTitle.setPosInPage(keyForGroupTitle + keys.size() + "_groupTitle");
        groupTitle.persist();

        List<String> mediaKeys = MediaUpload.listAllMediaUploadsKeyByKeyAndPerson("service_" + refMenuIdx, person);
        mediaKeys = TaoImage.stripThumOrderAndValidate(mediaKeys);
        StringBuilder sb = new StringBuilder();
        for (String key : mediaKeys) {
            sb.append(key);
            sb.append(',');
        }
        feature.setItemsToShow(sb.toString());

        feature.persist();
        return "redirect:/menu_" + menuIdx;
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
        return "redirect:/menu_" + request.getSession().getAttribute(CC.menuIdx);
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
