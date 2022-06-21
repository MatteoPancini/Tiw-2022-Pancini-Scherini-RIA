package it.polimi.tiw.dao;

import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Image;

public class ImageDAO {
	private Connection connection;	
	
	public ImageDAO(Connection connection) {
        this.connection = connection;
    }
	
	public List<Image> findAllAlbumImages(int idAlbum) throws SQLException {
		List<Image> albumImagesList = new ArrayList<>();
		
        String imagesQuery = "SELECT * FROM image WHERE idAlbum = ? ORDER BY orderNum DESC";
        
        ResultSet resultSet = null;
        PreparedStatement pstatement = null;
        
        try {
        	pstatement = connection.prepareStatement(imagesQuery);
        	pstatement.setInt(1, idAlbum);
        	resultSet = pstatement.executeQuery();
        	
        	while(resultSet.next()) {
        		Image albumImage = new Image();
        		albumImage.setIdImage(resultSet.getInt("idImage"));
        		albumImage.setIdUser(resultSet.getInt("idUser"));
        		albumImage.setIdAlbum(resultSet.getInt("idAlbum"));
        		albumImage.setTitle(resultSet.getString("title"));
        		albumImage.setDescription(resultSet.getString("description"));
        		albumImage.setDate(new Date(resultSet.getDate("date").getTime()));

            /*
        		albumImage.setDate(new Date(resultSet.getDate("date").getTime()).toInstant()
              	      .atZone(ZoneId.systemDefault())
            	      .toLocalDateTime());
            */
        		albumImage.setPath(resultSet.getString("path"));
        		
        		albumImagesList.add(albumImage);
        				
        	}
        } catch(SQLException e) {
        	e.printStackTrace();
            throw new SQLException(e);
        } finally {
              try {
                resultSet.close();
              } catch (Exception e1) {
                throw new SQLException(e1);
              }
              try {
                pstatement.close();
              } catch (Exception e2) {
                throw new SQLException(e2);
              }
          }
        return albumImagesList;
	}
	
	public Image getImageFromId(int imageId) throws SQLException{
		Image selectedImage = new Image();
		
		String query = "SELECT idImage,image.idUser,idAlbum,title,description,date,path,username FROM image JOIN user ON image.idUSer=user.idUser WHERE idImage = ?";
		
		ResultSet resultSet = null;
        PreparedStatement pstatement = null;
        
        try {
        	pstatement = connection.prepareStatement(query);
        	pstatement.setInt(1, imageId);
        	resultSet = pstatement.executeQuery();
        	resultSet.next();
        	selectedImage.setIdImage(resultSet.getInt("idImage"));
        	selectedImage.setIdUser(resultSet.getInt("idUser"));
        	selectedImage.setIdAlbum(resultSet.getInt("idAlbum"));
        	selectedImage.setTitle(resultSet.getString("title"));
        	selectedImage.setDescription(resultSet.getString("description"));
        	selectedImage.setDate(new Date(resultSet.getDate("date").getTime()));

          /*
        	selectedImage.setDate(new Date(resultSet.getDate("date").getTime()).toInstant()
          	      .atZone(ZoneId.systemDefault())
          	      .toLocalDateTime());
          */
        	selectedImage.setPath(resultSet.getString("path"));
        	selectedImage.setUsername(resultSet.getString("username"));
        }catch(SQLException e) {
        	e.printStackTrace();

        } finally {
              try {
                resultSet.close();
              } catch (Exception e1) {
                throw new SQLException(e1);
              }
              try {
                pstatement.close();
              } catch (Exception e2) {
                throw new SQLException(e2);
              }
        }
        return selectedImage;
	}
	
	public List<Image> getImagesPositionForOrdering(int albumId, String minId, String maxId) throws SQLException{
		
		List<Image> images = new ArrayList<>();

		
		ResultSet resultSet = null;
        PreparedStatement pstatement = null;
		
		String query = "SELECT idImage, orderNum FROM image WHERE idImage <= ? AND idImage >= ? AND idAlbum = ? ORDER BY idImage";
	
		int minId_int = Integer.parseInt(minId);
		int maxId_int = Integer.parseInt(maxId);

		
		try {
        	pstatement = connection.prepareStatement(query);
        	pstatement.setInt(1, minId_int);
        	pstatement.setInt(2, maxId_int);
        	pstatement.setInt(3, albumId);
        	resultSet = pstatement.executeQuery();
        	while(resultSet.next()) {
        		Image image = new Image();
        		image.setIdImage(resultSet.getInt("idImage"));
        		image.setOrder(resultSet.getInt("orderNum"));
        		images.add(image);
        	}
        	
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
              try {
                resultSet.close();
              } catch (Exception e1) {
                throw new SQLException(e1);
              }
              try {
                pstatement.close();
              } catch (Exception e2) {
                throw new SQLException(e2);
              }
        }	
		
		return images;
	}
	
	public void updateOrder(int idImage, int orderNum) throws SQLException{
		String query = "UPDATE image SET orderNum = ? WHERE idImage = ?";
		 try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	            preparedStatement.setInt(1, orderNum);
	            preparedStatement.setInt(2, idImage);
	            preparedStatement.executeUpdate();
	        }
	}
	
	
	public void createNewImage(int idUser, int albumId, String imageTitle, String description, String imagePath) throws SQLException {
        String query = "INSERT INTO image (idUser, idAlbum, title, description, date, path, orderNum) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        int orderNum = findLastId() +1;
        
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, idUser);
            preparedStatement.setInt(2, albumId);
            preparedStatement.setString(3, imageTitle);
            preparedStatement.setString(4, description);
            preparedStatement.setDate(5, new java.sql.Date(new Date().getTime()));
            preparedStatement.setString(6, imagePath);
            preparedStatement.setInt(7, orderNum);
            preparedStatement.executeUpdate();
        }
    }
	
	private int findLastId() throws SQLException{
		
		ResultSet resultSet = null;
        PreparedStatement pstatement = null;
        int res=0;
		
		String query = "SELECT idImage FROM image ORDER BY idImage DESC LIMIT 1";
		 try {
	        	pstatement = connection.prepareStatement(query);
	        	resultSet = pstatement.executeQuery();
	        	resultSet.next();
	        	res = resultSet.getInt("idImage");
	        	return res;
	        	
		 }catch(SQLException e) {
			 e.printStackTrace();
		 }
		 finally {
             try {
               resultSet.close();
             } catch (Exception e1) {
               throw new SQLException(e1);
             }
             try {
               pstatement.close();
             } catch (Exception e2) {
               throw new SQLException(e2);
             }
		 }
		return res;
	}
}
