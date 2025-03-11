package com.app.article.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.app.article.model.ArticleDTO;

@Component
public class ArticleMapper implements RowMapper<ArticleDTO> {
	String formatDate = "yyyy-MM-dd";
    @Override
    public ArticleDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
    	ArticleDTO article = new ArticleDTO();
        article.setId(rs.getLong("id"));
        article.setTitle(rs.getString("title"));
        article.setLast_update( rs.getString("last_update"));
      
        article.setDescription(rs.getString("description"));
        article.setLength(rs.getInt("length"));
        article.setRating(rs.getString("rating"));
        article.setRelease_year(rs.getInt("release_year"));
        article.setRental_rate(rs.getDouble("rental_rate"));
        article.setReplacement_cost(rs.getDouble("replacement_cost"));
        article.setSpecial_features(rs.getString("special_features"));

        return article;
    }
    Date convertStringToDate(String dateString, String format) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(format);  
        Date mdate = formatter.parse(dateString);
        return mdate;
    }
}
