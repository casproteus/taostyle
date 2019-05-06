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
import com.stgo.taostyle.domain.Product;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RequestMapping("/products")
@Controller
public class ProductController extends BaseController {
    // this method is needed when editing a product item. the page was under the path of "\products"
    @RequestMapping("/getImage/{id}")
    public void getImage(
            @PathVariable("id")
            String id,
            HttpServletRequest request,
            HttpServletResponse response) {
        response.setContentType("image/jpeg");
        Person person = TaoUtil.getCurPerson(request);
        MediaUpload tMedia = MediaUpload.findMediaByKeyAndPerson(TaoUtil.getLangPrfWithDefault(request) + id, person);
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
    public String addProduct(
            Model uiModel,
            HttpServletRequest request) {
        Person person = TaoUtil.getCurPerson(request);
        String tPosistionStr = request.getParameter("position");
        if (tPosistionStr.startsWith("menu_"))
            tPosistionStr = "catalog" + tPosistionStr.substring(4);
        Product tProduct = new Product();
        tProduct.setPerson(person);
        tProduct.setC1(tPosistionStr);
        populateEditForm(uiModel, tProduct);

        TaoUtil.initTexts(uiModel, "notice_creating_product_", 1, TaoUtil.getLangPrfWithDefault(request), person);
        return "products/create";
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(
            @Valid
            Product product,
            BindingResult bindingResult,
            Model uiModel,
            HttpServletRequest request) {
        Person person = TaoUtil.getCurPerson(request);
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, product);
            return "products/create";
        }
        String tPartNo = product.getPartNo();
        Product tProduct = Product.findProductsByPartNoAndPerson(tPartNo, person);
        if (tProduct != null) {
            uiModel.addAttribute("Product_ErrorMessage", "The Part Number '" + tPartNo
                    + "' has been used, please check and input anOther one!");
            populateEditForm(uiModel, product);
            return "products/create";
        }
        String tURLStr = product.getC1();
        if (!tURLStr.toLowerCase().startsWith("catalog_")) {
            uiModel.addAttribute(
                    "Product_ErrorMessage",
                    "C1 value not correct! (you are supposed to use the default value, if you do need to change, make sure the format are correct.");
            populateEditForm(uiModel, product);
            return "products/create";
        }
        tURLStr = "menu" + tURLStr.substring(7);
        uiModel.asMap().clear();
        product.persist();
        return "redirect:/" + encodeUrlPathSegment(tURLStr, request);
    }

    @RequestMapping(produces = "text/html")
    public String list(
            HttpServletRequest request,
            @RequestParam(value = "page", required = false)
            Integer page,
            @RequestParam(value = "size", required = false)
            Integer size,
            Model uiModel) {

        Person person = TaoUtil.getCurPerson(request);
        String langPrf = TaoUtil.getLangPrfWithDefault(request);

        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("products", Product.findProductEntries(firstResult, sizeNo));
            float nrOfPages = (float) Product.countProducts() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1
                    : nrOfPages));
        } else {
            uiModel.addAttribute("products", Product.findAllProducts());
        }

        // set header and footer.
        makesureCommonTextInitialized(uiModel, request, langPrf, person);

        // get every number out from the menuIndex which looks like: 2_1_1 or 2_1 or 2
        int[] menuIdxes = new int[3];
        String tSt = request.getParameter("position");
        if (tSt != null) {
            if (tSt.startsWith("menu_")) {
                tSt = tSt.substring(5);
                uiModel.addAttribute("c1", tSt);
            } else if (tSt.startsWith("catalog_")) {
                tSt = tSt.substring(8);
            }
        }
        for (int i = 0; i < 3; i++) {
            int septatorIdx = tSt.indexOf("_"); // get the menuIdx
            if (septatorIdx > 0) {
                menuIdxes[i] = Integer.valueOf(tSt.substring(0, septatorIdx));
                tSt = tSt.substring(septatorIdx + 1);
            } else {
                menuIdxes[i] = Integer.valueOf(tSt.substring(0));
                break;
            }
        }
        return TaoUtil.initSubPage(request, uiModel, langPrf, menuIdxes[0], menuIdxes[1], menuIdxes[2], person);
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
        Product product = Product.findProduct(id);
        product.remove();
        uiModel.asMap().clear();

        uiModel.addAttribute("position", request.getParameter("position"));
        if (page != null && size != null) {
            uiModel.addAttribute("page", page.toString());
            uiModel.addAttribute("size", size.toString());
        }
        String tURLStr = "menu" + request.getParameter("position").substring(7);
        return "redirect:/" + encodeUrlPathSegment(tURLStr, request);
    }

    @RequestMapping(value = "/{partNo}", params = "form", produces = "text/html")
    public String updateForm(
            @PathVariable("partNo")
            String partNo,
            Model uiModel,
            HttpServletRequest request) {
        populateEditForm(uiModel, Product.findProductsByPartNoAndPerson(partNo, TaoUtil.getCurPerson(request)));
        return "products/update";
    }

	@RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new Product());
        return "products/create";
    }

	@RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("product", Product.findProduct(id));
        uiModel.addAttribute("itemId", id);
        return "products/show";
    }

	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid Product product, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, product);
            return "products/update";
        }
        uiModel.asMap().clear();
        product.merge();
        return "redirect:/products/" + encodeUrlPathSegment(product.getId().toString(), httpServletRequest);
    }

	void populateEditForm(Model uiModel, Product product) {
        uiModel.addAttribute("product", product);
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

	@RequestMapping(value = "/{id}", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(@PathVariable("id") Long id) {
        Product product = Product.findProduct(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (product == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(product.toJson(), headers, HttpStatus.OK);
    }

	@RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<Product> result = Product.findAllProducts();
        return new ResponseEntity<String>(Product.toJsonArray(result), headers, HttpStatus.OK);
    }

	@RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(@RequestBody String json) {
        Product product = Product.fromJsonToProduct(json);
        product.persist();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

	@RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJsonArray(@RequestBody String json) {
        for (Product product: Product.fromJsonArrayToProducts(json)) {
            product.persist();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

	@RequestMapping(method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        Product product = Product.fromJsonToProduct(json);
        if (product.merge() == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

	@RequestMapping(value = "/jsonArray", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJsonArray(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        for (Product product: Product.fromJsonArrayToProducts(json)) {
            if (product.merge() == null) {
                return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("id") Long id) {
        Product product = Product.findProduct(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (product == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        product.remove();
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }
}
