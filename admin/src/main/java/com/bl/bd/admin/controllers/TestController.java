package com.bl.bd.admin.controllers;

import com.bl.db.admin.entity.TestEnity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by MK33 on 2016/9/13.
 */
@Controller
public class TestController {

    @RequestMapping(value = "/test-test", method = RequestMethod.GET)
    @ResponseBody
    public String test() {
        System.out.println("/test");
        return "index";
    }

    @RequestMapping(value = "/post-test")
    @ResponseBody
    public String post(@RequestParam(value = "gId", required = true) String gId,
                       @RequestParam(value = "chan", required = true) int chan, @RequestParam Map<String, String> re) {
        System.out.println("post");
        System.out.println(gId);
        System.out.println(chan);
        System.out.println(re.size());
//        System.out.println(s);
        return "post test";
    }

}
