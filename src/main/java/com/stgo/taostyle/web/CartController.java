package com.stgo.taostyle.web;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.joda.time.format.DateTimeFormat;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;
import com.stgo.taostyle.domain.Cart;
import com.stgo.taostyle.domain.Person;
import com.stgo.taostyle.domain.Product;
import com.stgo.taostyle.domain.UserAccount;

@RequestMapping("/carts")
@Controller
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

	@RequestMapping(value = "/{id}", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(@PathVariable("id") Long id) {
        Cart cart = Cart.findCart(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (cart == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(cart.toJson(), headers, HttpStatus.OK);
    }

	@RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<Cart> result = Cart.findAllCarts();
        return new ResponseEntity<String>(Cart.toJsonArray(result), headers, HttpStatus.OK);
    }

	@RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(@RequestBody String json) {
        Cart cart = Cart.fromJsonToCart(json);
        cart.persist();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

	@RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJsonArray(@RequestBody String json) {
        for (Cart cart: Cart.fromJsonArrayToCarts(json)) {
            cart.persist();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

	@RequestMapping(method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        Cart cart = Cart.fromJsonToCart(json);
        if (cart.merge() == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

	@RequestMapping(value = "/jsonArray", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJsonArray(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        for (Cart cart: Cart.fromJsonArrayToCarts(json)) {
            if (cart.merge() == null) {
                return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("id") Long id) {
        Cart cart = Cart.findCart(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (cart == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        cart.remove();
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

	@RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("cart", Cart.findCart(id));
        uiModel.addAttribute("itemId", id);
        return "carts/show";
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Cart cart = Cart.findCart(id);
        cart.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/carts";
    }

	void addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("cart_createddate_date_format", DateTimeFormat.patternForStyle("M-", LocaleContextHolder.getLocale()));
    }

	String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
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
