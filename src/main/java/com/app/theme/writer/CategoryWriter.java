package com.app.theme.writer;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.app.theme.model.Category;

@Component
public class CategoryWriter implements ItemWriter<Category> {

    @Autowired
    private JdbcTemplate destinationJdbcTemplate; // JdbcTemplate pour la base de données de destination
    private Map<Long, Long> categoryMappings=new HashMap<Long, Long>() ;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        // Récupérer categoryMappings depuis le contexte du job
        JobExecution jobExecution = stepExecution.getJobExecution();
        // Journaliser les données insérées
        if (jobExecution.getExecutionContext().containsKey("categoryMappings")) {
        	this.categoryMappings = (Map<Long, Long>) jobExecution.getExecutionContext().get("categoryMappings");
            System.out.println("CategoryWriter >> beforeStep >> Catégories loaded : " + categoryMappings);
        }  
        
    }
    @AfterStep
	public void afterStep(StepExecution stepExecution) {
		// Récupérer categoryMappings depuis le contexte du job
		// Journaliser les données insérées
		if (stepExecution.getJobExecution().getExecutionContext().containsKey("categoryMappings")) {
			
			if (categoryMappings.isEmpty())
				System.out.println("  .............    Catégories is empty : " + categoryMappings); 
			else System.out.println("Catégories loaded : " + categoryMappings);
		}
		stepExecution.getJobExecution().getExecutionContext().put("categoryMappings", categoryMappings);
	}

    @Override
    public void write(Chunk<? extends Category> categories) throws Exception {
       
        for (Category category : categories) {
            if (!categoryMappings.containsKey(category.getId())  || (categoryMappings.get(category.getId()) == null)) {
                // La catégorie n'existe pas dans la base de données de destination, l'insérer
                String sql = "INSERT INTO category (name) VALUES (?) RETURNING id";
                Long newCategoryId = destinationJdbcTemplate.queryForObject(sql, Long.class, category.getName());

                // Mettre à jour categoryMappings avec le nouvel ID
                categoryMappings.put(category.getId(), newCategoryId);
                category.setId(newCategoryId); // Mettre à jour l'ID de la catégorie
            }
        }
    }
 
}