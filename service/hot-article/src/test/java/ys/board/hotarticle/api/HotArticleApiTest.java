package ys.board.hotarticle.api;


import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import ys.board.hotarticle.service.response.HotArticleResponse;

import java.util.List;

public class HotArticleApiTest {
    RestClient restClient = RestClient.create("http://localhost:9004");


    @Test
    public void readAllTest() {
        List<HotArticleResponse> responses = restClient.get()
                .uri("/v1/hot-articles/articles/date/{dateStr}", "20250216")
                .retrieve()
                .body(new ParameterizedTypeReference<List<HotArticleResponse>>() {
                });

        for (HotArticleResponse hotArticleResponse : responses) {
            System.out.println("response = " + hotArticleResponse);
        }
    }
}
