package ru.hits.coreservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory("localhost");
        cachingConnectionFactory.setPort(5672);
        cachingConnectionFactory.setUsername("user");
        cachingConnectionFactory.setPassword("1234");
        cachingConnectionFactory.setVirtualHost("/");
        return cachingConnectionFactory;
    }

    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }

    @Bean
    public Queue myQueue() {
        return new Queue("transactions1", true);
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue("dlq-name", true);
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange("transactionsExchange", true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("dlx-name");
    }

    @Bean
    Binding binding() {
        return BindingBuilder.bind(myQueue()).to(exchange()).with("transactions");
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(exchange()).with("dlq-name");
    }
    @Bean
    public Binding queueToDeadLetterExchangeBinding() {
        return BindingBuilder.bind(myQueue()).to(deadLetterExchange()).with("transactions1");
    }

}
