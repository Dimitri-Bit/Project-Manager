package me.dimitri.project_manager.repository;

import com.mongodb.client.MongoCollection;
import io.micronaut.http.HttpResponse;
import me.dimitri.project_manager.model.project.Task;
import org.bson.Document;

public interface TaskRepository {
    MongoCollection<Document> getCollection();
    MongoCollection<Document> getRecommendationCollection();
    HttpResponse<?> createTask(Task task, String email);
    HttpResponse<?> findTasksTGroupId(String id, String email);
    HttpResponse<?> finishTask(String id, String email);
    HttpResponse<?> openTask(String id);
    HttpResponse<?> deleteTask(String id);
    Task mapTask(Document document);
    void storeTask(Task task);
    void storeTaskRecommendation(Task task);
    int findUserActiveTasks(String email);
}
