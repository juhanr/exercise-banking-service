package ee.juhanr.exercise.banking.core.account.publisher;

import ee.juhanr.exercise.banking.core.account.entity.AccountEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountPublisher {
    private final RabbitTemplate template;
    private final Binding accountQueueBinding;

    public void publish(AccountEntity message) {
        template.convertAndSend(accountQueueBinding.getExchange(), accountQueueBinding.getRoutingKey(), message);
    }
}
