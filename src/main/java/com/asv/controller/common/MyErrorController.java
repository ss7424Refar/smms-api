package com.asv.controller.common;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/")
public class MyErrorController implements ErrorController {

    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public String showError(HttpServletRequest httpServletRequest) {

        throw  new RuntimeException("内部/error跳转错误 ~");
    }

}
