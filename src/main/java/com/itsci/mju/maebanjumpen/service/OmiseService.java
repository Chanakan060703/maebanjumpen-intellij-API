package com.itsci.mju.maebanjumpen.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.itsci.mju.maebanjumpen.config.OmiseAPIConfig;

@Service
public class OmiseService {


    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OmiseAPIConfig omiseAPIConfig;

    private static final Pattern PERCENT_ATTRIBUTE_PATTERN = Pattern.compile(
            "([a-zA-Z-]+)=\"(\\d+(\\.\\d+)?)%\""
    );

    public Map<String, String> createPromptPayQRCode(Double amountInBaht, String transactionIdInOurSystem) throws Exception {
        int amountInSatang = (int) (amountInBaht * 100);

        if (amountInSatang < 2000) { // Omise PromptPay minimum is 20 THB (2000 satang)
            throw new IllegalArgumentException("Minimum amount for PromptPay is 20 Baht.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(omiseAPIConfig.getSecretKey(), "");
        headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new java.util.HashMap<>();
        requestBody.put("amount", amountInSatang);
        requestBody.put("currency", "THB");
        requestBody.put("description", "Deposit for transaction ID: " + transactionIdInOurSystem);

        Map<String, String> metadata = new java.util.HashMap<>();
        metadata.put("transaction_id", transactionIdInOurSystem);
        requestBody.put("metadata", metadata);

        Map<String, String> source = new java.util.HashMap<>();
        source.put("type", "promptpay");
        requestBody.put("source", source);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        String chargesUrl = omiseAPIConfig.getBaseUrl() + "/charges";
        System.out.println("Omise QR Code Request URL: " + chargesUrl);
        System.out.println("Omise QR Code Request Headers (excluding auth): " + headers);
        System.out.println("Omise QR Code Request Body: " + objectMapper.writeValueAsString(requestBody));

        ResponseEntity<String> response = restTemplate.exchange(chargesUrl, HttpMethod.POST, entity, String.class);

        System.out.println("Omise QR Code Response Status: " + response.getStatusCode());
        System.out.println("Omise QR Code Response Body: " + response.getBody());


        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode scannableCode = root.path("source").path("scannable_code");
            if (scannableCode.has("image") && scannableCode.path("image").has("download_uri")) {
                String qrCodeSvgDownloadUrl = scannableCode.path("image").path("download_uri").asText();

                byte[] svgBytes = downloadSvgFromUrl(qrCodeSvgDownloadUrl);

                String svgXmlContent = new String(svgBytes, StandardCharsets.UTF_8);

                Matcher matcher = PERCENT_ATTRIBUTE_PATTERN.matcher(svgXmlContent);
                StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    matcher.appendReplacement(sb, matcher.group(1) + "=\"" + matcher.group(2) + "\"");
                }
                matcher.appendTail(sb);
                svgXmlContent = sb.toString();

                System.out.println("--- Full SVG XML Content received from Omise (for transactionId: " + transactionIdInOurSystem + ") ---");
                System.out.println(svgXmlContent);
                System.out.println("--- End SVG XML Content ---");

                byte[] cleanedSvgBytes = svgXmlContent.getBytes(StandardCharsets.UTF_8);

                String qrCodeImageBase64 = Base64.getEncoder().encodeToString(cleanedSvgBytes);

                System.out.println("SVG downloaded and encoded to Base64 successfully. Encoded Length: " + qrCodeImageBase64.length() + " chars.");

                Map<String, String> responseMap = new java.util.HashMap<>();
                responseMap.put("qrCodeImageBase64", qrCodeImageBase64);
                return responseMap;

            } else {
                throw new RuntimeException("QR Code image download URI not found in Omise response.");
            }
        } else {
            throw new RuntimeException("Failed to create Omise charge. Status: " + response.getStatusCode() + ", Body: " + response.getBody());
        }
    }

    private byte[] downloadSvgFromUrl(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(omiseAPIConfig.getSecretKey(), "");
        headers.setAccept(java.util.Collections.singletonList(MediaType.parseMediaType("image/svg+xml")));

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to download SVG from URL: " + url + ". Status: " + response.getStatusCode());
        }
    }
}