package com.app.theme.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.app.theme.model.Category;

@Component
public class CategoryProcessor implements ItemProcessor<Category, Category> {

	@Autowired
	private JdbcTemplate destinationJdbcTemplate; // JdbcTemplate pour la base de données de destination
	private Map<Long, Long> categoryMappings = new HashMap<Long, Long>();

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		// Récupérer categoryMappings depuis le contexte du job
		// Journaliser les données insérées
		if (stepExecution.getJobExecution().getExecutionContext().containsKey("categoryMappings")) {
			this.categoryMappings = (Map<Long, Long>) stepExecution.getJobExecution().getExecutionContext()
					.get("categoryMappings");
			System.out.println(" .............   Catégories loaded : " + categoryMappings);
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
	public Category process(Category category) throws Exception {
		// Vérifier si la catégorie existe déjà dans la base de données de destination

		// Vérifier si categoryMappings est null
		if (categoryMappings == null) {
			throw new IllegalStateException("categoryMappings n'a pas été initialisé.");
		}

		// Vérifier si la catégorie existe déjà dans la base de données de destination
		String sql = "SELECT id FROM category WHERE name = ?";
		if (categoryMappings == null) {
			throw new IllegalStateException("categoryMappings n'a pas été initialisé.");
		}

		List<Long> destinationCategoryIds = destinationJdbcTemplate.queryForList(sql, Long.class, category.getName());

		if (!destinationCategoryIds.isEmpty()) {
			Long destinationCategoryId = destinationCategoryIds.get(0);
			if (!categoryMappings.containsKey(category.getId())  || (categoryMappings.get(category.getId()) == null)) {
				categoryMappings.put(category.getId(), destinationCategoryId);
			}
			//category.setId(destinationCategoryId);
			System.out.println("Catégorie existante mappée : " + category.getName() + " (ID source : "
					+ category.getId() + ", ID destination : " + destinationCategoryId + ")");
		} else {
			categoryMappings.put(category.getId(), null);
			System.out.println("Nouvelle catégorie à insérer : " + category.getName());
		}

		return category;

	}

}
