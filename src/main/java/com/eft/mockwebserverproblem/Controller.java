package com.eft.mockwebserverproblem;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Function;

@Component
public class Controller {
    final WebClient webClient;
    long maxAttempts = 3;
    long delay = 5;

    public Controller(WebClient webClient) {
        this.webClient = webClient;
    }

    public String sendGet(String path) {
        String method = HttpMethod.GET.name();
        Function<UriBuilder, URI> uriBuilderFunction = uriBuilder -> uriBuilder.path(path).build();
        var url = uriBuilderFunction.apply(new DefaultUriBuilderFactory().builder()).toString();

        Mono<ResponseEntity<String>> monoResp =
                webClient.get()
                        .uri(url)
                        .retrieve()
                        .onStatus(HttpStatusCode::is5xxServerError, exceptionMapper(url, method))
                        .toEntity(String.class)
                        .retryWhen(
                                Retry.fixedDelay(maxAttempts, Duration.ofSeconds(delay))
                                        .doBeforeRetry( rs -> System.err.println("#### Retrying")).filter(p -> {return true;})
                        );

        Optional<ResponseEntity<String>> response = Optional.ofNullable(monoResp.block());
        return response.map(ResponseEntity::getBody).orElse(null);
    }

    public String sendPost(String path) {
        String method = HttpMethod.POST.name();
        Function<UriBuilder, URI> uriBuilderFunction = uriBuilder -> uriBuilder.path(path).build();
        var url = uriBuilderFunction.apply(new DefaultUriBuilderFactory().builder()).toString();

        Mono<ResponseEntity<String>> monoResp =
                webClient.post()
                        .uri(url)
                        .retrieve()
                        .onStatus(HttpStatusCode::is5xxServerError, exceptionMapper(url, method))
                        .toEntity(String.class)
                        .retryWhen(
                                Retry.fixedDelay(maxAttempts, Duration.ofSeconds(delay))
                                        .doBeforeRetry( rs -> System.err.println("#### Retrying")).filter(p -> {return true;})
                        );

        Optional<ResponseEntity<String>> response = Optional.ofNullable(monoResp.block());
        return response.map(ResponseEntity::getBody).orElse(null);
    }

    private Function<ClientResponse, Mono<? extends Throwable>> exceptionMapper(String uri, String method) {
        return cl -> cl.toEntity(String.class)
                .map(error -> new RuntimeException(
                        String.format("#### Failed to send request %s to uri %s", method, uri)
                ));
    }
}
