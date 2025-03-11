package com.app.theme.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.app.theme.model.ThemeDTO;

@Component
public class ThemeMapper implements RowMapper<ThemeDTO> {
	String formatDate = "yyyy-MM-dd";
    @Override
    public ThemeDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
    	ThemeDTO theme = new ThemeDTO();
        theme.setId(rs.getLong("id"));
        theme.setTitle(rs.getString("title"));
        theme.setLast_update( rs.getString("last_update"));
      
        theme.setDescription(rs.getString("description"));
        theme.setLength(rs.getInt("length"));
        theme.setRating(rs.getString("rating"));
        theme.setRelease_year(rs.getInt("release_year"));
        theme.setRental_rate(rs.getDouble("rental_rate"));
        theme.setReplacement_cost(rs.getDouble("replacement_cost"));
        theme.setSpecial_features(rs.getString("special_features"));

        return theme;
    }
    Date convertStringToDate(String dateString, String format) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(format);  
        Date mdate = formatter.parse(dateString);
        return mdate;
    }
}
