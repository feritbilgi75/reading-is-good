package com.feritbilgi.log_service.controller;

import com.feritbilgi.log_service.model.LogEntry;
import com.feritbilgi.log_service.model.SmsEntry;
import com.feritbilgi.log_service.service.LogService;
import com.feritbilgi.shared.dto.LogEvent;
import com.feritbilgi.shared.dto.SmsEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class LogController {

    @Autowired
    private LogService logService;

    @PostMapping("/logs")
    public ResponseEntity<LogEntry> saveLog(@RequestBody LogEvent logEvent) {
        try {
            LogEntry saved = logService.saveLog(logEvent);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            log.error("Log kaydetme hatası: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/sms")
    public ResponseEntity<SmsEntry> saveSms(@RequestBody SmsEvent smsEvent) {
        try {
            SmsEntry saved = logService.saveSms(smsEvent);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            log.error("SMS kaydetme hatası: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/logs")
    public ResponseEntity<List<LogEntry>> getAllLogs() {
        return ResponseEntity.ok(logService.getAllLogs());
    }

    @GetMapping("/logs/service/{serviceName}")
    public ResponseEntity<List<LogEntry>> getLogsByService(@PathVariable String serviceName) {
        return ResponseEntity.ok(logService.getLogsByService(serviceName));
    }

    @GetMapping("/logs/status/{status}")
    public ResponseEntity<List<LogEntry>> getLogsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(logService.getLogsByStatus(status));
    }

    @GetMapping("/logs/operation/{operation}")
    public ResponseEntity<List<LogEntry>> getLogsByOperation(@PathVariable String operation) {
        return ResponseEntity.ok(logService.getLogsByOperation(operation));
    }

    @GetMapping("/logs/date-range")
    public ResponseEntity<List<LogEntry>> getLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(logService.getLogsByDateRange(startDate, endDate));
    }

    @GetMapping("/logs/service/{serviceName}/date-range")
    public ResponseEntity<List<LogEntry>> getLogsByServiceAndDateRange(
            @PathVariable String serviceName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(logService.getLogsByServiceAndDateRange(serviceName, startDate, endDate));
    }

    @GetMapping("/sms")
    public ResponseEntity<List<SmsEntry>> getAllSms() {
        return ResponseEntity.ok(logService.getAllSms());
    }

    @GetMapping("/sms/service/{serviceName}")
    public ResponseEntity<List<SmsEntry>> getSmsByService(@PathVariable String serviceName) {
        return ResponseEntity.ok(logService.getSmsByService(serviceName));
    }

    @GetMapping("/sms/status/{status}")
    public ResponseEntity<List<SmsEntry>> getSmsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(logService.getSmsByStatus(status));
    }
}
