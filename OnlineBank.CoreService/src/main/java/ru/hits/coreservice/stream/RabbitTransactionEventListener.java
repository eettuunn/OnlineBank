package ru.hits.coreservice.stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import ru.hits.coreservice.dto.CreateTransactionMessage;
import ru.hits.coreservice.service.TransactionService;

import java.util.function.Consumer;

@Component
@EnableRabbit
@RequiredArgsConstructor
public class RabbitTransactionEventListener {

    private final TransactionService transactionService;

    @RabbitListener(queues = "transactions1")
    public void createTransactionEvent(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            transactionService.createTransaction(objectMapper.readValue(message, CreateTransactionMessage.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
