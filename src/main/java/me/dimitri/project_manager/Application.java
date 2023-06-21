package me.dimitri.project_manager;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import io.micronaut.runtime.Micronaut;

import java.util.List;

public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}