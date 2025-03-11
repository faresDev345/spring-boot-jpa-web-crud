package com.app.theme.processor;

import org.springframework.batch.item.ItemProcessor;

import com.app.theme.model.Theme;
import com.app.theme.model.ThemeDTO;

//@Component
public class ThemeProcessor implements ItemProcessor<ThemeDTO, Theme> {

	@Override
	public Theme process(ThemeDTO item) throws Exception {
		// TODO Auto-generated method stub
		return null;
	} 
	
}

	 
