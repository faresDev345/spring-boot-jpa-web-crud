package com.app.theme.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import com.app.theme.model.Theme;

//@Component
public class ThemeWriter implements ItemWriter<Theme>{

	@Override
	public void write(Chunk<? extends Theme> chunk) throws Exception {
		// TODO Auto-generated method stub
		
	} 
	
}
