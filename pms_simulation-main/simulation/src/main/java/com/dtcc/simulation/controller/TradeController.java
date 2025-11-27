// package com.dtcc.simulation.controller;


// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.dtcc.simulation.model.TradeEvent;
// import com.dtcc.simulation.service.TradeGeneratorService;

// @RestController
// public class TradeController {

//     private final TradeGeneratorService generator;

//     public TradeController(TradeGeneratorService generator) {
//         this.generator = generator;
//     }

//     @GetMapping("/trade")
//     public TradeEvent getTrade() {
//         return generator.generateTrade();
//     }
// }
