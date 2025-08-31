package com.undoSchool.CourseSearch.dto;
import lombok.Data;

import java.time.Instant;


@Data
public class CourseSearchRequest {
    private String query;
    private String category;
    private String type;
    private String minAge;
    private String maxAge;
    private String minPrice;
    private String maxPrice;
    private Instant nextSessionFrom;
}
