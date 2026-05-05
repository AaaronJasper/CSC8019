package com.example.demo.controller;

import com.example.demo.dto.PaymentRequest;
import com.example.demo.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/pay")
    public ResponseEntity<?> payWithHorsePay(@RequestBody PaymentRequest request) {
        try {
            // Send payment request to external HorsePay API.
            Map<String, Object> response = paymentService.makePayment(request);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // Return clear validation error to frontend.
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "paymentSuccessful", false,
                            "reason", e.getMessage()
                    )
            );

        } catch (Exception e) {
            // Handles HorsePay server/network errors.
            return ResponseEntity.internalServerError().body(
                    Map.of(
                            "paymentSuccessful", false,
                            "reason", "Could not connect to HorsePay service."
                    )
            );
        }
    }
}