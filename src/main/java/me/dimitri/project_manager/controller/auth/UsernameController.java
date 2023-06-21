package me.dimitri.project_manager.controller.auth;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import me.dimitri.project_manager.service.UserService;

import java.security.Principal;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/util")
public class UsernameController {

    private final UserService userService;

    public UsernameController(UserService userService) {
        this.userService = userService;
    }

//    @Get("/username")
//    @Produces(MediaType.APPLICATION_JSON)
//    public HttpResponse<?> getOwnerProjects(Principal principal) {
//        return HttpResponse.ok("{\"message\": \"" + principal.getName() + "\"}");
//    }

    @Get("/username")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<?> getUsername(Principal principal) {
        String username = principal.getName();
        String picture = userService.userImage(username);
        String json = "{\"message\": \"" + username + "\", \"picture\": \"" + picture + "\"}";
        return HttpResponse.ok(json);
    }

}
