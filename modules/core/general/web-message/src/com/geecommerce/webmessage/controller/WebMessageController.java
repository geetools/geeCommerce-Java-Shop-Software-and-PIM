package com.geecommerce.webmessage.controller;

import com.geecommerce.core.util.Json;
import com.geecommerce.core.web.BaseController;
import com.geecommerce.webmessage.service.WebMessageService;
import com.geemvc.annotation.Request;
import com.geemvc.bind.param.annotation.PathParam;
import com.geemvc.view.bean.Result;
import com.google.inject.Inject;

@Request("/webmessage")
public class WebMessageController extends BaseController {
    protected final WebMessageService webMessageService;

    protected String code = null;

    @Inject
    public WebMessageController(WebMessageService webMessageService) {
        this.webMessageService = webMessageService;
    }

    @Request("fetch/{code}")
    public Result fetch(@PathParam("code") String code) {
        return json(Json.toJson(webMessageService.fetch(code)));
    }
}
