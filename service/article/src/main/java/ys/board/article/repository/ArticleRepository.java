package ys.board.article.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ys.board.article.entity.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

}
