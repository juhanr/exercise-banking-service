package ee.juhanr.exercise.banking.core.transaction.publisher;

import ee.juhanr.exercise.banking.core.transaction.entity.TransactionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionPublisher {
    private final RabbitTemplate template;
    private final Binding transactionQueueBinding;

    public void publish(TransactionEntity message) {
        template.convertAndSend(transactionQueueBinding.getExchange(), transactionQueueBinding.getRoutingKey(),
                message);
    }
}
