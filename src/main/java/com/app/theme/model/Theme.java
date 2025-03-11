package com.app.theme.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Theme {
	@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;
	 

	@Column(name = "title")
	@NonNull 
    private String title;
	@Column
    private int releaseYear;
	@Column
    private Long categoryId;
	
	public Theme( ) {
		 super();
		}
		
		public Theme(long id, String title, int year, long categoryId) {
			 super();
			 setId(id);
			 setTitle(title);
			 setReleaseYear(year);
			 setCategoryId(categoryId);
		}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getReleaseYear() {
		return releaseYear;
	}
	public void setReleaseYear(int releaseYear) {
		this.releaseYear = releaseYear;
	}
	public Long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	
	
}