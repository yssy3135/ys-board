package ys.board.common.outboxmessagerelay;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageRelay {

    private final OutboxRepository outboxRepository;
    private final MessageRelayCoordinator messageRelayCoordinator;
    private final KafkaTemplate<String, String> messageKafkaTemplate;


    // 작업이 커밋되기 전에 outbox를 생성하고 단일 트랜잭션에 포함시킴.
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void createOutbox(OutboxEvent outboxEvent) {
        log.info("[messageRelay.createOutbox] outboxEvent={}", outboxEvent);
        outboxRepository.save(outboxEvent.getOutbox());
    }

    // 트랜잭션이 커밋된 이후 event를 발행함.
    // MessageRelayConfig 에 정의된 messageRelayPublishEventExecutor 메서드를 통해 Executor를 얻어 별도의 스레드에서 실행됨.
    @Async("messageRelayPublishEventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishEvent(OutboxEvent outboxEvent) {
        publishEvent(outboxEvent.getOutbox());
    }


    public void publishEvent(Outbox outbox) {

        try {
            // 메시지 publish 1초동안 대기
            messageKafkaTemplate.send(
                    outbox.getEventType().getTopic(),
                    String.valueOf(outbox.getShardKey()),
                    outbox.getPayload()
            ).get(1, TimeUnit.SECONDS);
            outboxRepository.delete(outbox);
        } catch (Exception e) {
            log.error("[MessageRelay.publishEvent] outbox={}", outbox, e);
        }
    }


    // 10초동안 발행되지 못한 Event들을 주기적으로 polling 하여발행
    @Scheduled(
            fixedDelay = 10,
            initialDelay = 5,
            timeUnit = TimeUnit.SECONDS,
            scheduler = "messageRelayPublishPendingEventExecutor"
    )
    public void publishPendingEvent() {
        AssignedShard assignedShard = messageRelayCoordinator.assignShards();
        log.info("[MessageRelay.publishPendingEvent] assignedShard size={}", assignedShard.getShards().size());
        for(Long shard : assignedShard.getShards())  {
            List<Outbox> outboxes = outboxRepository.findAllByShardKeyAndCreatedAtLessThanEqualOrderByCreatedAtAsc(
                    shard,
                    LocalDateTime.now().minusSeconds(10),
                    PageRequest.ofSize(100)
            );

            for(Outbox outbox : outboxes) {
                publishEvent(outbox);
            }


        }
    }


}
