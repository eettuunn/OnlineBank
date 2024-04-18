package ru.hits.coreservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

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
        long requestTime = stopWatch.getLastTaskTimeMillis();
        double errorPercentage = (double) totalErrors.get() / totalRequests.get() * 100;
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String logMessage = String.format("Request %s.%s took %d ms. Error Percentage: %.2f%%",
                className, methodName, requestTime, errorPercentage);
        if (success) {
            log.info(logMessage);
        } else {
            log.error(logMessage);
        }
    }
}
