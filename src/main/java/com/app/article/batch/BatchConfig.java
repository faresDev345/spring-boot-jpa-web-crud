package com.app.article.batch; 

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import com.app.article.listener.ArticleWriterListener;
import com.app.article.model.Article;

import lombok.extern.log4j.Log4j2;

@Configuration
@EnableBatchProcessing 
@Log4j2
public class BatchConfig {
	
	 // JdbcTemplates
    @Bean
    public JdbcTemplate sourceJdbcTemplate(@Qualifier("sourceDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public JdbcTemplate destinationJdbcTemplate(@Qualifier("destinationDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

 // Job et Steps (nouvelle API)
    @Bean
    public Job copyArticleJob(JobRepository jobRepository, Step copyArticlesStep) {
        return new JobBuilder("copyArticleJob", jobRepository)
                .start(copyArticlesStep) 
                .build();
    }

    // Step 1:  : Copie des articles

    @Bean
    public Step copyArticlesStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        ItemReader<Article> articleReader,
        ItemProcessor<Article, Article> articleProcessor,
        ItemWriter<Article> articleWriter
    ) {
        return new StepBuilder("copyArticlesStep", jobRepository)
                .<Article, Article>chunk(10, transactionManager)
                .reader(articleReader)
                .processor(articleProcessor)
                .writer(articleWriter)
                .listener(new ArticleWriterListener())
                .build();
    }
    // Readers
 

    @Bean
    public JdbcCursorItemReader<Article> articleReader(@Qualifier("sourceDataSource") DataSource sourceDataSource) {
        return new JdbcCursorItemReaderBuilder<Article>()
                .name("articleReader")
                .sql("SELECT id, title, release_year, category_id FROM article")
                .rowMapper((rs, rowNum) -> new Article(
                    rs.getLong("id"),
                    rs.getString("title"),
                    rs.getInt("release_year"),
                    rs.getLong("category_id")
                ))
                .dataSource(sourceDataSource)
                .build();
    }


    // Processors
 

    @Bean
    @StepScope
    public ItemProcessor<Article, Article> articleProcessor(
        @Qualifier("destinationJdbcTemplate") JdbcTemplate destinationJdbcTemplate,
        @Value("#{jobExecutionContext['categoryMappings']}") Map<Long, Long> categoryMappings
    ) {
        return article -> {
            String sql = "SELECT id FROM article WHERE title = ? AND release_year = ?";
            List<Long> existingIds = destinationJdbcTemplate.queryForList(sql, Long.class, article.getTitle(), article.getReleaseYear());
            if (!existingIds.isEmpty()) { // Filtre les articles existants
            	System.out.println("Filtre les articles existants : " + article.getTitle());
	            return null;
            } else {

                // Mise à jour de category_id avec l'ID de destination
                article.setCategoryId(categoryMappings.get(article.getCategoryId()));
                return article;
            }
        };
    }

    // Writers
  

    @Bean
    @StepScope
    public JdbcBatchItemWriter<Article> articleWriter(@Qualifier("destinationJdbcTemplate") JdbcTemplate destinationJdbcTemplate) {
        return new JdbcBatchItemWriterBuilder<Article>()
                .sql("INSERT INTO article (title, release_year, category_id) VALUES (:title, :releaseYear, :categoryId)")
                .beanMapped()
                .dataSource(destinationJdbcTemplate.getDataSource())
                .build();
    }
}