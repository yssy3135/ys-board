package ys.board.common.event;

import ys.board.common.event.payload.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum EventType {
    ARTICLE_CREATED(ArticleCreatedEventPayload.class, Topic.YS_BOARD_ARTICLE),
    ARTICLE_UPDATED(ArticleUpdatedEventPayload.class, Topic.YS_BOARD_ARTICLE),
    ARTICLE_DELETED(ArticleDeletedEventPayload.class, Topic.YS_BOARD_ARTICLE),
    COMMENT_CREATED(CommentCreatedEventPayload.class, Topic.YS_BOARD_COMMENT),
    COMMENT_DELETED(CommentDeletedEventPayload.class, Topic.YS_BOARD_COMMENT),
    ARTICLE_LIKED(ArticleLikedEventPayload.class, Topic.YS_BOARD_LIKE),
    ARTICLE_UNLIKED(ArticleUnlikedEventPayload.class, Topic.YS_BOARD_LIKE),
    ARTICLE_VIEWED(ArticleViewedEventPayload.class, Topic.YS_BOARD_VIEW);

    private final Class<? extends EventPayload> payloadClass;
    private final String topic;

    public static EventType from(String type) {
        try {
            return valueOf(type);
        } catch (IllegalArgumentException e) {
            log.error("[EventType.from] type={}", type, e);
            return null;
        }
    }


    public static class Topic {
        public static final String YS_BOARD_ARTICLE = "ys-board-article";
        public static final String YS_BOARD_COMMENT = "ys-board-comment";
        public static final String YS_BOARD_LIKE = "ys-board-like";
        public static final String YS_BOARD_VIEW = "ys-board-view";
    }


}
