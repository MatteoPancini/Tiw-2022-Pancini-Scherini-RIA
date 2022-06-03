package it.polimi.tiw.controllers;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/CreateAccount")
@MultipartConfig

public class CreateAccount extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
       
    public CreateAccount() {
        super();
        // TODO Auto-generated constructor stub
    }
    
	public void init() throws ServletException{
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		String email = null;
		String username = null;
		String password = null;
		String confirmedPassword = null;
		boolean badRequest = false;
		try {
			email = StringEscapeUtils.escapeJava(request.getParameter("email"));
			username = StringEscapeUtils.escapeJava(request.getParameter("username"));
			password = StringEscapeUtils.escapeJava(request.getParameter("pwd"));
			confirmedPassword = StringEscapeUtils.escapeJava(request.getParameter("confirmed_pwd"));
			
			System.out.println("New user email: "+email+" username: "+username+" isPswEquals: "+password.equals(confirmedPassword));
			
			if(email == null || username == null || password == null || confirmedPassword == null) {
				badRequest = true;
			}
			} catch(NullPointerException e) {
				badRequest = true;
			}
		
		if(badRequest == true) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Credentials must be not null");
			return;
		}
		
		System.out.println("New user email: "+email+" username: "+username+" isPswEquals: "+password.equals(confirmedPassword));
	
		boolean invalidUser = false;
				
		UserDAO userDAO = new UserDAO(connection);
		try {
			List<String> usernameList = userDAO.findAllUsernames();
			
			// First check if the password and confirmed password are equals
			if(!password.equals(confirmedPassword)) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Password fields don't match");
				invalidUser = true;
			} else if(usernameList.contains(username)) { // Second check if username is not in the DB
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Username already in use");
				invalidUser = true;

			} else if(!mailSyntaxCheck(email)) { // Third check email validity structure
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Email pattern does not exists");
				invalidUser = true;

			}
			
			if(!invalidUser) {
			
				// Fourth create new User
				userDAO.createUser(email, username, password);
				
				int newUserId = userDAO.getIdFromUsername(username);
				
				//provo a creare una cartella in img
				if(newUserId != 0) {
					String folderPath = getServletContext().getInitParameter("folderPath");
					File file = new File(folderPath + newUserId);
					boolean bool = file.mkdir();
				      if(bool){
				         System.out.println("Directory created successfully");
				      }else{
				         System.out.println("Sorry couldn�t create specified directory");
				      }
				}
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().print(newUserId);
				
			}
			
			
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Error in creating the product in the database");
			return;
		}
		
	}
	
	@Override
	public void destroy() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e){
				System.err.println(e.getMessage());
			}
		}
	}
	
	private boolean mailSyntaxCheck(String email)
	   {
	        // Create the Pattern using the regex
	        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
	 
	        // Match the given string with the pattern
	        Matcher m = p.matcher(email);
	 
	        // check whether match is found
	        boolean matchFound = m.matches();
	 
	        StringTokenizer st = new StringTokenizer(email, ".");
	        String lastToken = null;
	        while (st.hasMoreTokens()) {
	            lastToken = st.nextToken();
	        }
	 
	    // validate the country code
	        if (matchFound && lastToken.length() >= 2
	                && email.length() - 1 != lastToken.length()) {
	 
	            return true;
	        } else {
	            return false;
	        }
	 
	    }

}
