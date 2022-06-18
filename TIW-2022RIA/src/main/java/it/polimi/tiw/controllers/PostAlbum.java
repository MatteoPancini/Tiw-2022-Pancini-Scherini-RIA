package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.utils.ConnectionHandler;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/PostAlbum")
@MultipartConfig

public class PostAlbum extends HttpServlet {
	private static final long serialVersionUID = 1L;

    private static Connection connection;


    @Override
    public void init() throws ServletException{
        connection = ConnectionHandler.getConnection(getServletContext());
    }
    
   
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String title = null;
        
        try {
        	title = StringEscapeUtils.escapeJava(request.getParameter("albumTitle"));
        	System.out.println("TITLE: " + title);
        	if(title.isEmpty() || title == null) {
        		throw new Exception("Missing or empty credential value");
        	}
        } catch(Exception e) {
        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Credentials must be not null");
			return;
		}

       
     	AlbumDAO albumDAO = new AlbumDAO(connection);
		HttpSession session = request.getSession();
		int idUser = ((User) session.getAttribute("user")).getIdUser();
     	
        try {
            albumDAO.createNewAlbum(idUser, title);
        }
        catch (SQLException e) {
        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("SQL Exception");
			return;
        }
        response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(title);
    }
    
    public void destroy() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e){
				System.err.println(e.getMessage());
			}
		}
	}
}