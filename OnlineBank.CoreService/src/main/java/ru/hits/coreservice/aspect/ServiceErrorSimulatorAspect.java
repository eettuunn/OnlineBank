package ru.hits.coreservice.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import ru.hits.coreservice.exception.InternalServerException;

import java.time.LocalDateTime;
import java.util.Random;

@Aspect
@Component
public class ServiceErrorSimulatorAspect {

    private final Random random = new Random();

    @Before("execution(* ru.hits.coreservice.controller.*.*(..))")
    public void simulateServiceError() {
        LocalDateTime currentTime = LocalDateTime.now();
        int probability = currentTime.getMinute() % 2 == 0 ? 90 : 50; // 90% в четные минуты, 50% в нечетные

        if (random.nextInt(100) < probability) {
            throw new InternalServerException("Имитация внутренней ошибки сервера");
        }
    }

}
