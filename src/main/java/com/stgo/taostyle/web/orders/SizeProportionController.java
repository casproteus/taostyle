package com.stgo.taostyle.web.orders;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.stgo.taostyle.domain.orders.MainOrder;
import com.stgo.taostyle.domain.orders.SizeProportion;

@RequestMapping("/sizeproportions")
@Controller
@RooWebScaffold(path = "sizeproportions", formBackingObject = SizeProportion.class)
public class SizeProportionController {

    @RequestMapping(produces = "text/html")
    public String list(
            @RequestParam(value = "page", required = false)
            Integer page,
            @RequestParam(value = "size", required = false)
            Integer size,
            Model uiModel,
            HttpServletRequest request) {
        Long mainOrderId = Long.valueOf(request.getParameter("mainOrder"));
        uiModel.addAttribute("mainOrderId", mainOrderId);

        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("sizeproportions",
                    SizeProportion.findSizeProportionEntriesByMainOrder(mainOrderId, firstResult, sizeNo));
            float nrOfPages = (float) SizeProportion.countSizeProportions() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1
                    : nrOfPages));
        } else {
            uiModel.addAttribute("sizeproportions", SizeProportion.findAllSizeProportionsByMainOrder(mainOrderId));
        }
        return "sizeproportions/list";
    }

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(
            Model uiModel,
            HttpServletRequest request) {
        populateEditForm(uiModel, new SizeProportion());

        // because it's for small use, so do this way for now which wasted a findAll query.
        if (request.getParameter("mainOrderId") != null) {
            MainOrder mainOrder = MainOrder.findMainOrder(Long.valueOf(request.getParameter("mainOrderId")));
            ArrayList<MainOrder> mainOrders = new ArrayList<MainOrder>();
            if (mainOrder != null)
                mainOrders.add(mainOrder);
            uiModel.addAttribute("mainorders", mainOrders);
        }

        List<String[]> dependencies = new ArrayList<String[]>();
        if (MainOrder.countMainOrders() == 0) {
            dependencies.add(new String[] { "mainorder", "mainorders" });
        }
        uiModel.addAttribute("dependencies", dependencies);
        return "sizeproportions/create";
    }

    void populateEditForm(
            Model uiModel,
            SizeProportion sizeProportion) {
        uiModel.addAttribute("sizeProportion", sizeProportion);
        uiModel.addAttribute("mainorders", MainOrder.findAllMainOrders());
    }
}
