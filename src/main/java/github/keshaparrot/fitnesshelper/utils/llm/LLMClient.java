package github.keshaparrot.fitnesshelper.utils.llm;

import github.keshaparrot.fitnesshelper.domain.dto.ChatMessage;

import java.util.List;

public interface LLMClient {
    String generate(String model, String prompt);
    String chat(String model, List<ChatMessage> history);
}
