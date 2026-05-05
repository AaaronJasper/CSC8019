package com.example.demo.service;

import com.example.demo.dto.PaymentRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    private static final String HORSEPAY_URL =
            "http://homepages.cs.ncl.ac.uk/daniel.nesbitt/CSC8019/HorsePay/HorsePay.php";

    private static final String STORE_ID = "Team08";

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> makePayment(PaymentRequest request) {

        validateRequest(request);

        Map<String, Object> payPayload = new HashMap<>();

        payPayload.put("storeID", STORE_ID);

        payPayload.put("customerID", request.getCustomerID());
        payPayload.put("date", request.getDate());
        payPayload.put("time", request.getTime());
        payPayload.put("timeZone", request.getTimeZone());
        payPayload.put("transactionAmount", request.getTransactionAmount());
        payPayload.put("currencyCode", request.getCurrencyCode());

        if (request.getForcePaymentStatusReturnType() != null) {
            payPayload.put(
                    "forcePaymentStatusReturnType",
                    request.getForcePaymentStatusReturnType()
            );
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(payPayload, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                HORSEPAY_URL,
                entity,
                Map.class
        );

        return response.getBody();
    }

    private void validateRequest(PaymentRequest request) {
        if (request.getDate() == null ||
                !request.getDate().matches("\\d{2}[-/]\\d{2}[-/]\\d{4}")) {
            throw new IllegalArgumentException(
                    "date must be in DD-MM-YYYY or DD/MM/YYYY format."
            );
        }

        if (request.getTime() == null ||
                !request.getTime().matches("([01]\\d|2[0-3]):[0-5]\\d")) {
            throw new IllegalArgumentException(
                    "time must be in HH:MM 24-hour format."
            );
        }

        if (request.getTimeZone() == null ||
                !request.getTimeZone().matches("[A-Z]{3}")) {
            throw new IllegalArgumentException(
                    "timeZone must be a 3-character uppercase string, e.g. GMT or BST."
            );
        }

        if (request.getTransactionAmount() <= 0) {
            throw new IllegalArgumentException(
                    "transactionAmount must be greater than 0."
            );
        }

        if (request.getCurrencyCode() == null ||
                !request.getCurrencyCode().matches("[A-Z]{3}")) {
            throw new IllegalArgumentException(
                    "currencyCode must be a 3-character uppercase string, e.g. GBP."
            );
        }
    }
}