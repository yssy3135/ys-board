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

    // 컨슈머는 쓰기 트래픽에 대해서 작업
    // API는 조회 트래픽에대해서 작업 -> 분리고리

    // 애플리케이션이 20개 실행되고있고, 구독한 토픽의 파티션이 5개 라고 가정해볼떄,
    // 5개의 컨슈머는 파티션을 처리하고있지만, 15개의 커ㅓㄴ슈머는 파티션을 처리안하고 놀고있다.
    // kafka 설정도 고려해보자
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
