package com.app.batch.config;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.app.batch.entities.Film;
import com.app.batch.mapper.FilmMapper;
import com.app.batch.processor.FilmProcessor;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {
	 @Value("${file.output}")
	 private String fileOutput;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private FilmProcessor filmProcessor;
    @Autowired
    private FilmMapper filmMapper;
 
 
    @Bean(name = "EXPORT_DATA_JOB")
	public Job importFilmsJob(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) throws Exception {
		return new JobBuilder("EXPORT_DATA_JOB", jobRepository).start(step(jobRepository, platformTransactionManager)).build();
	}
    @Async
	@Bean(name = "step_films")
	public Step step(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) throws Exception {
		return new StepBuilder("step_films", jobRepository).<Film, Film>chunk(10, platformTransactionManager).reader(dbReader(null, null))
				.processor(processor()).writer(fileWriter()).taskExecutor(taskExecutor()).build();
	}
    

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(64);
        executor.setMaxPoolSize(64);
        executor.setQueueCapacity(64);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix("MultiThreaded-");
        return executor;
    }

    @Bean
    @StepScope
    public ItemStreamReader<Film> dbReader(@Value("#{jobParameters[date]}") Date date,
                                           @Value("#{jobParameters[film_rating]}") String film_rating)
            throws Exception {

        System.out.println(date);
        System.out.println(film_rating);

        return (ItemStreamReader<Film>) itemStreamReader(filmMapper, "select * ",  "from film",  "where id_generated='" + film_rating + "'");
    }

    @Bean
    public ItemProcessor<Film, Film> processor() {

        return new ItemProcessor<Film, Film>() {
            @Override
            public Film process(Film film) throws Exception {
                return film;
            }
        };

    }

    @Bean
    @StepScope
    public FlatFileItemWriter<Film> fileWriter() {
        FlatFileItemWriter<Film> writer = new FlatFileItemWriter<Film>();
        writer.setResource(new FileSystemResource(fileOutput));

        writer.setHeaderCallback(new FlatFileHeaderCallback() {
            @Override
            public void writeHeader(Writer writer) throws IOException {
                writer.write("Id, title, description, last_update, length, rating, release_year, rental_rate, replacement_cost, special_features");
            }
        });

        DelimitedLineAggregator<Film> lineAggregator = new DelimitedLineAggregator<Film>();
        lineAggregator.setDelimiter(",");

        BeanWrapperFieldExtractor<Film> fieldExtractor = new BeanWrapperFieldExtractor<Film>();
        fieldExtractor.setNames(new String[]{"id", "title", "description", "last_update", "length", "rating", "release_year", "rental_rate", "replacement_cost", "special_features"});

        lineAggregator.setFieldExtractor(fieldExtractor);

        writer.setLineAggregator(lineAggregator);
        return writer;
    }

    @StepScope
    public ItemStreamReader<? extends Object> itemStreamReader(RowMapper rowMapper, String select, String from, String where) throws Exception {
        JdbcPagingItemReader<Object> reader = new JdbcPagingItemReader<Object>();
        reader.setDataSource(dataSource);
        final SqlPagingQueryProviderFactoryBean sqlPagingQueryProviderFactoryBean = new SqlPagingQueryProviderFactoryBean();
        sqlPagingQueryProviderFactoryBean.setDataSource(dataSource);
        // sqlPagingQueryProviderFactoryBean.setDataSource(dataSource);
        sqlPagingQueryProviderFactoryBean.setSelectClause(select);
        sqlPagingQueryProviderFactoryBean.setFromClause(from);
        // sqlPagingQueryProviderFactoryBean.setWhereClause(where);
        sqlPagingQueryProviderFactoryBean.setSortKey("id");
        reader.setQueryProvider(sqlPagingQueryProviderFactoryBean.getObject());
        reader.setPageSize(1000000);
        reader.setRowMapper(rowMapper);
        reader.afterPropertiesSet();
        reader.setSaveState(false);
        return reader;
    }


}
