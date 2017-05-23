package com.stgo.taostyle.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.stgo.taostyle.domain.MediaUpload;
import com.stgo.taostyle.domain.Person;
import com.stgo.taostyle.domain.Service;

@RooWebJson(jsonObject = Service.class)
@Controller
@RequestMapping("/services")
@RooWebScaffold(path = "services", formBackingObject = Service.class)
public class ServiceController extends BaseController {
    @RequestMapping("/getImage/{id}")
    public void getImage(
            @PathVariable("id")
            String id,
            HttpServletRequest request,
            HttpServletResponse response) {
        response.setContentType("image/jpeg");
        Person person = TaoUtil.getCurPerson(request);
        String langPrf = TaoUtil.getLangPrfWithDefault(request);
        MediaUpload tMedia = MediaUpload.findMediaByKeyAndPerson(langPrf + id, person);
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

    @RequestMapping(params = "createform", produces = "text/html")
    public String addService(
            Model uiModel,
            HttpServletRequest request) {
        Person person = TaoUtil.getCurPerson(request);
        String tPosistionStr = request.getParameter("position");
        if (tPosistionStr.startsWith("menu_"))
            tPosistionStr = "catalog" + tPosistionStr.substring(4);
        Service tService = new Service();
        tService.setPerson(person);
        tService.setC1(tPosistionStr);

        populateEditForm(uiModel, tService);
        TaoUtil.initTexts(uiModel, "notice_creating_service_", 1, TaoUtil.getLangPrfWithDefault(request), person);
        return "services/create";
    }

    @RequestMapping(produces = "text/html")
    public String list(
            HttpServletRequest request,
            @RequestParam(value = "page", required = false)
            Integer page,
            @RequestParam(value = "size", required = false)
            Integer size,
            Model uiModel) {
        String tPosistionStr = request.getParameter("position");
        if (tPosistionStr.startsWith("menu_"))
            tPosistionStr = "service" + tPosistionStr.substring(4);
        uiModel.addAttribute("position", tPosistionStr);
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("services", Service.findServiceEntries(firstResult, sizeNo));
            float nrOfPages = (float) Service.countServices() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1
                    : nrOfPages));
        } else {
            uiModel.addAttribute("services", Service.findAllServices());
        }
        return "services/list";
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
        Service service = Service.findService(id);
        service.remove();
        uiModel.asMap().clear();

        uiModel.addAttribute("position", httpServletRequest.getParameter("position"));
        if (page != null && size != null) {
            uiModel.addAttribute("page", page.toString());
            uiModel.addAttribute("size", size.toString());
        }

        String tURLStr = "menu" + httpServletRequest.getParameter("position").substring(7);
        return "redirect:/" + encodeUrlPathSegment(tURLStr, httpServletRequest);
    }
}
