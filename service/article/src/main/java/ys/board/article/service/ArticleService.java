package ys.board.article.service;

import ys.board.common.event.EventType;
import ys.board.common.event.payload.ArticleCreatedEventPayload;
import ys.board.common.event.payload.ArticleDeletedEventPayload;
import ys.board.common.event.payload.ArticleUpdatedEventPayload;
import ys.board.common.outboxmessagerelay.OutboxEventPublisher;
import ys.board.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ys.board.article.entity.Article;
import ys.board.article.entity.BoardArticleCount;
import ys.board.article.repository.ArticleRepository;
import ys.board.article.repository.BoardArticleCountRepository;
import ys.board.article.service.request.ArticleCreateRequest;
import ys.board.article.service.request.ArticleUpdateRequest;
import ys.board.article.service.response.ArticlePageResponse;
import ys.board.article.service.response.ArticleResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final Snowflake snowflake = new Snowflake();
    private final ArticleRepository articleRepository;
    private final BoardArticleCountRepository boardArticleCountRepository;
    private final OutboxEventPublisher outboxEventPublisher;


    @Transactional
    public ArticleResponse create(ArticleCreateRequest request) {
        Article article = articleRepository.save(
                Article.create(snowflake.nextId(), request.getTitle(), request.getContent(), request.getBoardId(), request.getWriterId())
        );

        int result = boardArticleCountRepository.increase(request.getBoardId());
        if (result == 0) {
            boardArticleCountRepository.save(
                    BoardArticleCount.init(request.getBoardId(), 1L)
            );
        }

        outboxEventPublisher.publish(
                EventType.ARTICLE_CREATED,
                ArticleCreatedEventPayload.builder().
                        articleId(article.getArticleId()).
                        title(article.getTitle()).
                        content(article.getContent()).
                        boardId(article.getBoardId()).
                        writerId(article.getWriterId()).
                        createdAt(article.getCreatedAt()).
                        modifiedAt(article.getModifiedAt()).
                        boardArticleCount(count(article.getBoardId()))
                .build(),
                article.getBoardId()
        );


        return ArticleResponse.from(article);
    }

    @Transactional
    public ArticleResponse update(Long articleId, ArticleUpdateRequest request) {
        Article article = articleRepository.findById(articleId).orElseThrow();
        article.update(request.getTitle(), request.getContent());

        outboxEventPublisher.publish(
                EventType.ARTICLE_UPDATED,
                ArticleUpdatedEventPayload.builder().
                        articleId(article.getArticleId()).
                        title(article.getTitle()).
                        content(article.getContent()).
                        boardId(article.getBoardId()).
                        writerId(article.getWriterId()).
                        createdAt(article.getCreatedAt()).
                        modifiedAt(article.getModifiedAt())
                        .build(),
                article.getBoardId()
        );

        return ArticleResponse.from(article);
    }

    @Transactional
    public ArticleResponse read(long articleId) {
        return ArticleResponse.from(articleRepository.findById(articleId).orElseThrow());
    }

    @Transactional
    public void delete(long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow();
        articleRepository.delete(article);

        outboxEventPublisher.publish(
                EventType.ARTICLE_DELETED,
                ArticleDeletedEventPayload.builder().
                        articleId(article.getArticleId()).
                        title(article.getTitle()).
                        content(article.getContent()).
                        boardId(article.getBoardId()).
                        writerId(article.getWriterId()).
                        createdAt(article.getCreatedAt()).
                        modifiedAt(article.getModifiedAt())
                        .build(),
                article.getBoardId()
        );


    }

    public ArticlePageResponse readAll(Long boardId, Long page, Long pageSize) {
        return ArticlePageResponse.of(
                articleRepository.findAll(boardId, (page - 1) * pageSize, pageSize).stream()
                        .map(ArticleResponse::from)
                        .toList(),
                articleRepository.count(
                        boardId,
                        PageLimitCalculator.calculatePageLimit(page, pageSize, 10L)
                )
        );
    }

    public List<ArticleResponse> readAllInfiniteScroll(Long boardId, Long pageSize, Long lastArticleId) {
        List<Article> articles = lastArticleId == null ?
                articleRepository.findAllInfiniteScroll(boardId, pageSize) :
                articleRepository.findAllInfiniteScroll(boardId, pageSize, lastArticleId);
        return articles.stream().map(ArticleResponse::from).toList();
    }

    public Long count(Long boardId) {
        return boardArticleCountRepository.findById(boardId)
                .map(BoardArticleCount::getArticleCount)
                .orElse(0L);
    }


}
