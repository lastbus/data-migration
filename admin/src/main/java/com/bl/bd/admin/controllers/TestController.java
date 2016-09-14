package com.bl.bd.admin.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by MK33 on 2016/9/13.
 */
@Controller
public class TestController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String test() {
        System.out.println("index");
        return "index";
    }

}
