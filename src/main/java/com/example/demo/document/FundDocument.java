package com.example.demo.document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.math.BigDecimal;

@Document(indexName = "funds")
@Setting(settingPath = "elasticsearch-settings.json")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundDocument {
    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String fundCode;

    @Field(type = FieldType.Text, analyzer = "edge_ngram_analyzer", searchAnalyzer = "search_analyzer")
    private String fundName;

    @Field(type = FieldType.Keyword)
    private String fundType;

    @Field(type = FieldType.Double)
    private BigDecimal oneMonth;

    @Field(type = FieldType.Double)
    private BigDecimal threeMonths;

    @Field(type = FieldType.Double)
    private BigDecimal sixMonths;

    @Field(type = FieldType.Double)
    private BigDecimal newYear;

    @Field(type = FieldType.Double)
    private BigDecimal oneYear;

    @Field(type = FieldType.Double)
    private BigDecimal threeYears;

    @Field(type = FieldType.Double)
    private BigDecimal fiveYears;
}