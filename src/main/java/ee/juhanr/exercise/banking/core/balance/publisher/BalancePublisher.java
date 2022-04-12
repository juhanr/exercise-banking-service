package ee.juhanr.exercise.banking.core.balance.publisher;

import ee.juhanr.exercise.banking.core.balance.entity.BalanceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalancePublisher {
    private final RabbitTemplate template;
    private final Binding balanceQueueBinding;

    public void publish(BalanceEntity message) {
        template.convertAndSend(balanceQueueBinding.getExchange(), balanceQueueBinding.getRoutingKey(), message);
    }
}
