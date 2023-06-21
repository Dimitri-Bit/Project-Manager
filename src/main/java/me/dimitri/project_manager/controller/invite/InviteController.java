package me.dimitri.project_manager.controller.invite;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;
import jakarta.inject.Inject;
import me.dimitri.project_manager.model.invite.Invite;
import me.dimitri.project_manager.model.invite.InviteID;
import me.dimitri.project_manager.service.InviteService;

import java.security.Principal;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/invite")
public class InviteController {

    private final InviteService inviteService;

    @Inject
    public InviteController(InviteService inviteService) {
        this.inviteService = inviteService;
    }

    @Post("/new")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<?> createInvite(@Nullable @Body Invite invite, Principal principal) {
        if (invite != null) {
            invite.setInviterEmail(principal.getName());
            return inviteService.createInvite(invite);
        }
        return HttpResponse.badRequest();
    }

    @Post("/accept")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<?> acceptInvite(@Nullable @Body InviteID inviteID, Principal principal) {
        if (inviteID != null) {
            return inviteService.acceptInvite(inviteID.getInviteId(), principal.getName());
        }
        return HttpResponse.badRequest();
    }

    @Get
    @View("/dashboard/invite")
    public HttpResponse<?> invites() {
        return HttpResponse.ok();
    }

    @Get("/sent")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<?> getSentInvites(Principal principal) {
        return inviteService.findSentInvites(principal.getName());
    }

    @Get("/received")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<?> getReceivedInvites(Principal principal) {
        return inviteService.findReceivedInvites(principal.getName());
    }
}
