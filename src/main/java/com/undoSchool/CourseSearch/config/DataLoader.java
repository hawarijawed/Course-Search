package com.undoSchool.CourseSearch.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.undoSchool.CourseSearch.model.CourseModel;
import com.undoSchool.CourseSearch.repository.CourseRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
@Component
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final CourseRepository courseRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public DataLoader(CourseRepository courseRepository, ObjectMapper objectMapper) {
        this.courseRepository = courseRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) {
        log.warn(">>> DataLoader started...");

        try {
            long count = courseRepository.count();
            log.warn(">>> Existing course count in Elasticsearch: {}", count);

            if (count == 0) {
                InputStream inputStream = getClass().getResourceAsStream("/sample-courses.json");

                if (inputStream == null) {
                    log.error(">>> sample-courses.json file not found in resources!");
                    return;
                }

                List<CourseModel> courses = objectMapper.readValue(
                        inputStream,
                        new TypeReference<List<CourseModel>>() {}
                );

                courseRepository.saveAll(courses);
                log.warn(">>> {} sample courses loaded into Elasticsearch.", courses.size());
            } else {
                log.warn(">>> Data already exists, skipping load.");
            }
        } catch (Exception e) {
            log.error(">>> Error loading sample data: ", e);
        }
    }
}