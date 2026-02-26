package com.myapp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "production_run")
public class ProductionRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "executed_at", nullable = false)
    public Instant executedAt;

    @Column(name = "grand_total", nullable = false, precision = 19, scale = 2)
    public BigDecimal grandTotal;

    @OneToMany(mappedBy = "run", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<ProductionRunItem> items = new ArrayList<>();
}