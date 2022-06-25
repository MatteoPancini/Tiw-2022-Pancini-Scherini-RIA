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
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.dao.ImageDAO;
import it.polimi.tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class GetAlbumPage
 */
@WebServlet("/GetAlbumPage")
public class GetAlbumPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private Connection connection = null;

    public GetAlbumPage() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void init() throws ServletException {
    	connection = ConnectionHandler.getConnection(getServletContext());
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int albumId = Integer.parseInt(request.getParameter("album"));
		//int pageId = Integer.parseInt(request.getParameter("page"));
		
		HttpSession session = request.getSession();

		int userId = ((User) session.getAttribute("user")).getIdUser();

		ImageDAO imageDAO = new ImageDAO(connection);
		
		List<Image> albumImages = null;
		
		try {
			albumImages = imageDAO.findAllAlbumImages(albumId);
			
		} catch(SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to retrieve album images");
            return;
		}
        
        Gson gson = new GsonBuilder().setDateFormat("yyyy MM dd").create();
        String json = gson.toJson(albumImages);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
        
    }
	
	
	
	public boolean checkUserAlbums(int userId, int albumId) {
		AlbumDAO albumDAO = new AlbumDAO(connection);
		
		List<Album> sessionUserAlbum = null;
		
		
		try {
			sessionUserAlbum = albumDAO.findUserAlbums(userId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(sessionUserAlbum == null)
			return false;
		else {
			for(Album a : sessionUserAlbum) {
				if(a.getIdAlbum() == albumId)
					return true;
			}

		}
		
		return false;
		
		
	}
}
