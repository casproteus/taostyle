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
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;
import com.stgo.taostyle.domain.Person;
import com.stgo.taostyle.domain.TextContent;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RequestMapping("/textcontents")
@Controller
public class TextContentController {

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(
            @Valid
            TextContent textContent,
            BindingResult bindingResult,
            Model uiModel,
            HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, textContent);
            return "textcontents/update";
        }
        uiModel.asMap().clear();
        Person person = TaoUtil.getCurPerson(request);
        textContent.setPerson(person);
        textContent.merge();
        return "redirect:/textcontents/" + encodeUrlPathSegment(textContent.getId().toString(), request);
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(
            @Valid
            TextContent textContent,
            BindingResult bindingResult,
            Model uiModel,
            HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, textContent);
            return "textcontents/create";
        }
        uiModel.asMap().clear();
        Person person = TaoUtil.getCurPerson(request);
        textContent.setPerson(person);
        textContent.persist();
        return "redirect:/textcontents/" + encodeUrlPathSegment(textContent.getId().toString(), request);
    }

	@RequestMapping(value = "/{id}", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(@PathVariable("id") Long id) {
        TextContent textContent = TextContent.findTextContent(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (textContent == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(textContent.toJson(), headers, HttpStatus.OK);
    }

	@RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<TextContent> result = TextContent.findAllTextContents();
        return new ResponseEntity<String>(TextContent.toJsonArray(result), headers, HttpStatus.OK);
    }

	@RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(@RequestBody String json) {
        TextContent textContent = TextContent.fromJsonToTextContent(json);
        textContent.persist();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

	@RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJsonArray(@RequestBody String json) {
        for (TextContent textContent: TextContent.fromJsonArrayToTextContents(json)) {
            textContent.persist();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

	@RequestMapping(method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        TextContent textContent = TextContent.fromJsonToTextContent(json);
        if (textContent.merge() == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

	@RequestMapping(value = "/jsonArray", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJsonArray(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        for (TextContent textContent: TextContent.fromJsonArrayToTextContents(json)) {
            if (textContent.merge() == null) {
                return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("id") Long id) {
        TextContent textContent = TextContent.findTextContent(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (textContent == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        textContent.remove();
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

	@RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new TextContent());
        return "textcontents/create";
    }

	@RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("textcontent", TextContent.findTextContent(id));
        uiModel.addAttribute("itemId", id);
        return "textcontents/show";
    }

	@RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("textcontents", TextContent.findTextContentEntries(firstResult, sizeNo));
            float nrOfPages = (float) TextContent.countTextContents() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("textcontents", TextContent.findAllTextContents());
        }
        return "textcontents/list";
    }

	@RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, TextContent.findTextContent(id));
        return "textcontents/update";
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        TextContent textContent = TextContent.findTextContent(id);
        textContent.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/textcontents";
    }

	void populateEditForm(Model uiModel, TextContent textContent) {
        uiModel.addAttribute("textContent", textContent);
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
