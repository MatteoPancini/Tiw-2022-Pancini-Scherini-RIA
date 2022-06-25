package it.polimi.tiw.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;


import it.polimi.tiw.beans.Image;
import it.polimi.tiw.dao.ImageDAO;
import it.polimi.tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class ChangeImagesOrder
 */
@WebServlet("/ChangeImagesOrder")
@MultipartConfig
public class ChangeImagesOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private Connection connection;
	private int albumId;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChangeImagesOrder() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.albumId = Integer.parseInt(request.getParameter("album"));
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String line = null;
		StringBuilder json = new StringBuilder();
		ImageDAO imageDAO = new ImageDAO(connection);
		
		Gson gson = new Gson();

		try {
			BufferedReader reader = request.getReader();
			while((line = reader.readLine())!=null){
				json.append(line);
			}
		}catch(Exception e) {
			System.out.println("Error reading JSON object");
			e.printStackTrace();
		}

		Map<String, Double> newOrder = new HashMap<String, Double>();
		newOrder = (Map<String, Double>) gson.fromJson(json.toString(), newOrder.getClass());
		
		int sizeArr = newOrder.size();
		System.out.println("Size arrays: " + sizeArr);
		
		String[] orderedClient = new String[sizeArr];
		
		System.out.println("Map json arriving from the client");
		newOrder.entrySet().forEach(x -> {
			System.out.println("id: "+x.getKey()+" position: "+ x.getValue());
			int value = x.getValue().intValue();
		
			orderedClient[value] = x.getKey();
			System.out.println("pos: " + value + " value:" + orderedClient[value]);
		});
		
		
		// Servono per trovare gli estremi degli album della pagina
		String minId = Collections.max(newOrder.keySet());
		String maxId = Collections.min(newOrder.keySet());
		int minId_int = Integer.parseInt(minId);
		System.out.println("minId_int = " + minId_int);


		List<Image> databaseOrder = new ArrayList<Image>();
		String[] orderedDatabase = new String[sizeArr];
		

		try {
			databaseOrder = imageDAO.getImagesPositionForOrdering(albumId, minId, maxId);
			System.out.println("Map arriving from the db");

			Collections.sort(databaseOrder);
			int k = 0;
			for(Image img : databaseOrder) {
				System.out.println("id: "+ img.getIdImage()+" order: "+ img.getOrder());
				orderedDatabase[k] = String.valueOf(img.getIdImage());
				k++;
			}
			
			for(int i = 0; i < sizeArr; i++) {
				System.out.println("Client: " + orderedClient[i] + " DB: " + orderedDatabase[i]);
			}
			
			for(int i = 0; i < sizeArr; i++) {
				if(!(orderedClient[i].equalsIgnoreCase(orderedDatabase[i]))) {
					System.out.println("Entro");
					for(int j=0; j < sizeArr; j++) {
						if(orderedClient[j].equalsIgnoreCase(orderedDatabase[i])) {
							System.out.println("SI");
							for(Image img : databaseOrder) {
								if(String.valueOf(img.getIdImage()).equalsIgnoreCase(orderedDatabase[j])) {
									System.out.println("Aggiorno " + orderedDatabase[i] + " con " + img.getOrder());
									try {
										imageDAO.updateOrder(Integer.valueOf(orderedDatabase[i]), img.getOrder());
									} catch(SQLException e) {
										e.printStackTrace();
									}
									break;
								}
							}
							break;	
						}
						System.out.println("NO");
					}	
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.setCharacterEncoding("UTF-8");
			return;
		}
		
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
	}

}


