package me.dimitri.project_manager.repository;

import com.mongodb.client.MongoCollection;
import io.micronaut.http.HttpResponse;
import me.dimitri.project_manager.model.project.Project;
import org.bson.Document;

public interface ProjectRepository {
    MongoCollection<Document> getCollection();
    HttpResponse<?> createProject(Project project);
    HttpResponse<?> findProjectsByOwner(String email);
    HttpResponse<?> findProjectsByMember(String email);
    HttpResponse<?> findProjectId(String id, String email, boolean auth);
    HttpResponse<?> findProjectMembers(String id, String email);
    HttpResponse<?> deleteProject(String id, String email);
    Project mapProject(Document document);
    void updateProject(String projectId, Project project);
    void storeProject(Project project);
    boolean hasAccess(String projectId, String email);
}
