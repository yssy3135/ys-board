package ys.board.articleread.client;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentClient {

    private RestClient restclient;

    @Value("${endpoints.ys-board-comment-service.url}")
    private String commentServiceUrl;


    @PostConstruct
    public void initRestClient() {
        restclient = RestClient.create(commentServiceUrl);
    }

    public Long count(Long articleId) {
        try {
            return restclient.get()
                    .uri("/v2/comments/articles/{articleId}", articleId)
                    .retrieve()
                    .body(Long.class);
        } catch (Exception e) {
            log.error("[CommentClient.read] articleId={}", articleId, e);

            return 0L;
        }
    }

}


