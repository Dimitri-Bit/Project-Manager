package me.dimitri.project_manager.repository;

import com.mongodb.client.MongoCollection;
import io.micronaut.http.HttpResponse;
import me.dimitri.project_manager.model.project.Project;
import me.dimitri.project_manager.model.project.TGroup;
import org.bson.Document;

public interface TGroupRepository {
    MongoCollection<Document> getCollection();
    HttpResponse<?> createTGroup(TGroup tGroup, String email);
    HttpResponse<?> deleteTGroup(String id, String email);
    HttpResponse<?> findTGroupsProjectId(String projectId, String email);
    HttpResponse<?> findProjectMembers(String id, String email);
    TGroup mapTGroup(Document document);
    TGroup findTGroupId(String id);
    void storeTGroup(TGroup tGroup);
}
