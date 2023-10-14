package com.plularsight.courseinfo.cli.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class CourseRetrievalService {
    private static final String url = "https://app.pluralsight.com/profile/data/author/sander-mak/all-content";
    private static final HttpClient _client = HttpClient.newHttpClient();
    private static  final ObjectMapper _mapper = new ObjectMapper();
    public List<Course> getCoursesFor(String authorId) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url.formatted(authorId)))
                .GET().build();
        try {
            HttpResponse<String> response = _client.send(request, HttpResponse.BodyHandlers.ofString());
            return switch(response.statusCode()){
                case 200 -> gtoCourses(response);
                case 404 -> List.of();
                default -> throw new RuntimeException("PS not working " + response.statusCode());
            };
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException("Could not call PS Api", ex);
        }
    }

    private static List<Course> gtoCourses(HttpResponse<String> response) throws JsonProcessingException {
        var returnType = _mapper.getTypeFactory()
                        .constructCollectionType(List.class, Course.class);
        return _mapper.readValue(response.body(), returnType);
    }
}
