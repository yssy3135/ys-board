package ys.board.articleread.sevice.event.handler;

import ys.board.articleread.repository.ArticleIdListRepository;
import ys.board.articleread.repository.ArticleQueryModelRepository;
import ys.board.articleread.repository.BoardArticleCountRepository;
import ys.board.common.event.Event;
import ys.board.common.event.EventType;
import ys.board.common.event.payload.ArticleDeletedEventPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleDeletedEventHandler implements EventHandler<ArticleDeletedEventPayload> {
    private final ArticleIdListRepository articleIdListRepository;
    private final ArticleQueryModelRepository articleQueryModelRepository;
    private final BoardArticleCountRepository boardArticleCountRepository;

    @Override
    public void handle(Event<ArticleDeletedEventPayload> event) {
        ArticleDeletedEventPayload payload = event.getPayload();
        /**
         * articleQueryModel을 먼저 삭제처리 할 경우
         * 목록에는 남아있지만 articleQueryModel은 존재하지 않는 상황일 수 있다.
         * 때문에 조회 목목인 articleIdList에서 먼저 삭제처리해준다.
         */
        articleIdListRepository.delete(payload.getBoardId(), payload.getArticleId());
        articleQueryModelRepository.delete(payload.getArticleId());
        boardArticleCountRepository.createOrUpdate(payload.getBoardId(), payload.getBoardArticleCount());
    }
    @Override
    public boolean supports(Event<ArticleDeletedEventPayload> event) {
        return EventType.ARTICLE_DELETED == event.getType();
    }
}
