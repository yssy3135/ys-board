package ys.board.articleread.api;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import ys.board.articleread.sevice.event.response.ArticleReadResponse;

public class ArticleReadApiTest {
    RestClient restClient = RestClient.create("http://localhost:9005");



    @Test
    void readTest() {
        ArticleReadResponse response = restClient.get()
                .uri("/v1/articles/{articleId}", 144436707892436994L)
                .retrieve()
                .body(ArticleReadResponse.class);

        System.out.println("response = " + response);
    }
}
