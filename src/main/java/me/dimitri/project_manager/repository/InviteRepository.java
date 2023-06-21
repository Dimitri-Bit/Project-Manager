package me.dimitri.project_manager.repository;

import com.mongodb.client.MongoCollection;
import io.micronaut.http.HttpResponse;
import me.dimitri.project_manager.model.invite.Invite;
import org.bson.Document;

public interface InviteRepository {
    MongoCollection<Document> getCollection();
    HttpResponse<?> createInvite(Invite invite);
    HttpResponse<?> acceptInvite(String inviteId, String email);
    HttpResponse<?> findSentInvites(String email);
    HttpResponse<?> findReceivedInvites(String email);
    Invite findInviteById(String inviteId);
    Invite mapInvite(Document document);
    void storeInvite(Invite invite);
    void deleteInvite(Invite invite, String email);
}
