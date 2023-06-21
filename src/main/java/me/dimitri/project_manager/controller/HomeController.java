package me.dimitri.project_manager.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller
public class HomeController {

    @View("dashboard/home")
    @Get
    public HttpResponse<?> home() {
        return HttpResponse.ok();
    }


}