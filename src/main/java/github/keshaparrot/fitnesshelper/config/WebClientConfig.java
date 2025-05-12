package github.keshaparrot.fitnesshelper.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Configuration
public class WebClientConfig {

    @Bean
    @Qualifier("ollamaWebClient")
    public WebClient ollamaWebClient(@Value("${app.ollama.url}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE)
                .filter(logRequest())   // якщо потрібно логувати запит
                .filter(logResponse())  // логування тіла відповіді
                .build();
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(response ->
                // замість просто Mono.just(response) мутуємо клієнтську відповідь, підміняючи body
                Mono.just(
                        response.mutate()
                                .body(dataBufferFlux ->
                                        dataBufferFlux.map(dataBuffer -> {
                                            // тут конвертуємо DataBuffer в UTF-8 рядок і логгуємо
                                            String bodyString = dataBuffer.toString(StandardCharsets.UTF_8);
                                            System.out.println("RESPONSE BODY: " + bodyString);
                                            return dataBuffer;
                                        }))
                                .build()
                ));
    }
    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            System.out.println("REQUEST: " + request.method() + " " + request.url());
            return Mono.just(request);
        });
    }

}