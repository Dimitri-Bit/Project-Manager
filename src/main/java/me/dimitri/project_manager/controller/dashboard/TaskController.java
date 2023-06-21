package me.dimitri.project_manager.controller.dashboard;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import me.dimitri.project_manager.model.project.RecommendUser;
import me.dimitri.project_manager.model.project.Task;
import me.dimitri.project_manager.model.project.TaskID;
import me.dimitri.project_manager.service.TaskService;

import java.security.Principal;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Post("/new")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<?> createTask(@Nullable @Body Task task, Principal principal) {
        if (task != null) {
            return taskService.createTask(task, principal.getName());
        }
        return HttpResponse.badRequest();
    }

    @Post("/finish")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<?> finishTask(@Nullable @Body TaskID taskID, Principal principal) {
        if (taskID != null) {
            return taskService.finishTask(taskID.getTaskId(), principal.getName());
        }
        return HttpResponse.badRequest();
    }

    @Post("/open")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<?> openTask(@Nullable @Body TaskID taskID, Principal principal) {
        if (taskID != null) {
            return taskService.openTask(taskID.getTaskId());
        }
        return HttpResponse.badRequest();
    }

    @Post("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<?> deleteTask(@Nullable @Body TaskID taskID, Principal principal) {
        if (taskID != null) {
            return taskService.deleteTask(taskID.getTaskId());
        }
        return HttpResponse.badRequest();
    }

    @Post("/recommend")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<?> recommendUser(@Nullable @Body RecommendUser recommendUser, Principal principal) {
        if (recommendUser != null) {
            return taskService.recommendUsers2(recommendUser.getDescription(), recommendUser.getUsers());
        }
        return HttpResponse.badRequest();
    }

    @Get("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<?> getTasks(@PathVariable String id, Principal principal) {
        return taskService.findTasksTGroupId(id, principal.getName());
    }
}
