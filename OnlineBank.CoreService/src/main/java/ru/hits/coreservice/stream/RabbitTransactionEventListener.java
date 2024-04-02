package ru.hits.coreservice.stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import ru.hits.coreservice.dto.CreateTransactionMessage;
import ru.hits.coreservice.service.TransactionService;

import java.util.function.Consumer;

@Component
@EnableRabbit
@RequiredArgsConstructor
@Slf4j
public class RabbitTransactionEventListener {

    private final TransactionService transactionService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "transactions1")
    public void createTransactionEvent(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            transactionService.createTransaction(objectMapper.readValue(message, CreateTransactionMessage.class));
        } catch (Exception e) {
            log.error("Error processing message: " + e.getMessage());
            rabbitTemplate.send("transactionsExchange", "dlq-name", new Message(message.getBytes()));
        }
    }

}
