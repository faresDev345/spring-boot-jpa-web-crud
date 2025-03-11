package com.app.batch.config;
 
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.app.batch.entities.Film;
import com.app.batch.mapper.FilmDTO;
import com.app.batch.processor.FilmProcessor;
import com.app.batch.writer.FilmWriter;

@Configuration
@EnableBatchProcessing
public class BatchConfigImportFile {

	 @Value("${file.input}")
	 private String fileInput;

	@Autowired
	private FilmProcessor processorFlm;

	@Autowired
	private FilmWriter writerFlm;
 
	@Bean
    @StepScope // <-- Required to access JobParameters	
	public FlatFileItemReader<FilmDTO> readerFilm(@Value("#{jobParameters['inputFile']}") String fileInputPath ) {
		 	  System.out.println("  ################# jobParameters  Input file path: " + fileInputPath); // Debugging

		if(fileInputPath !=null && !fileInputPath.isEmpty()) {
			return new FlatFileItemReaderBuilder<FilmDTO>().name("filmItemReader").linesToSkip(1)
					// reader.setResource(new FileSystemResource(inputFilePath)); // Use the path from job parameters! 
					  .resource(new FileSystemResource(fileInputPath))
					.linesToSkip(1).targetType(FilmDTO.class).lineMapper(lineMapperFilm())
					.build();
		}  
		return new FlatFileItemReaderBuilder<FilmDTO>().name("filmItemReader").linesToSkip(1)
				  .resource(new ClassPathResource(fileInput))
				.linesToSkip(1).targetType(FilmDTO.class).lineMapper(lineMapperFilm())
				.build();
	}
	@Bean
	@StepScope // Important for late binding of job parameters
	public FlatFileItemReader<FilmDTO> filmReader(@Value("#{jobParameters['inputFile']}") String inputFilePath) {
	    FlatFileItemReader<FilmDTO> reader = new FlatFileItemReader<>();
	    reader.setResource(new FileSystemResource(inputFilePath)); // Use the path from job parameters!
	    reader.setLinesToSkip(1); // Skip header row if present
	    reader.setLineMapper(new DefaultLineMapper<FilmDTO>() {
		    @Override
		    public FilmDTO mapLine(String line, int lineNumber) throws Exception {
		        String[] fields = line.split(","); // Adjust delimiter if needed
		       FilmDTO film = new FilmDTO();
		       // String[] fields = line.split(","); // Your delimiter
		        //Film film = new Film();
		        film.setId(  Long.parseLong(fields[0]));       // Id (Integer)
		        film.setTitle(fields[1]);                     // title (String)
		        film.setDescription(fields[2]);              // description (String)
		        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // For last_update
		        film.setLast_update(dateFormat.parse(fields[3])); // last_update (Date)
		        film.setLength(Integer.parseInt(fields[4]));   // length (Integer)
		        film.setRating(fields[5]);                   // rating (String)
		        film.setRelease_year(Integer.parseInt(fields[6])); // release_year (Integer)
		        film.setRental_rate(Double.parseDouble(fields[7])); // rental_rate (Double)
		        film.setReplacement_cost(Double.parseDouble(fields[8])); // replacement_cost (Double)
		        film.setSpecial_features(fields[9]); 
		     
		        return film;
		    }
		});
	    return reader;
	} 
	public FlatFileItemReader<FilmDTO> readerFilm2(@Value("#{jobExecutionContext['inputFile']}") String fileInputPath) {
	     FlatFileItemReader<FilmDTO> reader = new FlatFileItemReader<FilmDTO>();
	     reader.setResource(new ClassPathResource(fileInputPath));
	     reader.setLineMapper(new DefaultLineMapper<FilmDTO>() {{
	        setLineTokenizer(new DelimitedLineTokenizer() {{
	            setNames(new String[] {"Id", "title", "description", "last_update", "length", "rating", "release_year", "rental_rate", "replacement_cost", "special_features" });
	        }});
	        setFieldSetMapper(new BeanWrapperFieldSetMapper<FilmDTO>() {{
	            setTargetType(FilmDTO.class);
	        }});
	    }});
	    return reader;
	  }

	private LineMapper<FilmDTO> lineMapperFilm() {
		DefaultLineMapper<FilmDTO> lineMapper = new DefaultLineMapper<>();

		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		// * Id, title, description, last_update, length, rating, release_year, rental_rate, replacement_cost, special_features
		lineTokenizer.setNames("Id", "title", "description", "last_update", "length", "rating", "release_year", "rental_rate", "replacement_cost", "special_features");
		BeanWrapperFieldSetMapper<FilmDTO> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(FilmDTO.class); 
		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);
		return lineMapper;

	}

    @Primary  // Mark this bean as primary
	@Bean(name = "IMPORT_CSV_JOB") 
	public Job jobFilm(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager  ) {
		return new JobBuilder("IMPORT_CSV_JOB", jobRepository).incrementer(new RunIdIncrementer())
				.listener(new MyJobListener()).start(stepJobFilm(jobRepository, platformTransactionManager)).build();
	}

	@Bean(name = "stepJobFilm") 
	public Step stepJobFilm(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
		return new StepBuilder("stepJobFilm", jobRepository).<FilmDTO, Film>chunk(10, platformTransactionManager)
				.reader(filmReader("")) 
				.processor(processorFlm).writer(writerFlm)
				.faultTolerant() 
    			.skipLimit(5)   
    			.skip(FlatFileParseException.class) 
				.noSkip(FileNotFoundException.class)
				.listener(new MyStepListener())
	            .taskExecutor(taskExecutor()).build();
	}

	public TaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();
		// two threads run concurrently for batch processing
		simpleAsyncTaskExecutor.setConcurrencyLimit(2);
		return simpleAsyncTaskExecutor;
	}

	 
}
