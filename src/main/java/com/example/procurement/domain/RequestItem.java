package com.example.procurement.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "request_items")
public class RequestItem {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    @Column(nullable = false, length = 100)
    private String skuId;

    @Column(nullable = false)
    private int qty;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

}
