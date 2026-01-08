package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundSearchRequest {
    private String name;
    private String fundType;

    private ReturnRange oneMonth;
    private ReturnRange threeMonths;
    private ReturnRange newYear;
    private ReturnRange oneYear;
    private ReturnRange threeYear;
    private ReturnRange fiveYear;
}
