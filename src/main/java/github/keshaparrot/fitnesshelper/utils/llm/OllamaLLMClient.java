package github.keshaparrot.fitnesshelper.utils.llm;

import github.keshaparrot.fitnesshelper.domain.dto.ChatMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
public class OllamaLLMClient implements LLMClient {
    private final WebClient client;

    public OllamaLLMClient(@Qualifier("ollamaWebClient") WebClient client) {
        this.client = client;
    }

    @Override
    public String generate(String model, String prompt) {
        Map<String,Object> req = Map.of(
                "model",model,
                "prompt",prompt,
                "stream", false);

        Map<String,Object> resp = client
                .post()
                .uri("/api/generate")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return resp==null?"":resp.getOrDefault("response","").toString();
    }

    @Override
    public String chat(String model, List<ChatMessage> history) {
        Map<String,Object> req = Map.of(
                "model", model,
                "stream", false,
                "messages", history.stream()
                        .map(m -> Map.of("role", m.role(), "content", m.content()))
                        .toList()
        );

        Map<String,Object> resp = client
                .post()
                .uri("/api/chat")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (resp==null || !resp.containsKey("message")) return "";
        var msg = (Map<String,String>)resp.get("message");
        return msg.getOrDefault("content","");
    }

}
