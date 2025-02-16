package ys.board.articleread.sevice.event.response;

import ys.board.articleread.repository.ArticleQueryModel;

import java.time.LocalDateTime;

public class ArticleReadResponse {
    private Long articleId;
    private String title;
    private String content;
    private Long boardId;
    private Long writerId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Long articleCommentCount;
    private Long articleLikeCount;
    private Long articleViewCount;

    public static ArticleReadResponse from(ArticleQueryModel articleQueryModel, Long viewCount) {
        ArticleReadResponse articleReadResponse = new ArticleReadResponse();
        articleReadResponse.articleId = articleQueryModel.getArticleId();
        articleReadResponse.title = articleQueryModel.getTitle();
        articleReadResponse.content = articleQueryModel.getContent();
        articleReadResponse.boardId = articleQueryModel.getBoardId();
        articleReadResponse.writerId = articleQueryModel.getWriterId();
        articleReadResponse.createdAt = articleQueryModel.getCreatedAt();
        articleReadResponse.modifiedAt = articleQueryModel.getModifiedAt();
        articleReadResponse.articleCommentCount = articleQueryModel.getArticleCommentCount();
        articleReadResponse.articleLikeCount = articleQueryModel.getArticleLikeCount();
        articleReadResponse.articleViewCount = viewCount;

        return articleReadResponse;
    }
}
