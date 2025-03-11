package com.app.batch.writer;

import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.app.batch.entities.Film;
import com.app.batch.repo.FilmRepository;

@Component
public class FilmWriter implements ItemWriter<Film>{
	
	@Autowired(required=true)
	FilmRepository myRepository;
 
	public void write(Chunk<? extends Film> items) throws Exception {
		
		for (Film film : items) {
			System.out.println("film    : Writing data    : " + film.getId()+" : "+film.getTitle()+" : "+film.getDescription()
			+"  ");
			myRepository.save(film);
		}
	}

	public void write(List<? extends Film> items) throws Exception {
		
		for (Film film : items) {
			System.out.println("film    : Writing data    : " + film.getId()+" : "+film.getTitle()+" : "+film.getDescription()
			+"  ");
			myRepository.save(film);
			if(!film.getTitle().isEmpty() && myRepository.findByTitle(film.getTitle()).size()>0 ) {
				System.out.println("Emplye déjà renseigné   : " + film.getId()+" : "+film.getTitle()+" : "+film.getDescription());
			} else  myRepository.save(film);
		}
		
	}
}
