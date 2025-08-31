package com.undoSchool.CourseSearch.service;

import com.undoSchool.CourseSearch.dto.CourseSearchRequest;
import com.undoSchool.CourseSearch.model.CourseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.nio.charset.CoderResult;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
@Slf4j
@Service
public class CourseSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    public CourseSearchService(ElasticsearchOperations elasticsearchOperations){
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public List<CourseModel> searchByTitle (String value){
        // Split the input into individual words (e.g., "Painting Basics" → ["painting", "basics"])
        String[] tokens = value.toLowerCase().split("\\s+");

        // Start with the first token
        Criteria criteria = new Criteria("title").matches(tokens[0]);

        // Add "AND" criteria for remaining tokens
        for (int i = 1; i < tokens.length; i++) {
            criteria = criteria.and(new Criteria("title").matches(tokens[i]));
        }

        CriteriaQuery query = new CriteriaQuery(criteria);
        SearchHits<CourseModel> hits = elasticsearchOperations.search(query, CourseModel.class);
        log.info("Matched courses: {}",hits);
        return hits.stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
    }

    public List<CourseModel> searchCourses(CourseSearchRequest request) {

        Criteria criteria = new Criteria();

        // ✅ Full-text search on title + description
        if (request.getQuery() != null && !request.getQuery().isBlank()) {
            Criteria titleCriteria = new Criteria("title").matches(request.getQuery());
            Criteria descCriteria = new Criteria("description").matches(request.getQuery());
            criteria = criteria.or(titleCriteria).or(descCriteria);
        }

        // ✅ Exact match filters
        if (request.getCategory() != null) {
            criteria = criteria.and(new Criteria("category").is(request.getCategory()));
        }
        if (request.getType() != null) {
            criteria = criteria.and(new Criteria("type").is(request.getType()));
        }

        // ✅ Range filters
        if (request.getMinAge() != null) {
            criteria = criteria.and(new Criteria("minAge").greaterThanEqual(request.getMinAge()));
        }
        if (request.getMaxAge() != null) {
            criteria = criteria.and(new Criteria("maxAge").lessThanEqual(request.getMaxAge()));
        }
        if (request.getMinPrice() != null) {
            criteria = criteria.and(new Criteria("price").greaterThanEqual(request.getMinPrice()));
        }
        if (request.getMaxPrice() != null) {
            criteria = criteria.and(new Criteria("price").lessThanEqual(request.getMaxPrice()));
        }

        // ✅ Date filter
        if (request.getNextSessionFrom() != null) {
            Instant inputInstant = request.getNextSessionFrom();
            ZoneId zoneId = ZoneId.systemDefault(); // or specify a timezone like ZoneId.of("Asia/Kathmandu")

            ZonedDateTime startOfDay = inputInstant.atZone(zoneId).toLocalDate().atStartOfDay(zoneId);
            Instant startOfDayInstant = startOfDay.toInstant();
            criteria = criteria.and(new Criteria("nextSessionDate").greaterThanEqual(inputInstant.atZone(zoneId).toLocalDate().atStartOfDay()));
        }

        CriteriaQuery query = new CriteriaQuery(criteria);
        SearchHits<CourseModel> hits = elasticsearchOperations.search(query, CourseModel.class);

        return hits.get().map(hit -> hit.getContent()).collect(Collectors.toList());
    }
}
