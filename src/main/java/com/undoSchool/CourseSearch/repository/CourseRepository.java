package com.undoSchool.CourseSearch.repository;

import com.undoSchool.CourseSearch.model.CourseModel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableElasticsearchRepositories
public interface CourseRepository extends ElasticsearchRepository<CourseModel, String> {
    List<CourseModel> findByTitle(String title);
    List<CourseModel> findByDescriptionContaining(String description);
    List<CourseModel> findByTitleContainingOrDescriptionContaining(String keyword);
    // Example: search by category
    List<CourseModel> findByCategory(String category);

    // Example: search by grade level (if your JSON has grade field)
    List<CourseModel> findByGradeRange(String gradeRange);
}
