package com.undoSchool.CourseSearch.controller;

import com.undoSchool.CourseSearch.dto.CourseSearchRequest;
import com.undoSchool.CourseSearch.model.CourseModel;
import com.undoSchool.CourseSearch.repository.CourseRepository;
import com.undoSchool.CourseSearch.service.CourseSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
@Slf4j
public class CourseController {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseSearchService courseSearchService;
    @GetMapping("/")
    public Iterable<CourseModel> findAll() {
        return courseRepository.findAll();
    }

    @GetMapping("/search/title/{value}")
    public List<CourseModel> searchByTitle(@PathVariable String value){
        log.info("Path variable: {}", value);
        return courseSearchService.searchByTitle(value);
    }

    @GetMapping("/search/description/{value}")
    public List<CourseModel> searchByDescription(@PathVariable String value){
        return courseRepository.findByDescriptionContaining(value);
    }
    @GetMapping("/search/both/{value}")
    public List<CourseModel> searchByTitleOrDescription(@PathVariable String value){
        return courseRepository.findByTitleContainingOrDescriptionContaining(value);
    }

    @PostMapping("/search")
    public Page<CourseModel> searchCourses(@RequestBody CourseSearchRequest request) {
        log.info("Search request: {}", request);
        return courseSearchService.searchCourses(request);
    }
}
