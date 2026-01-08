package com.example.demo.service;

import com.example.demo.document.FundDocument;
import com.example.demo.dto.SearchResponse;
import com.example.demo.dto.UploadResponse;
import com.example.demo.entity.Fund;
import com.example.demo.exception.ReindexException;
import com.example.demo.repository.FundElasticsearchRepository;
import com.example.demo.repository.FundRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FundService {
    private static final Logger logger = LoggerFactory.getLogger(FundService.class);

    private final FundRepository fundRepository;
    private final FundElasticsearchRepository esRepository;

    private final ExcelService excelService;
    private final ElasticSyncService fundSyncService;

    @Transactional
    public UploadResponse importAndIndexFunds(MultipartFile file) {
        List<Fund> funds = excelService.parseExcelFile(file);
        int totalRecords = funds.size();

        List<Fund> savedFunds = fundRepository.saveAll(funds);
        logger.info("Saved {} funds to database", savedFunds.size());

        fundSyncService.syncFundsToElasticSearch(savedFunds);

        return UploadResponse.builder()
                .message("Funds imported and indexed successfully")
                .filename(file.getOriginalFilename())
                .fileSize(file.getSize())
                .totalRecords(totalRecords)
                .uploadedAt(LocalDateTime.now())
                .build();
    }

    public List<FundDocument> searchByFundCode(String fundCode) {
        return esRepository.findByFundCode(fundCode);
    }

    public SearchResponse<FundDocument> searchByFundName(String fundName, Pageable pageable) {
        Page<FundDocument> page = esRepository.findByFundName(fundName, pageable);
        return buildSearchResponse(page);
    }

    @Transactional
    public void reindexAllFunds() {
        try {
            List<Fund> allFunds = fundRepository.findAll();
            if (allFunds.isEmpty()) {
                logger.warn("No funds found in the database to reindex");
                return;
            }
            esRepository.deleteAll();

            List<FundDocument> documents = allFunds.stream()
                    .map(this::convertToDocument)
                    .collect(Collectors.toList());

            esRepository.saveAll(documents);
            logger.info("Successfully reindex {} funds to Elasticsearch", documents.size());

        } catch (Exception e) {
            logger.error("Reindex failed", e);
            throw new ReindexException("Failed to reindex funds: " + e.getMessage(), e);
        }
    }

    private SearchResponse<FundDocument> buildSearchResponse(Page<FundDocument> page) {
        return SearchResponse.<FundDocument>builder()
                .content(page.getContent())
                .pageInfo(SearchResponse.PageInfo.builder()
                        .pageNumber(page.getNumber() +1)
                        .pageSize(page.getSize())
                        .totalElements(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .first(page.isFirst())
                        .last(page.isLast())
                        .hasNext(page.hasNext())
                        .hasPrevious(page.hasPrevious())
                        .build())
                .build();
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
