package me.dimitri.project_manager.controller.dashboard;


import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import me.dimitri.project_manager.model.project.GroupID;
import me.dimitri.project_manager.model.project.TGroup;
import me.dimitri.project_manager.service.TGroupService;

import java.security.Principal;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/group")
public class TGroupController {

    private final TGroupService tGroupService;

    @Inject
    public TGroupController(TGroupService tGroupService) {
        this.tGroupService = tGroupService;
    }

    @Post("/new")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<?> createTGroup(@Nullable @Body TGroup tGroup, Principal principal) {
        if (tGroup != null) {
            tGroup.setCreatorEmail(principal.getName());
            tGroup.setImageUrl(null);
            return tGroupService.createTGroup(tGroup, principal.getName());
        }
        return HttpResponse.badRequest();
    }

    @Post("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<?> deleteTGroup(@Nullable @Body GroupID groupID, Principal principal) {
        if (groupID != null) {
            return tGroupService.deleteTGroup(groupID.getGroupId(), principal.getName());
        }
        return HttpResponse.badRequest();
    }

    @Get("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<?> getTGroups(@PathVariable String id, Principal principal) {
        return tGroupService.findTGroupsProjectId(id, principal.getName());
    }

    @Get("/members/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<?> getProjectMembers(@PathVariable String id, Principal principal) {
        return tGroupService.findProjectMembers(id, principal.getName());
    }
}
