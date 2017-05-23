package com.stgo.taostyle.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.stgo.taostyle.domain.MediaUpload;
import com.stgo.taostyle.domain.Person;
import com.stgo.taostyle.domain.Product;

@RequestMapping("/game")
@Controller
@RooWebScaffold(path = "products", formBackingObject = Product.class)
@RooWebJson(jsonObject = Product.class)
public class GameController extends BaseController {
    // this method is needed when editing a product item. the page was under the path of "\products"
    @RequestMapping("/congratulations")
    public String addProduct(
            Model uiModel,
            HttpServletRequest request) {
        return "game/congratulations";
    }

    // this method is needed when editing a product item. the page was under the path of "\game"
    @RequestMapping("/getImage/{id}")
    public void getImage(
            @PathVariable("id")
            String id,
            HttpServletRequest request,
            HttpServletResponse response) {
        response.setContentType("image/jpeg");
        Person person = TaoUtil.getCurPerson(request);
        MediaUpload tMedia = MediaUpload.findMediaByKeyAndPerson(id, person);
        try {
            if (tMedia != null && tMedia.getContent() != null) {
                byte[] imageBytes = tMedia.getContent();
                response.getOutputStream().write(imageBytes);
                response.getOutputStream().flush();
            } else {
            }
        } catch (Exception e) {
            System.out.println("Exception occured when fetching img of ID:" + id + "! " + e);
        }
    }
}
