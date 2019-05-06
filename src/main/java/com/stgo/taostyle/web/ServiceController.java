package com.stgo.taostyle.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
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
import com.stgo.taostyle.domain.MediaUpload;
import com.stgo.taostyle.domain.Person;
import com.stgo.taostyle.domain.Service;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
@RequestMapping("/services")
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

	@RequestMapping(value = "/{id}", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(@PathVariable("id") Long id) {
        Service service = Service.findService(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (service == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(service.toJson(), headers, HttpStatus.OK);
    }

	@RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<Service> result = Service.findAllServices();
        return new ResponseEntity<String>(Service.toJsonArray(result), headers, HttpStatus.OK);
    }

	@RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(@RequestBody String json) {
        Service service = Service.fromJsonToService(json);
        service.persist();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

	@RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJsonArray(@RequestBody String json) {
        for (Service service: Service.fromJsonArrayToServices(json)) {
            service.persist();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

	@RequestMapping(method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        Service service = Service.fromJsonToService(json);
        if (service.merge() == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

	@RequestMapping(value = "/jsonArray", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJsonArray(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        for (Service service: Service.fromJsonArrayToServices(json)) {
            if (service.merge() == null) {
                return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("id") Long id) {
        Service service = Service.findService(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (service == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        service.remove();
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid Service service, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, service);
            return "services/create";
        }
        uiModel.asMap().clear();
        service.persist();
        return "redirect:/services/" + encodeUrlPathSegment(service.getId().toString(), httpServletRequest);
    }

	@RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new Service());
        return "services/create";
    }

	@RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("service", Service.findService(id));
        uiModel.addAttribute("itemId", id);
        return "services/show";
    }

	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid Service service, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, service);
            return "services/update";
        }
        uiModel.asMap().clear();
        service.merge();
        return "redirect:/services/" + encodeUrlPathSegment(service.getId().toString(), httpServletRequest);
    }

	@RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, Service.findService(id));
        return "services/update";
    }

	void populateEditForm(Model uiModel, Service service) {
        uiModel.addAttribute("service", service);
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
