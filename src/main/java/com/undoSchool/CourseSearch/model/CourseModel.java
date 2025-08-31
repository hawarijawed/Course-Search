package com.undoSchool.CourseSearch.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.lang.annotation.Documented;
import java.time.Instant;
import java.time.LocalDateTime;
@Data
@Document(indexName = "courses")
//@Setting(settingPath = "/sample-courses.json")
public class CourseModel {
    @Id
    private String id;  // keep String, update JSON to use string ids

    private String title;
    private String description;
    private String category;
    private String type;
    private String gradeRange;
    private Integer minAge;
    private Integer maxAge;
    private Double price;
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Instant nextSessionDate;  // handles 2025-09-10T15:00:00Z
}
