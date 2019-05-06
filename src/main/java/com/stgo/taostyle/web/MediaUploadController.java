package com.stgo.taostyle.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
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
import com.stgo.taostyle.backend.security.TaoEncrypt;
import com.stgo.taostyle.backend.security.UserContextService;
import com.stgo.taostyle.domain.MediaUpload;
import com.stgo.taostyle.domain.Person;
import com.stgo.taostyle.domain.TextContent;
import com.stgo.taostyle.domain.UserAccount;

@Controller
@RequestMapping("/mediauploads")
public class MediaUploadController {

    @Inject
    private UserContextService userContextService;

    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(
            @PathVariable("id")
            Long id,
            Model uiModel,
            HttpServletRequest request) {
        uiModel.addAttribute("mediaupload", MediaUpload.findMediaUpload(id));
        Person person = TaoUtil.getCurPerson(request);
        List<TextContent> items = TextContent.findContentsByMediaIdAndPerson(String.valueOf(id), person);
        if (items == null)
            items = new ArrayList<TextContent>();
        for (TextContent textContent : items) {
            String nameForDisplay = TaoEncrypt.stripUserName(textContent.getPublisher());
            textContent.setPublisher(nameForDisplay);
        }
        uiModel.addAttribute("textcontents", items);
        return "mediauploads/show";
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(
            @PathVariable("id")
            Long id,
            Model uiModel,
            HttpServletRequest request) {
        Person person = TaoUtil.getCurPerson(request);
        MediaUpload mediaUpload = MediaUpload.findMediaUpload(id);
        if (mediaUpload.getAudient() == null) {
            Object contentManager = request.getSession().getAttribute(CC.app_ContentManager);
            if (contentManager != null) {
                String userName = contentManager.toString();
                mediaUpload.setAudient(UserAccount.findUserAccountByName(TaoEncrypt.enrichName(request, userName)));
            }
        }
        uiModel.addAttribute("mediaupload", mediaUpload);
        uiModel.addAttribute("revisers", UserAccount.findAllUserAccountsByPerson(person));
        return "mediauploads/update";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(
            @Valid
            MediaUpload pMediaupload,
            BindingResult bindingResult,
            Model uiModel,
            HttpServletRequest request) {
        // if it's GE mode, then change the audient.
        Person person = TaoUtil.getCurPerson(request);
        Object contentManager = request.getSession().getAttribute(CC.app_ContentManager);
        if (contentManager != null) {
            UserAccount userAccount =
                    UserAccount.findUserAccountByName(TaoEncrypt.enrichName(request, contentManager.toString()));
            if (userAccount != null) {
                MediaUpload mediaUpload = MediaUpload.findMediaUpload(pMediaupload.getId());
                String tUserName = userContextService.getCurrentUserName();
                // @Note: this is temporal workout, we used the filePath to save the comment(TextContent's content).
                // if filepath content changed, added a textContent. change the status to commented.
                String comment = pMediaupload.getFilepath();
                if (!mediaUpload.getFilepath().equalsIgnoreCase(comment)) {
                    TextContent textContent = new TextContent();
                    textContent.setContent(comment);
                    textContent.setPosInPage(String.valueOf(pMediaupload.getId()));
                    textContent.setPublisher(tUserName);
                    textContent.setMarkDate(new Date());
                    textContent.setPerson(person);
                    textContent.persist();
                    if (!tUserName.equalsIgnoreCase(mediaUpload.getPublisher().getLoginname())) {
                        mediaUpload.setStatus(CC.STATUS_COMMENTED);
                    }
                }

                if (tUserName.equalsIgnoreCase(contentManager.toString())) {
                    // if audient changed, set status to waiting.
                    String audient = request.getParameter("audient");
                    UserAccount formerAudient = mediaUpload.getAudient();
                    if (formerAudient == null || !formerAudient.getLoginname().equals(audient)) {
                        if (contentManager.toString().equalsIgnoreCase(audient)) {
                            mediaUpload.setAudient(null);
                        } else {
                            mediaUpload.setAudient(UserAccount.findUserAccountByName(TaoEncrypt.enrichName(request,
                                    audient)));
                            mediaUpload.setStatus(CC.STATUS_WAITING);
                        }
                    }
                } else { // if it's not GE, set audient back to null, GE will deal with it. the status might be waiting
                         // if there's no textContent given yet.
                    mediaUpload.setAudient(null);
                }
                //
                mediaUpload.merge();
            }
        }

        return "redirect:/mediauploads/" + pMediaupload.getId();
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
        // remove relevant textContents
        Person person = TaoUtil.getCurPerson(request);
        List<TextContent> textContents = TextContent.findContentsByMediaIdAndPerson(String.valueOf(id), person);
        if (textContents != null) {
            for (int i = 0; i < textContents.size(); i++) {
                textContents.get(i).remove();
            }
        }
        // remove the mediaUpload.
        MediaUpload mediaUpload = MediaUpload.findMediaUpload(id);
        if (mediaUpload == null) {
            return "redirect:/dashboard";
        }
        mediaUpload.remove();
        return "redirect:/dashboard";
    }

    @RequestMapping(value = "/{id}/content")
    public String download(
            @PathVariable("id")
            Long id,
            HttpServletResponse response,
            Model uiModel) {
        MediaUpload doc = MediaUpload.findMediaUpload(id);
        try {
            response.setHeader("Content-Disposition", "inline;filename=\"" + doc.getFilepath() + doc.getContentType()
                    + "\"");
            OutputStream out = response.getOutputStream();
            response.setContentType(doc.getContentType());
            IOUtils.copy(new ByteArrayInputStream(doc.getContent()), out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(@PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        try {
            MediaUpload mediaUpload = MediaUpload.findMediaUpload(id);
            if (mediaUpload == null) {
                return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<String>(mediaUpload.toJson(), headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

	@RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        try {
            List<MediaUpload> result = MediaUpload.findAllMediaUploads();
            return new ResponseEntity<String>(MediaUpload.toJsonArray(result), headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

	@RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(@RequestBody String json, UriComponentsBuilder uriBuilder) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
            MediaUpload mediaUpload = MediaUpload.fromJsonToMediaUpload(json);
            mediaUpload.persist();
            RequestMapping a = (RequestMapping) getClass().getAnnotation(RequestMapping.class);
            headers.add("Location",uriBuilder.path(a.value()[0]+"/"+mediaUpload.getId().toString()).build().toUriString());
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
            for (MediaUpload mediaUpload: MediaUpload.fromJsonArrayToMediaUploads(json)) {
                mediaUpload.persist();
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
            MediaUpload mediaUpload = MediaUpload.fromJsonToMediaUpload(json);
            mediaUpload.setId(id);
            if (mediaUpload.merge() == null) {
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
            MediaUpload mediaUpload = MediaUpload.findMediaUpload(id);
            if (mediaUpload == null) {
                return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
            }
            mediaUpload.remove();
            return new ResponseEntity<String>(headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
