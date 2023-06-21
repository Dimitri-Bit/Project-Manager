package me.dimitri.project_manager.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import io.micronaut.http.HttpResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import me.dimitri.project_manager.model.invite.Invite;
import me.dimitri.project_manager.model.project.Project;
import me.dimitri.project_manager.model.User;
import me.dimitri.project_manager.repository.InviteRepository;
import me.dimitri.project_manager.util.StringValidationUtil;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class InviteService implements InviteRepository {

    private final MongoClient mongoClient;

    private final UserService userService;
    private final ProjectService projectService;

    @Inject
    public InviteService(MongoClient mongoClient, UserService userService, ProjectService projectService) {
        this.mongoClient = mongoClient;
        this.userService = userService;
        this.projectService = projectService;
    }

    @Override
    public MongoCollection<Document> getCollection() {
        return mongoClient.getDatabase("invites").getCollection("invites");
    }

    /* Checks:
        1. Check invited email using StringValidationUtil
        2. Check if user under invited email exists
        3. Check if project under projectId exists
        4. Check if user is already part of project or the owner of project
        5. Check if inviter has already invited email
     */
    @Override
    public HttpResponse<?> createInvite(Invite invite) {
        String response = "Pozivnica je uspešno poslata";

        if (!StringValidationUtil.isValidEmail(invite.getInvitedEmail()) && !StringValidationUtil.isValidLength(invite.getInvitedEmail())) {
            response = "Invalidan email format";
            return HttpResponse.badRequest("{\"message\": \"" + response + "\"}");
        }

        User user = userService.findUserByEmail(invite.getInvitedEmail());
        if (user == null) {
            response = "Korisnik pod tom email adresom ne postoji";
            return HttpResponse.badRequest("{\"message\": \"" + response + "\"}");
        }

        Project project = (Project) projectService.findProjectId(invite.getProjectId(), "", false).body();
        if (project == null) {
            response = "Projekat ne postoji";
            return HttpResponse.badRequest("{\"message\": \"" + response + "\"}");
        }

        if (project.getOwnerEmail().equals(invite.getInvitedEmail())) {
            response = "Ova email adresa je već vlasnik ovoga projekta";
            return HttpResponse.badRequest("{\"message\": \"" + response + "\"}");
        }

        if (project.getTeamMemberEmails().contains(invite.getInvitedEmail())) {
            response = "Korisnik sa ovom email adresom je već dio projekta";
            return HttpResponse.badRequest("{\"message\": \"" + response + "\"}");
        }

        List<Invite> sentInvites = (List<Invite>) findSentInvites(invite.getInviterEmail()).body();
        if (sentInvites != null) {
            for (Invite invite1 : sentInvites) {
                if (invite1.getInvitedEmail().equals(invite.getInvitedEmail())) {
                    if (invite1.getProjectId().equals(invite.getProjectId())) {
                        response = "Već ste poslali ovu pozivnciu";
                        return HttpResponse.badRequest("{\"message\": \"" + response + "\"}");
                    }
                }
            }
        }

        invite.setProjectName(project.getName());
        storeInvite(invite);
        return HttpResponse.ok("{\"message\": \"" + response + "\"}");
    }

    @Override
    public HttpResponse<?> acceptInvite(String inviteId, String email) {
        Invite invite = findInviteById(inviteId);

        if (invite == null) {
            return HttpResponse.badRequest("{\"message\": \"Ova pozivnica više ne postoji\"}");
        }

        if (!invite.getInvitedEmail().equals(email)) {
            return HttpResponse.unauthorized();
        }

        Project project = (Project) projectService.findProjectId(invite.getProjectId(), "", false).body();
        if (project == null) {
            deleteInvite(invite, email);
            return HttpResponse.badRequest("{\"message\": \"Projekat na koji ste pozvani je izbrisan\"}");
        }

        project.getTeamMemberEmails().add(invite.getInvitedEmail());
        projectService.updateProject(invite.getProjectId(), project);
        deleteInvite(invite, email);
        return HttpResponse.ok("{\"message\": \"Pozivnica je uspešno prihvaćena\"}");
    }

    @Override
    public HttpResponse<?> findSentInvites(String email) {
        Document query = new Document("inviterEmail", email);
        List<Invite> invites = new ArrayList<>();

        for (Document document : getCollection().find(query)) {
            invites.add(mapInvite(document));
        }

        return HttpResponse.ok(invites);
    }

    @Override
    public HttpResponse<?> findReceivedInvites(String email) {
        Document query = new Document("invitedEmail", email);
        List<Invite> invites = new ArrayList<>();

        for (Document document : getCollection().find(query)) {
            invites.add(mapInvite(document));
        }

        return HttpResponse.ok(invites);
    }

    @Override
    public Invite findInviteById(String inviteId) {
        Document query = new Document("_id", new ObjectId(inviteId));

        MongoCursor<Document> cursor = getCollection().find(query).iterator();
        if (cursor.hasNext()) {
            return mapInvite(cursor.next());
        }

        return null;
    }

    @Override
    public Invite mapInvite(Document document) {
        Invite invite = new Invite();
        invite.setId(document.getObjectId("_id").toString());
        invite.setProjectId(document.getString("projectId"));
        invite.setProjectName(document.getString("projectName"));
        invite.setInviterEmail(document.getString("inviterEmail"));
        invite.setInvitedEmail(document.getString("invitedEmail"));
        return invite;
    }

    @Override
    public void storeInvite(Invite invite) {
        Document document = new Document("projectId", invite.getProjectId())
                .append("projectName", invite.getProjectName())
                .append("inviterEmail", invite.getInviterEmail())
                .append("invitedEmail", invite.getInvitedEmail());
        getCollection().insertOne(document);
    }

    @Override
    public void deleteInvite(Invite invite, String email) {
        Document query = new Document("_id", new ObjectId(invite.getId()));

        MongoCursor<Document> cursor = getCollection().find(query).iterator();
        if (cursor.hasNext()) {
            getCollection().deleteOne(cursor.next());
        }
    }
}
