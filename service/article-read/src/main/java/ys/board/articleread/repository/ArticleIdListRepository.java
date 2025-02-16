package ys.board.articleread.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ArticleIdListRepository {

    private final StringRedisTemplate redisTemplate;

    private static final String KEY_FORMAT = "article-read::board::%s::article-list";



    public void add(Long boardId, Long articleId, Long limit) {
        redisTemplate.executePipelined((RedisCallback<String>) action -> {
            StringRedisConnection conn = (StringRedisConnection) action;
            String key = generateKey(boardId);

            /**
             * score가 0 인 이유는 articleId를 Key값으로 사용하는데
             * score를 articleId로 지정하서 사용할 경우 redis에서 score는 double값으로 받기때문에
             * Long 값인 articleId가 들어갈 경우 데이터가 꼬일 수 있다.
             * 따라서 0으로 지정할 경우 value값으로 정렬하기 때문에 0으로 지정하여 articleId로 정렬할 수 있도록 구성.
             */
            conn.zAdd(key, 0, toPaddedString(articleId));
            conn.zRemRange(key, 0 , - limit -1);
            return null;
        });
    }

    public void delete(Long boardId, Long articleId) {
        redisTemplate.opsForZSet().remove(generateKey(boardId), toPaddedString(articleId));

    }

    public List<Long> readAll(Long boardId, Long offset, Long limit) {
        return redisTemplate.opsForZSet()
                .reverseRange(generateKey(boardId), offset, offset + limit - 1)
                .stream().map(Long::valueOf).toList();
    }

    public List<Long> readAllInfiniteScroll(Long boardId, Long lastArticleId, Long limit) {
        return redisTemplate.opsForZSet().reverseRangeByLex(
                generateKey(boardId),
                lastArticleId == null ?
                        Range.unbounded() :
                        Range.leftUnbounded(Range.Bound.exclusive(toPaddedString(lastArticleId))),
                Limit.limit().count(limit.intValue())
        ).stream().map(Long::valueOf).toList();
    }

    public String toPaddedString(Long articleId) {
        return "%019d".formatted(articleId);
        // 1234 -> 0000000000000001234
    }

    public String generateKey(Long articleId) {

        return String.format(KEY_FORMAT, articleId);
    }

}
