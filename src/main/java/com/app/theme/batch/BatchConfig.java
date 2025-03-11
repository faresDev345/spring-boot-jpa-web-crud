package com.app.theme.batch; 

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

import com.app.theme.listener.CategoryWriterListener;
import com.app.theme.listener.ThemeWriterListener;
import com.app.theme.model.Category;
import com.app.theme.model.Theme;
import com.app.theme.processor.CategoryProcessor;
import com.app.theme.writer.CategoryWriter;

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
    public Job copyThemeJob(JobRepository jobRepository, Step copyCategoriesStep, Step copyThemesStep) {
        return new JobBuilder("copyThemeJob", jobRepository)
                .start(copyCategoriesStep)
                .next(copyThemesStep)
                .build();
    }

    // Step 1: Copie des catégories
    @Bean
    public Step copyCategoriesStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        ItemReader<Category> categoryReader,
        CategoryProcessor categoryProcessor,
        CategoryWriter categoryWriter
    ) {
        return new StepBuilder("copyCategoriesStep", jobRepository)
                .<Category, Category>chunk(10, transactionManager)
                .reader(categoryReader)
                .processor(categoryProcessor)
                .writer(categoryWriter)
                .listener(new CategoryWriterListener())
                .build();
    }


    // Step 2: Copie des themes

    @Bean
    public Step copyThemesStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        ItemReader<Theme> themeReader,
        ItemProcessor<Theme, Theme> themeProcessor,
        ItemWriter<Theme> themeWriter
    ) {
        return new StepBuilder("copyThemesStep", jobRepository)
                .<Theme, Theme>chunk(10, transactionManager)
                .reader(themeReader)
                .processor(themeProcessor)
                .writer(themeWriter)
                .listener(new ThemeWriterListener())
                .build();
    }
    // Readers
    @Bean
    @StepScope 
    public JdbcCursorItemReader<Category> categoryReader(@Qualifier("sourceDataSource") DataSource sourceDataSource) {
        return new JdbcCursorItemReaderBuilder<Category>()
                .name("categoryReader")
                .sql("SELECT id, name FROM category")
                .rowMapper((rs, rowNum) -> new Category(rs.getLong("id"), rs.getString("name")))
                .dataSource(sourceDataSource)
                .build();
    }

    @Bean
    public JdbcCursorItemReader<Theme> themeReader(@Qualifier("sourceDataSource") DataSource sourceDataSource) {
        return new JdbcCursorItemReaderBuilder<Theme>()
                .name("themeReader")
                .sql("SELECT id, title, release_year, category_id FROM theme")
                .rowMapper((rs, rowNum) -> new Theme(
                    rs.getLong("id"),
                    rs.getString("title"),
                    rs.getInt("release_year"),
                    rs.getLong("category_id")
                ))
                .dataSource(sourceDataSource)
                .build();
    }


    // Processors
    //@Bean
    @StepScope
    public ItemProcessor<Category, Category> categoryProcessorOld(
        @Qualifier("destinationJdbcTemplate") JdbcTemplate destinationJdbcTemplate
    ) {
        return category -> {
            String sql = "SELECT id FROM category WHERE name = ?";
            List<Long> existingIds = destinationJdbcTemplate.queryForList(sql, Long.class, category.getName());
            return existingIds.isEmpty() ? category : null; // Filtre les catégories existantes
        };
    }

    @Bean
    @StepScope
    public ItemProcessor<Theme, Theme> themeProcessor(
        @Qualifier("destinationJdbcTemplate") JdbcTemplate destinationJdbcTemplate,
        @Value("#{jobExecutionContext['categoryMappings']}") Map<Long, Long> categoryMappings
    ) {
        return theme -> {
            String sql = "SELECT id FROM theme WHERE title = ? AND release_year = ?";
            List<Long> existingIds = destinationJdbcTemplate.queryForList(sql, Long.class, theme.getTitle(), theme.getReleaseYear());
            if (!existingIds.isEmpty()) { // Filtre les themes existants
            	System.out.println("Filtre les themes existants : " + theme.getTitle());
	            return null;
            } else {

                // Mise à jour de category_id avec l'ID de destination
                theme.setCategoryId(categoryMappings.get(theme.getCategoryId()));
                return theme;
            }
        };
    }

    // Writers
    //Bean
    @StepScope
    public JdbcBatchItemWriter<Category> categoryWriterOld(@Qualifier("destinationJdbcTemplate") JdbcTemplate destinationJdbcTemplate) {
        return new JdbcBatchItemWriterBuilder<Category>()
                .sql("INSERT INTO category (name) VALUES (:name)")
                .beanMapped()
                .dataSource(destinationJdbcTemplate.getDataSource())
                .build();
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<Theme> themeWriter(@Qualifier("destinationJdbcTemplate") JdbcTemplate destinationJdbcTemplate) {
        return new JdbcBatchItemWriterBuilder<Theme>()
                .sql("INSERT INTO theme (title, release_year, category_id) VALUES (:title, :releaseYear, :categoryId)")
                .beanMapped()
                .dataSource(destinationJdbcTemplate.getDataSource())
                .build();
    }
}