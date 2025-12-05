package com.example.portfolioId.generator.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.portfolioId.generator.entity.PortfolioSymbol;

public interface PortfolioSymbolRepository extends JpaRepository<PortfolioSymbol, Long> {
    List<PortfolioSymbol> findByPortfolioId(Long portfolioId);
}
