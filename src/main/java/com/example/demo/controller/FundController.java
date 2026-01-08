package com.example.demo.controller;

import com.example.demo.document.FundDocument;
import com.example.demo.dto.FundSearchRequest;
import com.example.demo.dto.SearchResponse;
import com.example.demo.dto.UploadResponse;
import com.example.demo.service.FundSearchService;
import com.example.demo.service.FundService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/funds")
@RequiredArgsConstructor
public class FundController {

    private final FundService fundService;
    private final FundSearchService fundSearchService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponse> importFunds(
            @RequestParam("file") MultipartFile file) {

        UploadResponse response = fundService.importAndIndexFunds(file);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/searchByCode")
    public ResponseEntity<List<FundDocument>> searchByCode(
            @RequestParam String code) {
        return ResponseEntity.ok(fundService.searchByFundCode(code));
    }

    @GetMapping("/searchByName")
    public ResponseEntity<SearchResponse<FundDocument>> searchByName(
            @RequestBody(required = false) FundSearchRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "oneMonth") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        int pageNo = Math.max(page - 1, 0);

        Pageable pageable = PageRequest.of(pageNo, size, sort);

        return ResponseEntity.ok(fundSearchService.searchWithFilters(request, pageable));
    }

    @PostMapping("/reindex")
    public ResponseEntity<Map<String, String>> reindexFunds() {
        fundService.reindexAllFunds();

        return ResponseEntity.ok(Map.of("message", "All funds reindex successfully"));
    }
}
