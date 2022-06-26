package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.beans.Image;
import it.polimi.tiw.dao.ImageDAO;
import it.polimi.tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class ShowImage
 */
@WebServlet("/ShowImageDetails")
public class ShowImageDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	  private Connection connection = null;

    public ShowImageDetails() {
        super();
    }
    
    public void init() throws ServletException{
    	System.out.println("Entered showDetails");
    	connection = ConnectionHandler.getConnection(getServletContext());
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("init get ShowImageDetails");
		int imgID = Integer.parseInt(request.getParameter("image"));

		Image imgDetails = null;
		ImageDAO imageDAO = new ImageDAO(connection);
		
		try {
			imgDetails = imageDAO.getImageFromId(imgID);
			if(imgDetails==null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Image not found");
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		Gson gson = new GsonBuilder().setDateFormat("yyyy MM dd").create();
        String json = gson.toJson(imgDetails);
        System.out.println(json);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
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
