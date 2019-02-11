package com.leyou.page.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author hftang
 * @date 2019-02-11 14:55
 * @desc
 */
@Controller
public class HelloController {

    @GetMapping("hello")
    public String toHello(Model model) {
        model.addAttribute("msg","hello, thymeleaf");

        return "hello";
    }
}
