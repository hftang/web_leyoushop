package com.leyou.page.web;

import com.leyou.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * @author hftang
 * @date 2019-02-11 16:33
 * @desc
 */
@Controller
public class PageController {

    @Autowired
    private PageService pageService;

    @GetMapping("item/{id}.html")
    public String toItemPage(@PathVariable("id") Long spuId, Model model) {
        System.out.println("------>spuId:"+spuId);

        Map<String,Object> attributes=pageService.loadModel(spuId);



        model.addAllAttributes(attributes);


        return "item";
    }
}
