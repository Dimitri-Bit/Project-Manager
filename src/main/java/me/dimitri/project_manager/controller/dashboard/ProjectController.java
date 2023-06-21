package me.dimitri.project_manager.controller.dashboard;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;
import jakarta.inject.Inject;
import me.dimitri.project_manager.model.project.Project;
import me.dimitri.project_manager.model.project.ProjectID;
import me.dimitri.project_manager.service.ProjectService;

import java.security.Principal;
import java.util.List;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/project")
public class ProjectController {

    private final ProjectService projectService;

    @Inject
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Post("/new")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<?> createProject(@Nullable @Body Project project, Principal principal) {
        if (project != null) {
            project.setOwnerEmail(principal.getName());
            project.setTeamMemberEmails(List.of("null"));
            project.setProjectImage(null);
            return projectService.createProject(project);
        }
        return HttpResponse.badRequest();
    }

    @Post("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<?> deleteProject(@Nullable @Body ProjectID projectID, Principal principal) {
        if (projectID != null) {
            return projectService.deleteProject(projectID.getProjectId(), principal.getName());
        }
        return HttpResponse.badRequest();
    }

    @Get("/owner")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<?> getOwnerProjects(Principal principal) {
        return projectService.findProjectsByOwner(principal.getName());
    }

    @Get("/member")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<?> getMemberProjects(Principal principal) {
        return projectService.findProjectsByMember(principal.getName());
    }

    @Get("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<?> getProject(@PathVariable String id, Principal principal) {
        return projectService.findProjectId(id, principal.getName(), true);
    }

    @Get("/members/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<?> getProjectMembers(@PathVariable String id, Principal principal) {
        return projectService.findProjectMembers(id, principal.getName());
    }

    /*
    The weird trailing slash code was an attempt to fix a really minor bug that occurs when you add
    a slash at the end of the url, but it doesn't seem to work, so I'll leave it for now and fix it if I have time.
     */
    @View("dashboard/project")
    @Get("/view/{id}{trailingSlash:(/)?}")
    public HttpResponse<?> viewProject(@PathVariable String id, @PathVariable String trailingSlash) {
        return HttpResponse.ok();
    }

    @View("dashboard/task")
    @Get("/task/{id}{trailingSlash:(/)?}")
    public HttpResponse<?> viewTask(@PathVariable String id, @PathVariable String trailingSlash) {
        return HttpResponse.ok();
    }
}
