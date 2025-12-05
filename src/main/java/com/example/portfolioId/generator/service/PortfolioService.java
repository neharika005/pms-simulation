package com.example.portfolioId.generator.service;

import org.springframework.stereotype.Service;

import com.example.portfolioId.generator.dto.PortfolioRequest;
import com.example.portfolioId.generator.dto.PortfolioResponse;
import com.example.portfolioId.generator.entity.Portfolio;
import com.example.portfolioId.generator.repository.PortfolioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepo;

    public PortfolioResponse createPortfolio(PortfolioRequest req) {

        String lastUuid = portfolioRepo.findLastUuid();
        if (lastUuid == null) {
            throw new IllegalStateException("No base UUIDs found in DB. Insert initial 5 UUIDs first.");
        }

        String newUuid = generateNextUuid(lastUuid);

        Portfolio p = new Portfolio();
        p.setId(newUuid);
        p.setName(req.getName());
        p.setPhone(req.getPhone());
        p.setAddress(req.getAddress());

        portfolioRepo.save(p);

        return new PortfolioResponse(newUuid, "Portfolio created successfully");
    }

    private String generateNextUuid(String lastUuid) {

        String prefix = lastUuid.substring(0, lastUuid.length() - 3);

        String lastThree = lastUuid.substring(lastUuid.length() - 3);

        int number = Integer.parseInt(lastThree);

        number++;

        String newLastThree = String.format("%03d", number);

        return prefix + newLastThree;
    }
}
