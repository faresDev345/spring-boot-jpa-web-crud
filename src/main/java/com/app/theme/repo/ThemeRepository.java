package com.app.theme.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.app.theme.model.Theme;
@Repository
public interface ThemeRepository extends CrudRepository<Theme, Long> {
	   List<Theme> findByTitle(String name	);
	    long countByTitle(String name	);
}
