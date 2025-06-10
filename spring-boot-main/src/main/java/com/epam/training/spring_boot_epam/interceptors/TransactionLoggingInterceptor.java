package com.epam.training.spring_boot_epam.interceptors;

import com.epam.training.spring_boot_epam.domain.TransactionLog;
import com.epam.training.spring_boot_epam.service.TransactionLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class TransactionLoggingInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionLoggingInterceptor.class);
    private final TransactionLogService logService;

    public TransactionLoggingInterceptor(TransactionLogService logService) {
        this.logService = logService;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {

        TransactionLog log = new TransactionLog();
        log.setTransactionId(UUID.randomUUID().toString());
        log.setTimestamp(LocalDateTime.now());
        log.setUsername(getCurrentUsername());
        log.setEndpoint(request.getRequestURI());
        log.setHttpMethod(request.getMethod());
        log.setResponseStatus(response.getStatus());
        log.setSuccess(response.getStatus() < 400);
        logService.save(log);

        LOGGER.info("Transaction completed with id: {}", log.getTransactionId());
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated()) ? auth.getName() : "anonymous";
    }
}
