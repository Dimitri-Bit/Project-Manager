package me.dimitri.project_manager.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Updates;
import io.micronaut.http.HttpResponse;
import jakarta.inject.Inject;
import me.dimitri.project_manager.model.User;
import me.dimitri.project_manager.model.project.Member;
import me.dimitri.project_manager.model.project.Project;
import me.dimitri.project_manager.repository.ProjectRepository;
import me.dimitri.project_manager.util.StringValidationUtil;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class ProjectService implements ProjectRepository {

    private final MongoClient mongoClient;

    private final UserService userService;

    @Inject
    public ProjectService(MongoClient mongoClient, UserService userService) {
        this.mongoClient = mongoClient;
        this.userService = userService;
    }

    @Override
    public MongoCollection<Document> getCollection() {
        return mongoClient.getDatabase("projects").getCollection("projects");
    }

    /* Checks:
        1. Project name length
        2. Uuh, I think that's it?
     */
    @Override
    public HttpResponse<?> createProject(Project project) {
        String response = "Projekat je uspešno napravljen";

        if (!StringValidationUtil.isValidLength(project.getName())) {
            response = "Ime projekta nije dovoljno/previše je dugo";
            return HttpResponse.badRequest("{\"message\": \"" + response + "\"}");
        }

        storeProject(project);
        return HttpResponse.ok("{\"message\": \"" + response + "\"}");
    }

    @Override
    public HttpResponse<?> findProjectsByOwner(String email) {
        Document query = new Document("ownerEmail", email);
        List<Project> projects = new ArrayList<>();

        for (Document document : getCollection().find(query)) {
            projects.add(mapProject(document));
        }

        return HttpResponse.ok(projects);
    }

    @Override
    public HttpResponse<?> findProjectsByMember(String email) {
        Document query = new Document("teamMemberEmails", email);
        List<Project> projects = new ArrayList<>();

        for (Document document : getCollection().find(query)) {
            projects.add(mapProject(document));
        }

        return HttpResponse.ok(projects);
    }

    /* Checks:
        1. If auth equals true check if email is the project owner or team member
     */
    @Override
    public HttpResponse<?> findProjectId(String id, String email, boolean auth) {
        Document query = new Document("_id", new ObjectId(id));

        MongoCursor<Document> cursor = getCollection().find(query).iterator();
        if (cursor.hasNext()) {
            Project project = mapProject(cursor.next());

            if (auth) {
                if (!project.getOwnerEmail().equals(email) &&
                        !project.getTeamMemberEmails().contains(email)) {
                    return HttpResponse.unauthorized();
                }
            }

            return HttpResponse.ok(project);
        }
        return HttpResponse.notFound();
    }

    @Override
    public HttpResponse<?> findProjectMembers(String id, String email) {
        Document query = new Document("_id", new ObjectId(id));
        List<Member> members = new ArrayList<>();

        MongoCursor<Document> cursor = getCollection().find(query).iterator();
        if (cursor.hasNext()) {
            Project project = mapProject(cursor.next());

            // Make sure request has access to project
            if (!project.getOwnerEmail().equals(email) &&
                    !project.getTeamMemberEmails().contains(email)) {
                return HttpResponse.unauthorized();
            }

            for (String memberEmail : project.getTeamMemberEmails()) {
                User user = userService.findUserByEmail(memberEmail);
                if (user != null) {
                    members.add(new Member(memberEmail, user.getFirstName() + " " + user.getLastName(), user.getProfilePictureUrl()));
                }
            }
            User owner = userService.findUserByEmail(project.getOwnerEmail());
            members.add(new Member(owner.getEmail(), owner.getFirstName() + " " + owner.getLastName(), owner.getProfilePictureUrl()));
            return HttpResponse.ok(members);
        }
        return HttpResponse.notFound();
    }

    @Override
    public HttpResponse<?> deleteProject(String id, String email) {
        Project project = (Project) findProjectId(id, "", false).body();

        if (project != null) {
            if (project.getOwnerEmail().equals(email)) {
                Document query = new Document("_id", new ObjectId(id));
                getCollection().deleteOne(query);

                return HttpResponse.ok("{\"message\": \"Projekat je uspešno obrisan\"}");
            }
            return HttpResponse.badRequest("{\"message\": \"Samo vlasnik može izbrisati ovaj projekat\"}");
        }
        return HttpResponse.notFound();
    }

    @Override
    public Project mapProject(Document document) {
        Project project = new Project();
        project.setId(document.getObjectId("_id").toString());
        project.setName(document.getString("name"));
        project.setOwnerEmail(document.getString("ownerEmail"));
        project.setTeamMemberEmails(document.getList("teamMemberEmails", String.class));
        project.setProjectImage(document.getString("projectImage"));
        return project;
    }

    @Override
    public void updateProject(String projectId, Project project) {
        Document query = new Document("_id", new ObjectId(projectId));

        Bson updates = Updates.combine(
                Updates.set("teamMemberEmails", project.getTeamMemberEmails())
        );

        getCollection().updateOne(query, updates);
    }

    @Override
    public void storeProject(Project project) {
        Document document = new Document("name", project.getName())
                .append("ownerEmail", project.getOwnerEmail())
                .append("teamMemberEmails", project.getTeamMemberEmails())
                .append("projectImage", project.getProjectImage());
        getCollection().insertOne(document);
    }

    /*
    I made this method after making a few service classes already, so it could
    have been used a lot in other parts of the project, but I don't think I have enough
    time to refactor that.
     */
    @Override
    public boolean hasAccess(String projectId, String email) {
        Project project = (Project) findProjectId(projectId, "", false).body();

        if (project != null) {
            return project.getOwnerEmail().equals(email) || project.getTeamMemberEmails().contains(email);
        }

        return false;
    }
}
