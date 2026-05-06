package com.example.demo.controller;

import com.example.demo.dto.HorsePayRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private static final String HORSEPAY_URL =
            "http://homepages.cs.ncl.ac.uk/daniel.nesbitt/CSC8019/HorsePay/HorsePay.php";

    private final RestClient restClient = RestClient.create();

    @PostMapping("/horsepay")
    public ResponseEntity<Object> processPayment(@RequestBody HorsePayRequest request) {
        Object response = restClient.post()
                .uri(HORSEPAY_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Object.class);
        return ResponseEntity.ok(response);
    }
}
