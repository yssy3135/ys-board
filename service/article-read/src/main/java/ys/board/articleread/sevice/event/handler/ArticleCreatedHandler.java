package ys.board.articleread.sevice.event.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ys.board.articleread.client.ArticleClient;
import ys.board.articleread.repository.ArticleIdListRepository;
import ys.board.articleread.repository.ArticleQueryModel;
import ys.board.articleread.repository.ArticleQueryModelRepository;
import ys.board.articleread.repository.BoardArticleCountRepository;
import ys.board.common.event.Event;
import ys.board.common.event.EventType;
import ys.board.common.event.payload.ArticleCreatedEventPayload;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class ArticleCreatedHandler implements EventHandler<ArticleCreatedEventPayload> {

    private final ArticleQueryModelRepository articleQueryModelRepository;
    private final ArticleIdListRepository articleIdListRepository;
    private final BoardArticleCountRepository boardArticleCountRepository;

    @Override
    public void handle(Event<ArticleCreatedEventPayload> event) {
        ArticleCreatedEventPayload payload = event.getPayload();

        /**
         * articleIdList가 먼저 추가된다면
         * 조회 목록에 노출되지만 articleQueryModel은 아직 생성되지 않는 상황이 생길수 있다.
         * 때문에 articleQueryModel을 먼저 생성해준다.
         */
        articleQueryModelRepository.create(
                ArticleQueryModel.create(payload),
                Duration.ofDays(1)
        );
        articleIdListRepository.add(payload.getBoardId(), payload.getArticleId(), 1000L);
        boardArticleCountRepository.createOrUpdate(payload.getBoardId(), payload.getBoardArticleCount());
    }


    @Override
    public boolean supports(Event event) {
        return EventType.ARTICLE_CREATED == event.getType();
    }
}
