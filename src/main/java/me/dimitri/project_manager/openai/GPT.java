package me.dimitri.project_manager.openai;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public class GPT {
    private static String taskName = "You are an API server that responds in a JSON format. Don't say anything else. Respond only with JSON. The user will provide you with a string that describes a name for a new to-do task. The name could be in any language, you need to take that input and re-write it so it sounds nicer while fixing any grammatical mistakes and while still keeping the original language. Make the new name input short, no more then one sentence. Respond in a JSON format, the json name of the response should be \"message\". Don't add anything else after you respond with the JSON.";
    private static String recommendName = "You are an API server that responds in a JSON format. Don't say anything else. Respond only with JSON. The user will provide you with a prompt asking you to recommend him a person that is provided in the prompt to finish a task. You will be provided with the description of the task that needs to be finished as well as a list of available users to finish that task. The list of users will contain their job title, currently assigned tasks (their work load), full name and email address. You need to determine who is best suited for that task based on the information given to you. Respond in a JSON format, make an array list that contains the email address or addresses (depending on the amount of users) of the users you recommend for the task. The array list's name should be \"message\". In your response also add another json string under the name \"explanation\", in this string you will provide a brief explanation of why you choose the users you have chosen. Chose the minimum amount of users needed to finish the task in a good time. The explanation response should be written in the Serbian language using latinica.";

    @Value("${config.openai}")
    String apiKey;

    public String getRecommendResponse(String prompt) {
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo")
                .temperature(0.8)
                .messages(
                        List.of(
                                new ChatMessage("system", recommendName),
                                new ChatMessage("user", prompt)))
                .build();

        OpenAiService openAiService = new OpenAiService(apiKey);

        StringBuilder builder = new StringBuilder();
        openAiService.createChatCompletion(chatCompletionRequest)
                .getChoices().forEach(choice -> {
                    builder.append(choice.getMessage().getContent());
                });

        return builder.toString();
    }
}
