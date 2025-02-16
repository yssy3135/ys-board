package ys.board.articleread.sevice;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import ys.board.articleread.client.ArticleClient;
import ys.board.articleread.client.CommentClient;
import ys.board.articleread.client.LikeClient;
import ys.board.articleread.client.ViewClient;
import ys.board.articleread.repository.ArticleQueryModel;
import ys.board.articleread.repository.ArticleQueryModelRepository;
import ys.board.articleread.sevice.event.handler.EventHandler;
import ys.board.articleread.sevice.event.response.ArticleReadResponse;
import ys.board.common.event.Event;

import java.time.Duration;
import java.util.List;
import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
public class ArticleReadService {
    private final ArticleClient articleClient;
    private final CommentClient commentClient;
    private final LikeClient likeClient;
    private final ViewClient viewClient;
    private final ArticleQueryModelRepository articleQueryModelRepository;
    List<EventHandler> eventHandlers;


    public void handleEvent(Event event) {
        for (EventHandler eventHandler : eventHandlers) {
            if(eventHandler.supports(event)) {
                eventHandler.handle(event);
            }
        }
    }


    public ArticleReadResponse read(Long articleId) {
        ArticleQueryModel articleQueryModel = articleQueryModelRepository.read(articleId)
                .or(() -> fetch(articleId))
                .orElseThrow();

        return ArticleReadResponse.from(
                articleQueryModel,
                viewClient.count(articleId)
        );
    }

    public Optional<ArticleQueryModel> fetch(Long articleId) {
        Optional<ArticleQueryModel> articleQueryModelOptional = articleClient.read(articleId)
                .map(article -> ArticleQueryModel.create(
                        article,
                        commentClient.count(articleId),
                        likeClient.count(articleId)
                ));

        articleQueryModelOptional
                .ifPresent(articleQueryModel -> articleQueryModelRepository.create(articleQueryModel, Duration.ofDays(1L)));
        log.info("[ArticleReadService.fetch] fetch data. articleId={}, isPresent={}", articleId, articleQueryModelOptional.isPresent());

        return articleQueryModelOptional;
    }

}
