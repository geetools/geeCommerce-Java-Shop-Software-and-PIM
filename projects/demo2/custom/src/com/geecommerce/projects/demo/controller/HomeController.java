package com.geecommerce .projects.demo.controller;

import com.geemvc.annotation.Controller;
import com.geemvc.annotation.Request;

/**
 * Created by Michael on 09.07.2016.
 */
@Controller
@Request("/demo-home")
public class HomeController {

    @Request("/")
    public String home() {

        System.out.println("~~~~~~~~~~~~~+ IN PROJECT's HOME!!!!!");

        return "view: demo-home";
    }
}
