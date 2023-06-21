package me.dimitri.project_manager.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Updates;
import io.micronaut.http.HttpResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import me.dimitri.project_manager.model.User;
import me.dimitri.project_manager.repository.UserRepository;
import me.dimitri.project_manager.util.HashUtil;
import me.dimitri.project_manager.util.StringValidationUtil;
import org.bson.Document;
import org.bson.conversions.Bson;

@Singleton
public class UserService implements UserRepository {

    private final MongoClient mongoClient;

    @Inject
    public UserService(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    public MongoCollection<Document> getCollection() {
        return mongoClient.getDatabase("users").getCollection("users");
    }

    @Override
    public HttpResponse<?> createUser(User user) {
        String response = "Uspešna Registracija";

        if (!StringValidationUtil.isValidLength(user.getEmail())) {
            response = "Email adresa nije validne dužine";
            return HttpResponse.badRequest("{\"message\": \"" + response + "\"}");
        }

        if (!StringValidationUtil.isValidLength(user.getFirstName())) {
            response = "Prvo ime nije validne dužine";
            return HttpResponse.badRequest("{\"message\": \"" + response + "\"}");
        }

        if (!StringValidationUtil.isValidLength(user.getLastName())) {
            response = "Zadnje ime nije validne dužine";
            return HttpResponse.badRequest("{\"message\": \"" + response + "\"}");
        }

        if (!StringValidationUtil.isValidLength(user.getJobTitle())) {
            response = "Ime Profesije nije validne dužine";
            return HttpResponse.badRequest("{\"message\": \"" + response + "\"}");
        }

        if (!StringValidationUtil.isValidEmail(user.getEmail())) {
            response = "Email Adresa nije validnog formata";
            return HttpResponse.badRequest("{\"message\": \"" + response + "\"}");
        }

        if (!StringValidationUtil.isValidName(user.getFirstName())) {
            response = "Prvo ime nije validno";
            return HttpResponse.badRequest("{\"message\": \"" + response + "\"}");
        }

        if (!StringValidationUtil.isValidName(user.getLastName())) {
            response = "Zadnje ime nije validno";
            return HttpResponse.badRequest("{\"message\": \"" + response + "\"}");
        }

        if (!StringValidationUtil.isValidPassword(user.getPassword())) {
            response = "Lozinka mora biti duga između 3 i 30 karaktera i mora sadržati barem jedno slovo, jedan broj, jedan jedinstveni znak, kao i veliko i malo slovo.";
            return HttpResponse.badRequest("{\"message\": \"" + response + "\"}");
        }

        if (findUserByEmail(user.getEmail()) != null) {
            response = "Korisnik sa ovom email adresom već postoji";
            return HttpResponse.badRequest("{\"message\": \"" + response + "\"}");
        }

        storeUser(user);
        return HttpResponse.ok("{\"message\": \"" + response + "\"}");
    }

    @Override
    public User findUserByEmail(String email) {
        Document document = new Document("email", email);

        MongoCursor<Document> cursor = getCollection().find(document).iterator();
        if (cursor.hasNext()) {
            return mapUser(cursor.next());
        }
        return null;
    }

    @Override
    public User mapUser(Document document) {
        User user = new User();
        user.setId(document.getObjectId("_id").toString());
        user.setFirstName(document.getString("firstName"));
        user.setLastName(document.getString("lastName"));
        user.setJobTitle(document.getString("jobTitle"));
        user.setEmail(document.getString("email"));
        user.setPassword(document.getString("password"));
        user.setProfilePictureUrl(document.getString("profilePictureUrl"));
        return user;
    }

    @Override
    public String userImage(String email) {
        Document document = new Document("email", email);

        MongoCursor<Document> cursor = getCollection().find(document).iterator();
        if (cursor.hasNext()) {
            return mapUser(cursor.next()).getProfilePictureUrl();
        }
        return null;
    }

    @Override
    public void storeUser(User user) {
        Document document = new Document("firstName", user.getFirstName())
                .append("lastName", user.getLastName())
                .append("jobTitle", user.getJobTitle())
                .append("email", user.getEmail())
                .append("password", HashUtil.hashString(user.getPassword()))
                .append("profilePictureUrl", user.getProfilePictureUrl());
        getCollection().insertOne(document);
    }

    @Override
    public void updateUserImage(String imageUrl, String email) {
        Document query = new Document("email", email);

        Bson updates = Updates.combine(
                Updates.set("profilePictureUrl", imageUrl)
        );

        getCollection().updateOne(query, updates);
    }
}
