package ys.board.hotarticle.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ys.board.common.event.Event;
import ys.board.common.event.EventPayload;
import ys.board.common.event.EventType;
import ys.board.hotarticle.service.HotArticleService;

@Slf4j
@Component
@RequiredArgsConstructor
public class HotArticleEventConsumer {

    private final HotArticleService hotArticleService;

    @KafkaListener(topics = {
            EventType.Topic.YS_BOARD_ARTICLE,
            EventType.Topic.YS_BOARD_COMMENT,
            EventType.Topic.YS_BOARD_LIKE,
            EventType.Topic.YS_BOARD_VIEW
    })
    public void listen(String message, Acknowledgment acknowledgment) {
        log.info("[HotArticleEventConsumer.listen] received message={}", message);
        Event<EventPayload> event = Event.fromJson(message);
        if(event != null) {
            hotArticleService.handleEvent(event);
        }
        acknowledgment.acknowledge();
    }

}
