// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.stgo.taostyle.web.orders;

import com.stgo.taostyle.domain.orders.SizeProportion;
import com.stgo.taostyle.web.orders.SizeProportionController;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect SizeProportionController_Roo_Controller {
    
    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String SizeProportionController.create(@Valid SizeProportion sizeProportion, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, sizeProportion);
            return "sizeproportions/create";
        }
        uiModel.asMap().clear();
        sizeProportion.persist();
        return "redirect:/sizeproportions/" + encodeUrlPathSegment(sizeProportion.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id}", produces = "text/html")
    public String SizeProportionController.show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("sizeproportion", SizeProportion.findSizeProportion(id));
        uiModel.addAttribute("itemId", id);
        return "sizeproportions/show";
    }
    
    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String SizeProportionController.update(@Valid SizeProportion sizeProportion, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, sizeProportion);
            return "sizeproportions/update";
        }
        uiModel.asMap().clear();
        sizeProportion.merge();
        return "redirect:/sizeproportions/" + encodeUrlPathSegment(sizeProportion.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String SizeProportionController.updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, SizeProportion.findSizeProportion(id));
        return "sizeproportions/update";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String SizeProportionController.delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        SizeProportion sizeProportion = SizeProportion.findSizeProportion(id);
        sizeProportion.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/sizeproportions";
    }
    
    String SizeProportionController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }
    
}
