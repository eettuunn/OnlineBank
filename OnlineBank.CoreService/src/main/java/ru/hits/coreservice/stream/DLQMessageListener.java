package ru.hits.coreservice.stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DLQMessageListener {

    @RabbitListener(queues = "dlq-name")
    public void handleDLQMessage(String message) {
        log.info("Received message from DLQ: " + message);
    }

}
