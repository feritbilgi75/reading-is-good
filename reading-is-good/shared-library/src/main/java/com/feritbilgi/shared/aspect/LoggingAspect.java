package com.feritbilgi.shared.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feritbilgi.shared.annotation.LogOperation;
import com.feritbilgi.shared.dto.LogEvent;
import com.feritbilgi.shared.dto.SmsEvent;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Autowired
    private WebClient.Builder webClientBuilder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Around("@annotation(logOperation)")
    public Object logOperation(ProceedingJoinPoint joinPoint, LogOperation logOperation) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        String serviceName = joinPoint.getTarget().getClass().getSimpleName();
        
        LogEvent.LogEventBuilder logEventBuilder = LogEvent.builder()
                .operation(logOperation.operation())
                .description(logOperation.description())
                .serviceName(serviceName)
                .methodName(methodName)
                .timestamp(LocalDateTime.now());

        // Request bilgilerini al
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                logEventBuilder.ipAddress(request.getRemoteAddr())
                        .userAgent(request.getHeader("User-Agent"));
            }
        } catch (Exception e) {
            log.warn("Request bilgileri alınamadı: {}", e.getMessage());
        }

        // Request data'yı logla
        try {
            String requestData = objectMapper.writeValueAsString(joinPoint.getArgs());
            logEventBuilder.requestData(requestData);
        } catch (Exception e) {
            logEventBuilder.requestData("Request data serialize edilemedi");
        }

        Object result = null;
        try {
            // Method'u çalıştır
            result = joinPoint.proceed();
            
            // Response data'yı logla
            try {
                String responseData = objectMapper.writeValueAsString(result);
                logEventBuilder.responseData(responseData);
            } catch (Exception e) {
                logEventBuilder.responseData("Response data serialize edilemedi");
            }

            // Başarılı log
            long executionTime = System.currentTimeMillis() - startTime;
            LogEvent logEvent = logEventBuilder
                    .status("SUCCESS")
                    .executionTime(executionTime)
                    .build();

            // Log service'e gönder
            sendToLogService(logEvent);

            // SMS gönderimi gerekiyorsa
            if (logOperation.sendSms()) {
                sendSmsNotification(logEvent, logOperation);
            }

            log.info("✅ {} - {} başarıyla tamamlandı ({}ms)", 
                    logOperation.operation(), logOperation.description(), executionTime);

        } catch (Exception e) {
            // Hata logu
            long executionTime = System.currentTimeMillis() - startTime;
            LogEvent logEvent = logEventBuilder
                    .status("ERROR")
                    .errorMessage(e.getMessage())
                    .executionTime(executionTime)
                    .build();

            // Log service'e gönder
            sendToLogService(logEvent);

            log.error("❌ {} - {} hatası: {}", 
                    logOperation.operation(), logOperation.description(), e.getMessage());
            
            throw e;
        }

        return result;
    }

    private void sendToLogService(LogEvent logEvent) {
        try {
            webClientBuilder.build()
                    .post()
                    .uri("http://log-service/api/logs")
                    .bodyValue(logEvent)
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(
                            response -> log.debug("Log service'e gönderildi: {}", response),
                            error -> log.error("Log service hatası: {}", error.getMessage())
                    );
        } catch (Exception e) {
            log.error("Log service'e gönderim hatası: {}", e.getMessage());
        }
    }

    private void sendSmsNotification(LogEvent logEvent, LogOperation logOperation) {
        try {
            SmsEvent smsEvent = SmsEvent.builder()
                    .phoneNumber("+905551234567") // Demo için sabit numara
                    .message("İşlem tamamlandı: " + logOperation.description())
                    .template(logOperation.smsTemplate())
                    .serviceName(logEvent.getServiceName())
                    .operation(logEvent.getOperation())
                    .timestamp(LocalDateTime.now())
                    .status("SENT")
                    .build();

            webClientBuilder.build()
                    .post()
                    .uri("http://log-service/api/sms")
                    .bodyValue(smsEvent)
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(
                            response -> log.info("📱 SMS gönderildi: {}", smsEvent.getMessage()),
                            error -> log.error("SMS gönderim hatası: {}", error.getMessage())
                    );
        } catch (Exception e) {
            log.error("SMS notification hatası: {}", e.getMessage());
        }
    }
}
