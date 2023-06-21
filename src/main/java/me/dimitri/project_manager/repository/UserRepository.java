package me.dimitri.project_manager.repository;

import com.mongodb.client.MongoCollection;
import io.micronaut.http.HttpResponse;
import me.dimitri.project_manager.model.User;
import org.bson.Document;

public interface UserRepository {
    MongoCollection<Document> getCollection();
    HttpResponse<?> createUser(User user);
    User findUserByEmail(String email);
    User mapUser(Document document);
    String userImage(String email);
    void storeUser(User user);
    void updateUserImage(String imageUrl, String email);
}
