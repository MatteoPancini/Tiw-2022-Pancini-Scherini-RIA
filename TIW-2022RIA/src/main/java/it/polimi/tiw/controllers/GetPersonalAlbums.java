package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class GetPersonalAlbums
 */
@WebServlet("/GetPersonalAlbums")
public class GetPersonalAlbums extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	  


	    
	public GetPersonalAlbums() {
		super();
		}

	    
	   public void init() throws ServletException {
	      connection = ConnectionHandler.getConnection(getServletContext());
	    }
	    
	    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
	    
	    List<Album> userAlbumList = null;
	      
	    AlbumDAO albumDAO = new AlbumDAO(connection);
	    int userId = ((User) session.getAttribute("user")).getIdUser();
	      
	    try {
	      userAlbumList = albumDAO.findUserAlbums(userId);

	    } catch(SQLException e) {
	    	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Cannot find album list!");
            return;
	    }
	      
	    Gson gson = new GsonBuilder().setDateFormat("yyyy MM dd").create();
        String json = gson.toJson(userAlbumList);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
	    
	}

	

}
