package com.undoSchool.CourseSearch.service;

import com.undoSchool.CourseSearch.dto.CourseSearchRequest;
import com.undoSchool.CourseSearch.model.CourseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
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

    public Page<CourseModel> searchCourses(CourseSearchRequest request) {

        Criteria criteria = new Criteria();
        if(request.getQuery() == null && request.getMinAge()==null
        && request.getMaxAge()== null && request.getType()==null
        && request.getCategory()==null && request.getNextSessionFrom()==null){
            return Page.empty();
        }
        log.info(String.valueOf(request));
        // Full-text search on title + description
        if (request.getQuery() != null && !request.getQuery().isBlank()) {
            Criteria titleCriteria = new Criteria("title").matches(request.getQuery());
            Criteria descCriteria = new Criteria("description").matches(request.getQuery());
            criteria = criteria.or(titleCriteria).or(descCriteria);
            log.info("Title or description matche");
        }

        // Exact match filters
        if (request.getCategory() != null) {
            criteria = criteria.and(new Criteria("category").is(request.getCategory()));
            log.info("Category match");
        }
        if (request.getType() != null) {
            criteria = criteria.and(new Criteria("type").is(request.getType()));
            log.info("Type match");
        }

        // Range filters
        if (request.getMinAge() != null) {
            criteria = criteria.and(new Criteria("minAge").greaterThanEqual(request.getMinAge()));
            log.info("minAge matche");
        }
        if (request.getMaxAge() != null) {
            criteria = criteria.and(new Criteria("maxAge").lessThanEqual(request.getMaxAge()));
            log.info("maxAge matche");
        }
        if (request.getMinPrice() != null) {
            criteria = criteria.and(new Criteria("price").greaterThanEqual(request.getMinPrice()));
            log.info("minPrice matche");
        }
        if (request.getMaxPrice() != null) {
            criteria = criteria.and(new Criteria("price").lessThanEqual(request.getMaxPrice()));
            log.info("maxnPrice matche");
        }

        // Date filter
        if (request.getNextSessionFrom() != null) {
            Instant inputInstant = request.getNextSessionFrom();
            ZoneId zoneId = ZoneId.systemDefault(); // e.g. ZoneId.of("Asia/Kathmandu")

            // normalize to midnight of that date
            Instant startOfDayInstant = inputInstant.atZone(zoneId)
                    .toLocalDate()
                    .atStartOfDay(zoneId)
                    .toInstant();

            criteria = criteria.and(
                    new Criteria("nextSessionDate").greaterThanEqual(startOfDayInstant)
            );
            log.info("Date filter matche");
        }

        //Pagination
        int page = request.getPage() != null?request.getPage():0;
        int size = request.getSize() != null?request.getSize():10;
        Pageable pageable = PageRequest.of(page,size);
        //Sorting
        Sort sort = Sort.unsorted();
        if("upcoming".equalsIgnoreCase(request.getSort())){
            sort = Sort.by(Sort.Order.asc("nextSessionDate"));
        }
        else if("priceAsc".equalsIgnoreCase(request.getSort())){
            sort = Sort.by(Sort.Order.asc("price"));
        }
        else if("priceDesc".equalsIgnoreCase(request.getSort())){
            sort = Sort.by(Sort.Order.desc("price"));
        }
        CriteriaQuery query = new CriteriaQuery(criteria, pageable).addSort(sort);
        SearchHits<CourseModel> hits = elasticsearchOperations.search(query, CourseModel.class);
        List<CourseModel> courses = hits.stream().map(SearchHit::getContent).toList();
        log.info("Total hist: {}",hits);
        return new PageImpl<>(courses, pageable, hits.getTotalHits());
    }
}
