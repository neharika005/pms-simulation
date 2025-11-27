// package com.dtcc.simulation.controller;

// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

// import com.dtcc.simulation.model.TradeEvent;
// import com.dtcc.simulation.service.TradeGeneratorService;

// @RestController
// public class TradeStreamController {

//     private final TradeGeneratorService generator;

//     public TradeStreamController(TradeGeneratorService generator) {
//         this.generator = generator;
//     }

//     @GetMapping(value = "/trade/stream", produces = "text/event-stream")
//     public SseEmitter streamTrades() {

//         SseEmitter emitter = new SseEmitter();

//         new Thread(() -> {
//             try {
//                 while (true) {
//                     TradeEvent event = generator.generateTrade();
//                     if (event != null) {    
//                         emitter.send(event);
//                     }
//                 }
//             } catch (Exception e) {
//                 emitter.completeWithError(e);
//             }
//         }).start();

//         return emitter;
//     }
// }


