package ys.board.articleread.sevice.event.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ys.board.articleread.client.ArticleClient;
import ys.board.articleread.repository.ArticleQueryModel;
import ys.board.articleread.repository.ArticleQueryModelRepository;
import ys.board.common.event.Event;
import ys.board.common.event.EventType;
import ys.board.common.event.payload.ArticleCreatedEventPayload;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class ArticleCreatedHandler implements EventHandler<ArticleCreatedEventPayload> {

    private final ArticleQueryModelRepository articleQueryModelRepository;
    private final ArticleClient articleClient;

    @Override
    public void handle(Event<ArticleCreatedEventPayload> event) {
        ArticleCreatedEventPayload payload =  event.getPayload();

        articleQueryModelRepository.create(
                ArticleQueryModel.create(payload),
                Duration.ofDays(1)
        );
    }

    @Override
    public boolean supports(Event event) {
        return EventType.ARTICLE_CREATED == event.getType();
    }
}
