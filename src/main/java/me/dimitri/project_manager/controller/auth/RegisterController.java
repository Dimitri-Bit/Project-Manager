package me.dimitri.project_manager.controller.auth;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;
import me.dimitri.project_manager.model.User;
import me.dimitri.project_manager.service.UserService;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/register")
public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @Post
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<?> createUser(@Nullable @Body User user) {
        if (user != null) {
            return userService.createUser(user);
        }
        return HttpResponse.badRequest();
    }

    @View("auth/register")
    @Get("/auth")
    public HttpResponse<?> registerView() {
        return HttpResponse.ok();
    }

}
