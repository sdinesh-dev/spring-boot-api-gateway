package com.dinesh.microservices.api_gateway.filter;

import com.dinesh.microservices.api_gateway.model.Company;
import com.dinesh.microservices.api_gateway.model.Student;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class PostGlobalFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        DataBufferFactory dataBufferFactory = response.bufferFactory();
        ServerHttpResponseDecorator decoratedReponse = getDecoratedResponse(path, response, request, dataBufferFactory);
        return chain.filter(exchange.mutate().response(decoratedReponse).build());
    }

    private ServerHttpResponseDecorator getDecoratedResponse(String path, ServerHttpResponse response, ServerHttpRequest request, DataBufferFactory dataBufferFactory) {
        return new ServerHttpResponseDecorator(response) {
            @Override
            public Mono<Void> writeWith(final Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                        DefaultDataBuffer joinedBuffer = new DefaultDataBufferFactory().join(dataBuffers);
                        byte[] content = new byte[joinedBuffer.readableByteCount()];
                        joinedBuffer.read(content);
                        String responseBody = new String(content, StandardCharsets.UTF_8);
                        System.out.println("requestId:" + request.getId() + ", method:" + request.getMethodValue() + ", req URL:" + request.getURI() + ", response body:" + responseBody);
                        try {
                            if (request.getURI().getPath().equals("/first") && request.getMethodValue().equals("GET")) {
                                List<Student> student = new ObjectMapper().readValue(responseBody, List.class);
                                System.out.println("Student:" + student);
                            } else if (request.getURI().getPath().equals("/second") && request.getMethodValue().equals("GET")) {
                                List<Company> companies = new ObjectMapper().readValue(responseBody, List.class);
                                System.out.println("Companies:" + companies);
                            }
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                        return dataBufferFactory.wrap(responseBody.getBytes());
                    })).onErrorResume(err -> {
                        System.out.println("Error While Decorating Response:" + err.getMessage());
                        return Mono.empty();
                    });
                }
                return super.writeWith(body);
            }
        };
    }
}
