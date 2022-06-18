package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.CommentDAO;
import it.polimi.tiw.utils.ConnectionHandler;


@WebServlet("/PostComment")
@MultipartConfig

public class PostComment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;


    public PostComment() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
        int userId = ((User) session.getAttribute("user")).getIdUser();
        
        
        String comment = null;
        String finalComment = null;
        int imgID = 0;
		boolean badRequest = false;

        
        try {
        	imgID = Integer.parseInt(request.getParameter("image"));
        	comment = StringEscapeUtils.escapeJava(request.getParameter("comment"));
        	// regex pattern to replace \r\n in comments
        	comment = comment.replaceAll("(\\\\r\\\\n|\\\\n)", "\\\n");
        	finalComment = comment.replaceAll("\\\n", "<br />");
        	if(comment.equals("")) badRequest = true;
        	
        	System.out.println("COMMENTO PRESO DAL FORM : "+finalComment);
        }catch(NullPointerException e) {
        	System.out.println("COMMENTO PRESO DAL FORM : "+comment);
        	badRequest = true;
        }
        
        if(badRequest == true) {
        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Missing or incorrect parameters");
			return;
		}
        
        CommentDAO commentDAO = new CommentDAO(connection);
        try {
			commentDAO.createNewComment(imgID, userId, finalComment);
			System.out.println(finalComment);
		} catch (SQLException e) {
			e.printStackTrace();
		}
        
        response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(comment);
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
