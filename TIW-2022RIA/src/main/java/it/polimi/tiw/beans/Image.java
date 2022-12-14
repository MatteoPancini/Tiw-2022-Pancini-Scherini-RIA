package it.polimi.tiw.beans;

import java.util.Date;

public class Image implements Comparable<Image>{
	private int idImage;
	private int idUser;
	private int idAlbum;
	private String title;
	private String description;
	private Date date;
	private String path;
	private String username;
	private int order;
	
	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public Image() {}
	
	
	public Image(int idImage, int idUser, int idAlbum, String title, String description, Date date, String path, int order) {
		this.idImage = idImage;
		this.idUser = idUser;
		this.idAlbum = idAlbum;
		this.title = title;
		this.description = description;
		this.date = date;
		this.path = path;
		this.order = order;
	}


	public int getIdImage() {
		return idImage;
	}


	public void setIdImage(int idImage) {
		this.idImage = idImage;
	}


	public int getIdUser() {
		return idUser;
	}


	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}


	public int getIdAlbum() {
		return idAlbum;
	}


	public void setIdAlbum(int idAlbum) {
		this.idAlbum = idAlbum;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}



	
	public Date getDate() {
		return date;
	}


	public void setDate(Date date) {
		this.date = date;
	}

	

	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}


	public int getOrder() {
		return order;
	}


	public void setOrder(int order) {
		this.order = order;
	}
	
	@Override
	public int compareTo(Image i) {
		return i.getOrder() - this.order;
	}
	
	
	

}
