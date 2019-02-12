package com.leyou.page.web;

import com.leyou.page.pojo.User;
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

        User user = new User();
        user.setAge(21);
        user.setName("hftang");
        user.setFriend(new User("李小龙",20,null));

        model.addAttribute("user",user);


//        model.addAttribute("msg","hello, thymeleaf AAAA");

        return "hello";
    }
}
