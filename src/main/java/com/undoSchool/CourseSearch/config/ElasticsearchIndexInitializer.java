package com.undoSchool.CourseSearch.config;

import com.undoSchool.CourseSearch.model.CourseModel;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Component;

@Component
public class ElasticsearchIndexInitializer {
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;
    @PostConstruct
    public void createIndexAndMapping() {
        IndexOperations indexOps = elasticsearchOperations.indexOps(CourseModel.class);

        if (!indexOps.exists()) {
            indexOps.create(); // create the index
            indexOps.putMapping(indexOps.createMapping()); // apply mapping based on @Document
        }
    }
}
