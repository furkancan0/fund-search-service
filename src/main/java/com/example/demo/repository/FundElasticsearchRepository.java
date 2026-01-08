package com.example.demo.repository;

import com.example.demo.document.FundDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FundElasticsearchRepository extends ElasticsearchRepository<FundDocument, String> {
    List<FundDocument> findByFundCode(String fundCode);
    Page<FundDocument> findByFundName(String fundName, Pageable pageable);
}