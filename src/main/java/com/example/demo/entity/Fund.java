package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "funds")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String fundCode;

    @Column(nullable = false, length = 255)
    private String fundName;

    @Column(nullable = false, length = 100)
    private String fundType;

    @Column(precision = 15, scale = 6)
    private BigDecimal oneMonth;

    @Column(precision = 15, scale = 6)
    private BigDecimal threeMonths;

    @Column(precision = 15, scale = 6)
    private BigDecimal sixMonths;

    @Column(precision = 15, scale = 6)
    private BigDecimal newYear;

    @Column(precision = 15, scale = 6)
    private BigDecimal oneYear;

    @Column(precision = 15, scale = 6)
    private BigDecimal threeYears;

    @Column(precision = 15, scale = 6)
    private BigDecimal fiveYears;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}