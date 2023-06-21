package me.dimitri.project_manager.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import io.micronaut.http.HttpResponse;
import jakarta.inject.Inject;
import me.dimitri.project_manager.model.User;
import me.dimitri.project_manager.model.project.RecommendUser;
import me.dimitri.project_manager.model.project.TGroup;
import me.dimitri.project_manager.model.project.Task;
import me.dimitri.project_manager.openai.GPT;
import me.dimitri.project_manager.repository.TaskRepository;
import me.dimitri.project_manager.util.StringValidationUtil;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class TaskService implements TaskRepository {

    private final MongoClient mongoClient;

    private final TGroupService tGroupService;
    private final ProjectService projectService;
    private final UserService userService;
    private final GPT gpt;

    @Inject
    public TaskService(MongoClient mongoClient, TGroupService tGroupService, ProjectService projectService, UserService userService, GPT gpt) {
        this.mongoClient = mongoClient;
        this.tGroupService = tGroupService;
        this.projectService = projectService;
        this.userService = userService;
        this.gpt = gpt;
    }

    @Override
    public MongoCollection<Document> getCollection() {
        return mongoClient.getDatabase("tasks").getCollection("tasks");
    }

    @Override
    public MongoCollection<Document> getRecommendationCollection() {
        return mongoClient.getDatabase("task-recommend").getCollection("task-recommend");
    }

    /* Checks:
        1. Check if task group exists
        2. Check if email has access
     */
    @Override
    public HttpResponse<?> createTask(Task task, String email) {
        TGroup tGroup = tGroupService.findTGroupId(task.getTaskGroupId());
        String response = "Zadatak je uspešno napravljen";

        if (tGroup == null) {
            return HttpResponse.badRequest();
        }

        if (!projectService.hasAccess(tGroup.getProjectId(), email)) {
            return HttpResponse.unauthorized();
        }

        if (!StringValidationUtil.isValidLength(task.getName())) {
            response = "Ime zadatka nije validne dužine";
            return HttpResponse.badRequest("{\"message\": \"" + response + "\"}");
        }

        if (task.getDescription().length() <= 3) {
            response = "Deskripcija zadatka nije dovoljno duga";
            return HttpResponse.badRequest("{\"message\": \"" + response + "\"}");
        }

        if (task.getAssignedEmails().size() == 0) {
            response = "Morate odabrati člana tima za ovaj zadatak";
            return HttpResponse.badRequest("{\"message\": \"" + response + "\"}");
        }

        storeTask(task);

        return HttpResponse.ok("{\"message\": \"" + response + "\"}");
    }

    /*
    Check if email has access first. I don't even know why I'm doing all these checks ahh... Pretty much all the
     search stuff is based on object ids and those are so random that nobody could really guess them.
     */
    @Override
    public HttpResponse<?> findTasksTGroupId(String id, String email) {
        TGroup tGroup = tGroupService.findTGroupId(id);

        if (tGroup == null) {
            return HttpResponse.notFound();
        }

        if (!projectService.hasAccess(tGroup.getProjectId(), email)) {
            return HttpResponse.unauthorized();
        }

        Document query = new Document("taskGroupId", id);
        List<Task> tasks = new ArrayList<>();

        for (Document document : getCollection().find(query)) {
            tasks.add(mapTask(document));
        }

        return HttpResponse.ok(tasks);
    }

    @Override
    public HttpResponse<?> finishTask(String id, String email) {
        Document query = new Document("_id", new ObjectId(id));

        MongoCursor<Document> cursor = getCollection().find(query).iterator();
        if (cursor.hasNext()) {
            Task task = mapTask(cursor.next());
            task.setFinished(true);
            storeTaskRecommendation(task);

            Bson updates = Updates.combine(
                    Updates.set("finished", true)
            );

            getCollection().updateOne(query, updates);
            return HttpResponse.ok("{\"message\": \"Zadatak je završen!\"}");
        }

        return HttpResponse.notFound();
    }

    @Override
    public HttpResponse<?> openTask(String id) {
        Document query = new Document("_id", new ObjectId(id));

        MongoCursor<Document> cursor = getCollection().find(query).iterator();
        if (cursor.hasNext()) {
            Bson updates = Updates.combine(
                    Updates.set("finished", false)
            );

            getCollection().updateOne(query, updates);
            return HttpResponse.ok("{\"message\": \"Zadatak je opet otvoren!\"}");
        }

        return HttpResponse.notFound();
    }

    @Override
    public HttpResponse<?> deleteTask(String id) {
        Document query = new Document("_id", new ObjectId(id));

        MongoCursor<Document> cursor = getCollection().find(query).iterator();
        if (cursor.hasNext()) {
            Task task = mapTask(cursor.next());

            if (!task.isFinished()) {
                return HttpResponse.badRequest("{\"message\": \"Zadatak prvo mora biti označen kao završen prije nego ga izbrišete\"}");
            }

            getCollection().deleteOne(query);
            return HttpResponse.ok("{\"message\": \"Zadatak je uspešno izbrisan\"}");
        }

        return HttpResponse.notFound();
    }

    @Override
    public Task mapTask(Document document) {
        Task task = new Task();
        task.setId(document.getObjectId("_id").toString());
        task.setTaskGroupId(document.getString("taskGroupId"));
        task.setName(document.getString("name"));
        task.setDescription(document.getString("description"));
        task.setLabelColor(document.getString("labelColor"));
        task.setAssignedEmails(document.getList("assignedEmails", String.class));
        task.setFinished(document.getBoolean("finished"));
        return task;
    }

    @Override
    public void storeTask(Task task) {
        Document document = new Document("taskGroupId", task.getTaskGroupId())
                .append("name", task.getName())
                .append("description", task.getDescription())
                .append("labelColor", task.getLabelColor())
                .append("assignedEmails", task.getAssignedEmails())
                .append("finished", task.isFinished());
        getCollection().insertOne(document);
    }

    @Override
    public void storeTaskRecommendation(Task task) {
        Document query = new Document("description", task.getDescription());

        // Check that the task is not already stored
        MongoCursor<Document> cursor = getRecommendationCollection().find(query).iterator();
        if (!cursor.hasNext()) {
            Document document = new Document("taskGroupId", task.getTaskGroupId())
                    .append("name", task.getName())
                    .append("description", task.getDescription())
                    .append("labelColor", task.getLabelColor())
                    .append("assignedEmails", task.getAssignedEmails())
                    .append("finished", task.isFinished());
            getRecommendationCollection().insertOne(document);
        }
    }

    /*
    We use this so determine how many tasks the users currently has assigned to them,
    so we can use it to recommend users for task. I am so tired and the deadline is so close.
     */
    public int findUserActiveTasks(String email) {
        Document query = new Document("assignedEmails", email);
        int amount = 0;

        for (Document document : getCollection().find(query)) {
            Task task = mapTask(document);
            if (!task.isFinished()) {
                amount++;
            }
        }

        return amount;
    }

    public HttpResponse<?> recommendUsers2(String taskDescription, List<String> emails) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Which user would be the best for this task? The task description is: \"").append(taskDescription).append("\". The list of users is:\n");

        for (String email : emails) { // We get each finished task that has been assigned to this email and save them to a list
            List<Task> finishedTasks = new ArrayList<>();
            Document query = new Document("assignedEmails", email);

            for (Document document : getRecommendationCollection().find(query)) {
                finishedTasks.add(mapTask(document));
            }

            // Now that we have the users finished tasks, amount of active tasks we are going to append the user to the ChatGPT prompt string
            User user = userService.findUserByEmail(email);
            int activeTasks = findUserActiveTasks(email);

            if (user != null) {
                prompt.append("Name: \"").append(user.getFirstName()).append(" ").append(user.getLastName()).append("\" Email: \"").append(user.getEmail()).append("\". Job Title: \"")
                        .append(user.getJobTitle()).append("\". ").append("Amount of currently assigned tasks: \"").append(activeTasks).append("\"");

                prompt.append(". Previously Finished Tasks: \"");

                // Next we are getting the last 2 finished tasks since we can't have too big of an input for ChatGPT.
                List<Task> lastTwoTasks;
                if (finishedTasks.size() >= 2) {
                    lastTwoTasks = finishedTasks.subList(finishedTasks.size() - 6, finishedTasks.size());
                } else {
                    lastTwoTasks = finishedTasks;
                }

                for (Task t : lastTwoTasks) {
                    prompt.append("Task Name: ").append(t.getName()).append(". Task Description: ").append(t.getDescription()).append("\". End of task");
                }

                // That's it for this user
                prompt.append(" End of user.\n");
            }
        }

        String json = gpt.getRecommendResponse(prompt.toString());
        System.out.println(json);
        return HttpResponse.ok(json);
    }
}
