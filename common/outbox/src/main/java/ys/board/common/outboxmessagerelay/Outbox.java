package ys.board.common.outboxmessagerelay;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ys.board.common.event.EventType;

import java.time.LocalDateTime;

@Table(name = "outbox")
@Getter
@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Outbox {

    @Id
    public Long outboxId;
    @Enumerated(EnumType.STRING)
    private EventType eventType;
    private String payload;
    private Long sharedKey;
    private LocalDateTime createAt;

    public static Outbox create(
            Long outboxId,
            EventType eventType,
            String payload,
            Long sharedKey
    ) {
        Outbox outbox = new Outbox();
        outbox.outboxId = outboxId;
        outbox.eventType = eventType;
        outbox.payload = payload;
        outbox.sharedKey = sharedKey;
        outbox.createAt = LocalDateTime.now();

        return outbox;
    }

}

