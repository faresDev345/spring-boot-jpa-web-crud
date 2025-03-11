package com.app.article.processor;

import org.springframework.batch.item.ItemProcessor;

import com.app.article.model.Article;
import com.app.article.model.ArticleDTO;

//@Component
public class ArticleProcessor implements ItemProcessor<ArticleDTO, Article> {

	@Override
	public Article process(ArticleDTO item) throws Exception {
		// TODO Auto-generated method stub
		return null;
	} 
	
}

	 
