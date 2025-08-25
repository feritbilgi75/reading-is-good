package com.feritbilgi.log_service.service;

import com.feritbilgi.log_service.model.LogEntry;
import com.feritbilgi.log_service.model.SmsEntry;
import com.feritbilgi.log_service.repository.LogRepository;
import com.feritbilgi.log_service.repository.SmsRepository;
import com.feritbilgi.shared.dto.LogEvent;
import com.feritbilgi.shared.dto.SmsEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class LogService {

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private SmsRepository smsRepository;

    public LogEntry saveLog(LogEvent logEvent) {
        try {
            LogEntry logEntry = LogEntry.builder()
                    .operation(logEvent.getOperation())
                    .description(logEvent.getDescription())
                    .serviceName(logEvent.getServiceName())
                    .methodName(logEvent.getMethodName())
                    .requestData(logEvent.getRequestData())
                    .responseData(logEvent.getResponseData())
                    .errorMessage(logEvent.getErrorMessage())
                    .status(logEvent.getStatus())
                    .userId(logEvent.getUserId())
                    .timestamp(logEvent.getTimestamp())
                    .executionTime(logEvent.getExecutionTime())
                    .ipAddress(logEvent.getIpAddress())
                    .userAgent(logEvent.getUserAgent())
                    .build();

            LogEntry saved = logRepository.save(logEntry);
            log.info("📝 Log kaydedildi: {} - {} ({})", 
                    logEvent.getOperation(), logEvent.getDescription(), logEvent.getServiceName());
            
            return saved;
        } catch (Exception e) {
            log.error("Log kaydetme hatası: {}", e.getMessage());
            throw e;
        }
    }

    public SmsEntry saveSms(SmsEvent smsEvent) {
        try {
            SmsEntry smsEntry = SmsEntry.builder()
                    .phoneNumber(smsEvent.getPhoneNumber())
                    .message(smsEvent.getMessage())
                    .template(smsEvent.getTemplate())
                    .serviceName(smsEvent.getServiceName())
                    .operation(smsEvent.getOperation())
                    .timestamp(smsEvent.getTimestamp())
                    .status(smsEvent.getStatus())
                    .build();

            SmsEntry saved = smsRepository.save(smsEntry);
            log.info("📱 SMS kaydedildi: {} - {}", smsEvent.getPhoneNumber(), smsEvent.getMessage());
            
            return saved;
        } catch (Exception e) {
            log.error("SMS kaydetme hatası: {}", e.getMessage());
            throw e;
        }
    }

    public List<LogEntry> getAllLogs() {
        return logRepository.findAll();
    }

    public List<LogEntry> getLogsByService(String serviceName) {
        return logRepository.findByServiceNameOrderByTimestampDesc(serviceName);
    }

    public List<LogEntry> getLogsByStatus(String status) {
        return logRepository.findByStatusOrderByTimestampDesc(status);
    }

    public List<LogEntry> getLogsByOperation(String operation) {
        return logRepository.findByOperationOrderByTimestampDesc(operation);
    }

    public List<LogEntry> getLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return logRepository.findByDateRange(startDate, endDate);
    }

    public List<LogEntry> getLogsByServiceAndDateRange(String serviceName, LocalDateTime startDate, LocalDateTime endDate) {
        return logRepository.findByServiceNameAndDateRange(serviceName, startDate, endDate);
    }

    public List<SmsEntry> getAllSms() {
        return smsRepository.findAll();
    }

    public List<SmsEntry> getSmsByService(String serviceName) {
        return smsRepository.findByServiceNameOrderByTimestampDesc(serviceName);
    }

    public List<SmsEntry> getSmsByStatus(String status) {
        return smsRepository.findByStatusOrderByTimestampDesc(status);
    }
}
