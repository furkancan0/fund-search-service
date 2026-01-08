package com.example.demo.service;

import com.example.demo.document.FundDocument;
import com.example.demo.entity.Fund;
import com.example.demo.repository.FundElasticsearchRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElasticSyncService {
    private static final Logger logger = LoggerFactory.getLogger(ElasticSyncService.class);

    private final FundElasticsearchRepository esRepository;

    @Async
    public void syncFundsToElasticSearch(List<Fund> funds){
        try {
            List<FundDocument> documents = funds.stream()
                    .map(this::convertToDocument)
                    .collect(Collectors.toList());

            esRepository.saveAll(documents);
            logger.info("Indexed {} funds to Elasticsearch", documents.size());
        } catch (Exception e) {
            logger.error("Failed to index funds to Elasticsearch", e);
        }
    }

    private FundDocument convertToDocument(Fund fund) {
        return FundDocument.builder()
                .id(String.valueOf(fund.getId()))
                .fundCode(fund.getFundCode())
                .fundName(fund.getFundName())
                .fundType(fund.getFundType())
                .oneMonth(fund.getOneMonth())
                .threeMonths(fund.getThreeMonths())
                .sixMonths(fund.getSixMonths())
                .newYear(fund.getNewYear())
                .oneYear(fund.getOneYear())
                .threeYears(fund.getThreeYears())
                .fiveYears(fund.getFiveYears())
                .build();
    }
}
