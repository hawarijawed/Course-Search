package com.undoSchool.CourseSearch.service;

import com.undoSchool.CourseSearch.model.CourseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.nio.charset.CoderResult;
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
}
