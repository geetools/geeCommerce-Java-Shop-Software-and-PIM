package com.geecommerce.home.controller;

import com.geemvc.Results;
import com.geemvc.annotation.Controller;
import com.geemvc.annotation.Request;
import com.geemvc.view.bean.Result;

@Controller
@Request("/home")
public class HomeController {

    @Request("/")
    public Result ViewHome() {

        return Results.view("home");
    }
}
