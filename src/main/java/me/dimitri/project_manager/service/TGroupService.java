package me.dimitri.project_manager.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import io.micronaut.http.HttpResponse;
import jakarta.inject.Inject;
import me.dimitri.project_manager.model.project.TGroup;
import me.dimitri.project_manager.repository.TGroupRepository;
import me.dimitri.project_manager.util.StringValidationUtil;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class TGroupService implements TGroupRepository {

    /*
    TGroup stands for Task Group by the way.
     */

    private final MongoClient mongoClient;

    private final ProjectService projectService;

    @Inject
    public TGroupService(MongoClient mongoClient, ProjectService projectService) {
        this.mongoClient = mongoClient;
        this.projectService = projectService;
    }

    @Override
    public MongoCollection<Document> getCollection() {
        return mongoClient.getDatabase("taskGroup").getCollection("taskGroup");
    }

    /* Checks:
    1. Check name validity
    2. Check if project exists and if email has access to it
 */
    @Override
    public HttpResponse<?> createTGroup(TGroup tGroup, String email) {
        if (!StringValidationUtil.isValidLength(tGroup.getName())) {
            return HttpResponse.badRequest("{\"message\": \"Ime grupe mora biti između 3 i 30 slova\"}");
        }

        if (!projectService.hasAccess(tGroup.getProjectId(), email)) {
            return HttpResponse.unauthorized();
        }

        storeTGroup(tGroup);
        return HttpResponse.ok("{\"message\": \"Kategorija je uspešno napravljena\"}");
    }

    @Override
    public HttpResponse<?> deleteTGroup(String id, String email) {
        TGroup tGroup = findTGroupId(id);

        if (tGroup != null) {
            if (projectService.hasAccess(tGroup.getProjectId(), email)) {
                Document query = new Document("_id", new ObjectId(id));
                getCollection().deleteOne(query);

                return HttpResponse.ok("{\"message\": \"Kategorija uspešno obrisana\"}");
            }
            return HttpResponse.unauthorized();
        }
        return HttpResponse.notFound();
    }

    @Override
    public HttpResponse<?> findTGroupsProjectId(String projectId, String email) {
        Document query = new Document("projectId", projectId);
        List<TGroup> tGroups = new ArrayList<>();

        for (Document document : getCollection().find(query)) {
            tGroups.add(mapTGroup(document));
        }

        return HttpResponse.ok(tGroups);
    }

    @Override
    public HttpResponse<?> findProjectMembers(String id, String email) {
        Document query = new Document("_id", new ObjectId(id));

        MongoCursor<Document> cursor = getCollection().find(query).iterator();
        if (cursor.hasNext()) {
            TGroup group = mapTGroup(cursor.next());
            return projectService.findProjectMembers(group.getProjectId(), email);
        }
        return HttpResponse.notFound();
    }

    @Override
    public TGroup mapTGroup(Document document) {
        TGroup tGroup = new TGroup();
        tGroup.setId(document.getObjectId("_id").toString());
        tGroup.setName(document.getString("name"));
        tGroup.setDescription(document.getString("description"));
        tGroup.setProjectId(document.getString("projectId"));
        tGroup.setImageUrl(document.getString("imageUrl"));
        tGroup.setCreatorEmail(document.getString("creatorEmail"));
        return tGroup;
    }

    @Override
    public TGroup findTGroupId(String id) {
        Document query = new Document("_id", new ObjectId(id));

        MongoCursor<Document> cursor = getCollection().find(query).iterator();
        if (cursor.hasNext()) {
            return mapTGroup(cursor.next());
        }
        return null;
    }

    @Override
    public void storeTGroup(TGroup tGroup) {
        Document document = new Document("name", tGroup.getName())
                .append("description", tGroup.getDescription())
                .append("projectId", tGroup.getProjectId())
                .append("imageUrl", tGroup.getImageUrl())
                .append("creatorEmail", tGroup.getCreatorEmail());
        getCollection().insertOne(document);
    }

}
