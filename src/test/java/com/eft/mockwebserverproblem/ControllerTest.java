package com.eft.mockwebserverproblem;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ControllerTest {
    static MockWebServer mockWebServer;
    static Util util= new Util();

    static final Dispatcher dispatcher = new Dispatcher() {
        @NotNull
        @Override
        public MockResponse dispatch(@NotNull RecordedRequest recordedRequest) throws InterruptedException {
            String path = recordedRequest.getPath();
            System.err.println("#### " + recordedRequest.getMethod() + " " + recordedRequest.getRequestUrl());
            //System.err.println("#### " + path);

            if(path != null) {
                if(path.contains("/v1/test1")) {
                    return new MockResponse().setResponseCode(200).setBody("#### response to /v1/test1");
                } else if (path.contains("/v1/test2")) {
                    return new MockResponse().setResponseCode(202).setBody("version=9");
                }else if(path.contains("/v1/test3")) {
                    return new MockResponse().setResponseCode(200).setBody(util.getRequestMessageFromPath("get_big.txt"));
                } else if(path.contains("/v1/test4")) {
                    //causes java.lang.IllegalArgumentException: invalid version format: VERSION=9HTTP/1.1
                    //In Spring Boot 3.3.3 remove the ".setBody("version=9")" below to fix the exception
                    //On the other hand it works correctly in Spring Boot 3.3.2
                    return new MockResponse().setResponseCode(204).setBody("version=9");
                } else if(path.contains("/v1/post")) {
                    return new MockResponse().setResponseCode(200).setBody(util.getRequestMessageFromPath("post_big.txt"));
                }else {
                    return new MockResponse().setResponseCode(204);
                }
            } else {
                return new MockResponse().setResponseCode(204);
            }
        }
    };

    @BeforeAll
    public static void beforeAll() throws IOException {
        System.err.println("#### Starting MockWebServer");
        mockWebServer = new MockWebServer();
        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start(59000);
    }

    @AfterAll
    public static void afterAll() throws IOException {
        System.err.println("#### Stopping MockWebServer");
        mockWebServer.shutdown();
    }

    @Autowired
    Controller controller;

    @Test
    void test3Gets2Posts() {
        System.err.println("#### Sending GET");
        controller.sendGet("/v1/test4");

        System.err.println("#### Sending POST");
        String sPost = controller.sendPost("/v1/post");
        assertNotNull(sPost);
        assertTrue(sPost.startsWith("Lorem ipsum odor amet, consectetuer adipiscing elit."));
    }

}
