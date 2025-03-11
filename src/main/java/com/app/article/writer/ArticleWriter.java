package com.app.article.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import com.app.article.model.Article;

//@Component
public class ArticleWriter implements ItemWriter<Article>{

	@Override
	public void write(Chunk<? extends Article> chunk) throws Exception {
		// TODO Auto-generated method stub
		
	} 
	
}
