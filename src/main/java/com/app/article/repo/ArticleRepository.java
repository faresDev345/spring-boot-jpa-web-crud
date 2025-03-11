package com.app.article.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.app.article.model.Article;
@Repository
public interface ArticleRepository extends CrudRepository<Article, Long> {
	   List<Article> findByTitle(String name	);
	    long countByTitle(String name	);
}
