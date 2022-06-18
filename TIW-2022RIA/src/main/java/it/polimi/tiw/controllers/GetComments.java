package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.beans.Comment;
import it.polimi.tiw.dao.CommentDAO;
import it.polimi.tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class ShowImage
 */
@WebServlet("/GetComments")
public class GetComments extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	  private Connection connection = null;

    
    public void init() throws ServletException{
    	connection = ConnectionHandler.getConnection(getServletContext());
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("init get comments");
		int imgID = Integer.parseInt(request.getParameter("image"));

		List<Comment> comments = new ArrayList<>();
		CommentDAO commentDAO = new CommentDAO(connection);
		
		try {
			comments.addAll(commentDAO.findAllComments(imgID));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		Gson gson = new GsonBuilder().setDateFormat("yyyy MM dd").create();
        String json = gson.toJson(comments);
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
