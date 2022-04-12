package ee.juhanr.exercise.banking.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    public DirectExchange exchange(@Value("${service.rabbitmq.exchange.name}") String exchangeName) {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Queue accountQueue(@Value("${service.rabbitmq.queue.name.account}") String queueName) {
        return new Queue(queueName);
    }

    @Bean
    public Binding accountQueueBinding(DirectExchange exchange, Queue accountQueue,
            @Value("${service.rabbitmq.routing.key.account}") String routingKey) {
        return BindingBuilder
                .bind(accountQueue)
                .to(exchange)
                .with(routingKey);
    }

    @Bean
    public Queue balanceQueue(@Value("${service.rabbitmq.queue.name.balance}") String queueName) {
        return new Queue(queueName);
    }

    @Bean
    public Binding balanceQueueBinding(DirectExchange exchange, Queue balanceQueue,
            @Value("${service.rabbitmq.routing.key.balance}") String routingKey) {
        return BindingBuilder
                .bind(balanceQueue)
                .to(exchange)
                .with(routingKey);
    }

    @Bean
    public Queue transactionQueue(@Value("${service.rabbitmq.queue.name.transaction}") String queueName) {
        return new Queue(queueName);
    }

    @Bean
    public Binding transactionQueueBinding(DirectExchange exchange, Queue transactionQueue,
            @Value("${service.rabbitmq.routing.key.transaction}") String routingKey) {
        return BindingBuilder
                .bind(transactionQueue)
                .to(exchange)
                .with(routingKey);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
