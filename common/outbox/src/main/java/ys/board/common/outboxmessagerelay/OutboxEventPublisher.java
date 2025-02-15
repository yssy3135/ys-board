package ys.board.common.outboxmessagerelay;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ys.board.common.event.Event;
import ys.board.common.event.EventPayload;
import ys.board.common.event.EventType;
import ys.board.common.snowflake.Snowflake;

@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {
    private final Snowflake outboxIdSnowflake = new Snowflake();
    private final Snowflake eventIdSnowflake = new Snowflake();
    private final ApplicationEventPublisher applicationEventPublisher;

    public void publish(
            EventType eventType,
            EventPayload eventPayload,
            Long shardKey
    ) {
        //articleId=10, shardKey == articleId
        // 10 % 4 = 2 물리적 샤드 2
        Outbox outbox = Outbox.create(
                outboxIdSnowflake.nextId(),
                eventType,
                Event.of(
                        eventIdSnowflake.nextId(),
                        eventType,
                        eventPayload
                ).toJson(),
                shardKey % MessageRelayConstants.SHARD_COUNT
        );

        applicationEventPublisher.publishEvent(OutboxEvent.create(outbox));
    }

}
