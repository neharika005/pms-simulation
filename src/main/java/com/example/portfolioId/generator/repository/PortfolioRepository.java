package com.example.portfolioId.generator.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.portfolioId.generator.entity.Portfolio;


public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    @Query(value = "SELECT id FROM portfolio ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String findLastUuid();
}
