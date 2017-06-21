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
import com.stgo.taostyle.domain.orders.TaxonomyMaterial;

@RequestMapping("/taxonomymaterials")
@Controller
@RooWebScaffold(path = "taxonomymaterials", formBackingObject = TaxonomyMaterial.class)
public class TaxonomyMaterialController {

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
            uiModel.addAttribute("taxonomymaterials",
                    TaxonomyMaterial.findTaxonomyMaterialEntriesByMainOrder(mainOrderId, firstResult, sizeNo));
            float nrOfPages = (float) TaxonomyMaterial.countTaxonomyMaterials() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1
                    : nrOfPages));
        } else {
            uiModel.addAttribute("taxonomymaterials",
                    TaxonomyMaterial.findAllTaxonomyMaterialsByMainOrderID(mainOrderId));
        }
        return "taxonomymaterials/list";
    }

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(
            Model uiModel,
            HttpServletRequest request) {
        populateEditForm(uiModel, new TaxonomyMaterial());

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
        return "taxonomymaterials/create";
    }

    void populateEditForm(
            Model uiModel,
            TaxonomyMaterial taxonomyMaterial) {
        uiModel.addAttribute("taxonomyMaterial", taxonomyMaterial);
        uiModel.addAttribute("mainorders", MainOrder.findAllMainOrders());
    }
}
