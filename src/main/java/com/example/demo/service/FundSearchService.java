package com.example.demo.service;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.json.JsonData;
import com.example.demo.document.FundDocument;
import com.example.demo.dto.FundSearchRequest;
import com.example.demo.dto.ReturnRange;
import com.example.demo.dto.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FundSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    public SearchResponse<FundDocument> searchWithFilters(FundSearchRequest request, Pageable pageable) {
        List<Query> mustQueries = new ArrayList<>();

        if (request.getName() != null) {
            mustQueries.add(MatchQuery.of(m -> m
                    .field("fundName")
                    .query(request.getName())
            )._toQuery());
        }

        if (request.getFundType() != null) {
            mustQueries.add(TermQuery.of(t -> t
                    .field("fundType")
                    .value(request.getFundType())
            )._toQuery());
        }

        addRangeQuery(mustQueries, "oneYear", request.getOneYear());

        Query query = BoolQuery.of(b -> b
                .must(mustQueries)
        )._toQuery();

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(query)
                .withPageable(pageable)
                .build();

        SearchHits<FundDocument> searchHits = elasticsearchOperations.search(
                searchQuery,
                FundDocument.class
        );

        return buildSearchResponse(searchHits, pageable);
    }

    private void addRangeQuery(
            List<Query> queries,
            String field,
            ReturnRange range) {

        if (range == null) {
            return;
        }

        RangeQuery.Builder rangeBuilder = new RangeQuery.Builder().field(field);

        if (range.getMin() != null) {
            rangeBuilder.gte(JsonData.of(range.getMin().doubleValue()));
        }
        if (range.getMax() != null) {
            rangeBuilder.lte(JsonData.of(range.getMax().doubleValue()));
        }

        queries.add(rangeBuilder.build()._toQuery());
    }

    private SearchResponse<FundDocument> buildSearchResponse(
            SearchHits<FundDocument> searchHits,
            Pageable pageable) {

        List<FundDocument> content = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        long totalElements = searchHits.getTotalHits();
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());
        int currentPage = pageable.getPageNumber();

        return SearchResponse.<FundDocument>builder()
                .content(content)
                .pageInfo(SearchResponse.PageInfo.builder()
                        .pageNumber(currentPage + 1)
                        .pageSize(pageable.getPageSize())
                        .totalElements(totalElements)
                        .totalPages(totalPages)
                        .first(currentPage == 1)
                        .last(currentPage >= totalPages - 1)
                        .hasNext(currentPage < totalPages - 1)
                        .hasPrevious(currentPage > 0)
                        .build())
                .build();
    }
}
