package me.dimitri.project_manager.controller.auth;

import io.micronaut.core.util.CollectionUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/login")
public class LoginAuthController {

    @Get("/auth")
    @View("auth/login")
    public HttpResponse<?> auth() {
        return HttpResponse.ok(CollectionUtils.mapOf("errors", false));
    }

    @Get("/authFailed")
    @View("auth/login")
    public HttpResponse<?> authFailed() {
        return HttpResponse.ok(CollectionUtils.mapOf("errors", true));
    }
}