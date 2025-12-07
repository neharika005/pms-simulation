package com.example.portfolioId.generator.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.portfolioId.generator.entity.InvestorDetails;

public interface InvestorDetailsRepository extends JpaRepository<InvestorDetails, UUID> {

}
