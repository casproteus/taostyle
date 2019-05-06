package com.stgo.taostyle.web;

import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;
import com.stgo.taostyle.domain.Customize;
import com.stgo.taostyle.domain.Person;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RequestMapping("/customizes")
@Controller
public class CustomizeController {

    @RequestMapping(produces = "text/html")
    public String list(
            @RequestParam(value = "page", required = false)
            Integer page,
            @RequestParam(value = "size", required = false)
            Integer size,
            @RequestParam(value = "sortFieldName", defaultValue = "cusKey", required = false)
            String sortFieldName,
            @RequestParam(value = "sortOrder", defaultValue = "ASC")
            String sortOrder,
            Model uiModel,
            HttpServletRequest request) {

        Person person = TaoUtil.getCurPerson(request);
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("customizes", Customize.findOrderedCustomizeEntriesByPerson(firstResult, sizeNo,
                    sortFieldName, sortOrder, person));
            float nrOfPages = (float) Customize.countCustomizesByPerson(person) / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1
                    : nrOfPages));
        } else {
            uiModel.addAttribute("customizes",
                    Customize.findAllOrderedCustomizesByPerson(sortFieldName, sortOrder, person));
        }
        return "customizes/list";
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(
            @Valid
            Customize customize,
            BindingResult bindingResult,
            Model uiModel,
            HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, customize);
            return "customizes/create";
        }
        uiModel.asMap().clear();
        Person person = TaoUtil.getCurPerson(request);
        customize.setPerson(person);
        customize.persist();
        request.getSession().setAttribute(customize.getCusKey(), customize.getCusValue());
        return "redirect:/customizes/" + encodeUrlPathSegment(customize.getId().toString(), request);
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
        String cusKey = customize.getCusKey();
        if (cusKey.indexOf('_') == 2) {
            cusKey = cusKey.substring(3);
        }
        request.getSession().setAttribute(cusKey, customize.getCusValue());
        return "redirect:/customizes/" + encodeUrlPathSegment(customize.getId().toString(), request);
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

	@RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new Customize());
        return "customizes/create";
    }

	@RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("customize", Customize.findCustomize(id));
        uiModel.addAttribute("itemId", id);
        return "customizes/show";
    }

	@RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, Customize.findCustomize(id));
        return "customizes/update";
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

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(@PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        try {
            Customize customize = Customize.findCustomize(id);
            if (customize == null) {
                return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<String>(customize.toJson(), headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

	@RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        Person person = TaoUtil.getCurPerson(request);
        try {
            List<Customize> result = Customize.findAllCustomizesByPerson(person);
            return new ResponseEntity<String>(Customize.toJsonArray(result), headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

	@RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(@RequestBody String json, UriComponentsBuilder uriBuilder) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
            Customize customize = Customize.fromJsonToCustomize(json);
            customize.persist();
            RequestMapping a = (RequestMapping) getClass().getAnnotation(RequestMapping.class);
            headers.add("Location",uriBuilder.path(a.value()[0]+"/"+customize.getId().toString()).build().toUriString());
            return new ResponseEntity<String>(headers, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

	@RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJsonArray(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
            for (Customize customize: Customize.fromJsonArrayToCustomizes(json)) {
                customize.persist();
            }
            return new ResponseEntity<String>(headers, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody String json, @PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
            Customize customize = Customize.fromJsonToCustomize(json);
            customize.setId(id);
            if (customize.merge() == null) {
                return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<String>(headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
            Customize customize = Customize.findCustomize(id);
            if (customize == null) {
                return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
            }
            customize.remove();
            return new ResponseEntity<String>(headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
