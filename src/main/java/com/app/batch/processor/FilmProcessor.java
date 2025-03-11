package com.app.batch.processor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.app.batch.entities.Film;
import com.app.batch.mapper.FilmDTO;

@Component
public class FilmProcessor implements ItemProcessor<FilmDTO, Film> {

    @Override
    public Film process(FilmDTO filmDto) throws Exception {
    	Film film = new Film();
       
        film.setTitle(filmDto.getTitle());
       
        film.setDescription(filmDto.getDescription());
        film.setLength(filmDto.getLength());
        film.setRating(filmDto.getRating());
        film.setRelease_year(filmDto.getRelease_year());
        film.setRental_rate(filmDto.getRental_rate());
        film.setReplacement_cost(filmDto.getReplacement_cost());
        film.setSpecial_features(filmDto.getSpecial_features());
    	  film.setLast_update(filmDto.getLast_update());

          System.out.println("Film  object: " + filmDto.toString());
        // film.setLast_upate( filmDto.getString("last_update")); 
        return film;
    }

	private Date convertStringToDate(String special_features) {
		        String format = "dd/MM/yyyy";      // Format of the date string
		        Date date = new Date();
		        try {
		            SimpleDateFormat sdf = new SimpleDateFormat(format);
		             date = sdf.parse(special_features);

		            System.out.println("Date object: " + date);

		            // To format the Date object back to a string in a different format:
		            String formattedDate = sdf.format(date);
		            System.out.println("Formatted date: " + formattedDate);


		        } catch (ParseException e) {
		            System.err.println("Error parsing date: " + e.getMessage());
		        }
		return date;
	}
    
}
