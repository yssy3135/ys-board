package ys.board.articleread.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ys.board.articleread.sevice.ArticleReadService;
import ys.board.common.event.Event;
import ys.board.common.event.EventPayload;
import ys.board.common.event.EventType;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleReadEventConsumer {

    private final ArticleReadService articleReadService;

    @KafkaListener(topics = {
            EventType.Topic.YS_BOARD_ARTICLE,
            EventType.Topic.YS_BOARD_COMMENT,
            EventType.Topic.YS_BOARD_LIKE
    })
    public void listen(String message, Acknowledgment ack) {
        log.info("[ArticleReadEventConsumer.listen] message={}", message);
        Event<EventPayload> event = Event.fromJson(message);
        if (event != null) {
            articleReadService.handleEvent(event);
        }
        ack.acknowledge();
    }
}
