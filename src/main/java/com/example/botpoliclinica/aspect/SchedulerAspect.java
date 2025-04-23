package com.example.botpoliclinica.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class SchedulerAspect {
    @Before("@annotation(scheduled)")
    public void beforeScheduledMethod(JoinPoint joinPoint, Scheduled scheduled) {
        String methodName = joinPoint.getSignature().getName();
        log.info("Запуск задачи: {}", methodName);
    }

    @AfterReturning("@annotation(scheduled)")
    public void afterScheduledMethod(JoinPoint joinPoint, Scheduled scheduled) {
        String methodName = joinPoint.getSignature().getName();
        log.info("Задача выполнена: {}", methodName);
    }

    @AfterThrowing(pointcut = "@annotation(scheduled)", throwing = "ex")
    public void afterThrowingScheduledMethod(JoinPoint joinPoint, Scheduled scheduled, Throwable ex) {
        String methodName = joinPoint.getSignature().getName();
        log.error("Ошибка в задаче: {} — {}", methodName, ex.getMessage());
    }
}
