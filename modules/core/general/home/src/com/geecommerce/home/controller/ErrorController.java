package com.geecommerce.home.controller;

import com.geecommerce.core.App;
import com.geemvc.Results;
import com.geemvc.annotation.Controller;
import com.geemvc.annotation.Request;
import com.geemvc.bind.param.annotation.PathParam;
import com.geemvc.view.bean.Result;
import com.google.inject.Inject;

@Controller
@Request("/error")
public class ErrorController {
    @Inject
    protected App app;

    @Request("/{code}")
    public Result viewHome(@PathParam("code") String code) {

        System.out.println("GEEMVC ERROR !!!!!!!!!!!!!!!!! " + code + " - " + app.getOriginalURI() + " - " + app.getViewPath());

        return Results.view("error")
            .bind("errorCode", code);
    }
}