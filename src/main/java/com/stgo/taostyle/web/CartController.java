package com.stgo.taostyle.web;

import java.util.ArrayList;
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

import com.stgo.taostyle.domain.Cart;
import com.stgo.taostyle.domain.Person;
import com.stgo.taostyle.domain.Product;
import com.stgo.taostyle.domain.UserAccount;

@RequestMapping("/carts")
@Controller
@RooWebScaffold(path = "carts", formBackingObject = Cart.class)
@RooWebJson(jsonObject = Cart.class)
public class CartController {

    void populateEditForm(
            Model uiModel,
            Cart cart,
            Person person) {
        uiModel.addAttribute("cart", cart);
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("products", Product.findAllProducts());
        uiModel.addAttribute("useraccounts", UserAccount.findAllUserAccountsByPerson(person));
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(
            @Valid
            Cart cart,
            BindingResult bindingResult,
            Model uiModel,
            HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            Person person = TaoUtil.getCurPerson(request);
            populateEditForm(uiModel, cart, person);
            return "carts/create";
        }
        uiModel.asMap().clear();
        cart.persist();
        return "redirect:/carts/" + encodeUrlPathSegment(cart.getId().toString(), request);
    }

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(
            Model uiModel,
            HttpServletRequest request) {
        Person person = TaoUtil.getCurPerson(request);
        populateEditForm(uiModel, new Cart(), person);
        List<String[]> dependencies = new ArrayList<String[]>();
        if (UserAccount.countUserAccountsByPerson(person) == 0) {
            dependencies.add(new String[] { "useraccount", "useraccounts" });
        }
        uiModel.addAttribute("dependencies", dependencies);
        return "carts/create";
    }

    @RequestMapping(produces = "text/html")
    public String list(
            @RequestParam(value = "page", required = false)
            Integer page,
            @RequestParam(value = "size", required = false)
            Integer size,
            Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("carts", Cart.findCartEntries(firstResult, sizeNo));
            float nrOfPages = (float) Cart.countCarts() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1
                    : nrOfPages));
        } else {
            uiModel.addAttribute("carts", Cart.findAllCarts());
        }
        addDateTimeFormatPatterns(uiModel);
        return "carts/list";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(
            @Valid
            Cart cart,
            BindingResult bindingResult,
            Model uiModel,
            HttpServletRequest request) {
        Person person = TaoUtil.getCurPerson(request);
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, cart, person);
            return "carts/update";
        }
        uiModel.asMap().clear();
        cart.merge();
        return "redirect:/carts/" + encodeUrlPathSegment(cart.getId().toString(), request);
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(
            @PathVariable("id")
            Long id,
            Model uiModel,
            HttpServletRequest request) {
        Person person = TaoUtil.getCurPerson(request);
        populateEditForm(uiModel, Cart.findCart(id), person);
        return "carts/update";
    }
}
