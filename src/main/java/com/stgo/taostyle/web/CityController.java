package com.stgo.taostyle.web;

import com.stgo.taostyle.domain.City;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/citys")
@Controller
@RooWebScaffold(path = "citys", formBackingObject = City.class)
@RooWebJson(jsonObject = City.class)
public class CityController {
}
