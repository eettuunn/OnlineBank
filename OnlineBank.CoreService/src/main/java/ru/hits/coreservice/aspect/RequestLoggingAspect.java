package ru.hits.coreservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Aspect
@Component
@Slf4j
public class RequestLoggingAspect {

    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong totalErrors = new AtomicLong(0);
    private final StopWatch stopWatch = new StopWatch();

    @Around("execution(* ru.hits.coreservice.controller.*.*(..))")
    public Object logRequestExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        stopWatch.start();
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            totalErrors.incrementAndGet();
            throw throwable;
        } finally {
            stopWatch.stop();
            totalRequests.incrementAndGet();
        }
        return result;
    }

    @AfterReturning("execution(* ru.hits.coreservice.controller.*.*(..))")
    public void logRequestSuccess(JoinPoint joinPoint) {
        logRequestInfo(joinPoint, true);
    }

    @AfterThrowing("execution(* ru.hits.coreservice.controller.*.*(..))")
    public void logRequestError(JoinPoint joinPoint) {
        logRequestInfo(joinPoint, false);
    }

    private void logRequestInfo(JoinPoint joinPoint, boolean success) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        long requestTime = stopWatch.getLastTaskTimeMillis();
        double errorPercentage = (double) totalErrors.get() / totalRequests.get() * 100;
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String url = request.getRequestURL().toString();
        String protocol = request.getProtocol();
        String apiName = "CoreService";
        String logMessage = String.format("Request %s.%s took %d ms. Error Percentage: %.2f%%",
                className, methodName, requestTime, errorPercentage);

        sendMonitoringRequest(
                url,
                request.getMethod(),
                protocol,
                success ? HttpStatus.OK.value() : HttpStatus.INTERNAL_SERVER_ERROR.value(),
                requestTime,
                apiName);

        if (success) {
            log.info(logMessage);
        } else {
            log.error(logMessage);
        }
    }

    private void sendMonitoringRequest(String url, String method, String protocol, int status, long spentTimeInMs, String apiName) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("url", url);
        requestBody.put("method", method);
        requestBody.put("protocol", protocol);
        requestBody.put("status", status);
        requestBody.put("spentTimeInMs", spentTimeInMs);
        requestBody.put("apiName", apiName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    "http://host.docker.internal:7777/monitoring_api",
                    HttpMethod.POST,
                    requestEntity,
                    String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Monitoring request sent successfully.");
            } else {
                log.error("Failed to send monitoring request. Status code: {}", response.getStatusCodeValue());
            }
        } catch (Exception e) {
            log.error("Error while sending monitoring request: {}", e.getMessage());
        }
    }
}
