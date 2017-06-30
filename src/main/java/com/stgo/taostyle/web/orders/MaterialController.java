package com.stgo.taostyle.web.orders;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.stgo.taostyle.domain.TextContent;
import com.stgo.taostyle.domain.orders.MainOrder;
import com.stgo.taostyle.domain.orders.Material;
import com.stgo.taostyle.web.TaoUtil;

@RequestMapping("/materials")
@Controller
@RooWebScaffold(path = "materials", formBackingObject = Material.class)
public class MaterialController {

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
            uiModel.addAttribute("materials",
                    Material.findMaterialEntriesByMainOrderId(mainOrderId, firstResult, sizeNo));
            float nrOfPages = (float) Material.countMaterials() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1
                    : nrOfPages));
        } else {
            uiModel.addAttribute("materials", Material.findAllMaterialsByMainOrderId(mainOrderId));
        }
        return "materials/list";
    }

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(
            Model uiModel,
            HttpServletRequest request) {
        populateEditForm(uiModel, new Material());

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
        return "materials/create";
    }

    void populateEditForm(
            Model model,
            Material material) {
        model.addAttribute("material", material);
        model.addAttribute("mainorders", MainOrder.findAllMainOrders());
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(
            @Valid
            Material material,
            BindingResult bindingResult,
            Model uiModel,
            HttpServletRequest httpServletRequest) {
        Material tMaterial = Material.findMaterial(material.getId());
        uiModel.asMap().clear();
        tMaterial.setRemark(material.getRemark());
        tMaterial.persist();
        return "redirect:/showDetailOrder/"
                + encodeUrlPathSegment(tMaterial.getMainOrder().getId().toString(), httpServletRequest);
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
            HttpServletRequest httpServletRequest) {
        Material material = Material.findMaterial(id);
        MainOrder mainOrder = material.getMainOrder();
        String strFR =
                "redirect:/showDetailOrder/" + encodeUrlPathSegment(mainOrder.getId().toString(), httpServletRequest);
        material.remove();

        // check if there's no material left.
        List<Material> materials = Material.findAllMaterialsByMainOrder(mainOrder);
        if (materials == null || materials.size() == 0) {
            mainOrder.remove();
            return "redirect:/dashboard";
        } else {
            float totalPrice = 0;
            for (Material dish : materials) {
                totalPrice += Float.valueOf(dish.getDencity().substring(1));
            }
            mainOrder.setPayCondition(mainOrder.getPayCondition().substring(0, 1) + totalPrice);
            mainOrder.persist();
            uiModel.asMap().clear();
            return strFR;
        }
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(
            @Valid
            Material material,
            BindingResult bindingResult,
            Model uiModel,
            HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, material);
            // return "materials/create";
            return "materials/update";
        }
        uiModel.asMap().clear();
        // material.persist();
        material.merge();
        // return "redirect:/materials/" + encodeUrlPathSegment(material.getId().toString(), httpServletRequest);
        return "redirect:/mainorders/"
                + encodeUrlPathSegment(material.getMainOrder().getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(
            @PathVariable("id")
            Long id,
            Model model,
            HttpServletRequest request) {
        populateEditForm(model, Material.findMaterial(id));

        String langPrf = TaoUtil.getLangPrfWithDefault(request);
        List<String> quick_notes =
                TextContent.findAllMatchedContent(langPrf + "quick_note_%", "content", TaoUtil.getCurPerson(request));
        model.addAttribute("quick_note", quick_notes);

        return "materials/update";
    }
}
