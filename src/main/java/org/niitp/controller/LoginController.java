package org.niitp.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

    @ApiOperation(value = "Возвращает страницу авторизации")
    @GetMapping(value = "/login")
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("custom-login");
        return modelAndView;
    }

    @ApiOperation(value = "Возвращает index.html")
    @GetMapping(value = "/passport")
    public ModelAndView onlogin() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        return modelAndView;
    }
}
