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
		
		System.out.println("Map json arriving from the client");
		newOrder.entrySet().forEach(x -> System.out.println("id: "+x.getKey()+" position: "+x.getValue()));
		
		// Servono per trovare gli estremi degli album della pagina
		String minId = Collections.max(newOrder.keySet());
		String maxId = Collections.min(newOrder.keySet());

		List<Image> databaseOrder = new ArrayList<Image>();
		try {
			databaseOrder = imageDAO.getImagesPositionForOrdering(albumId, minId, maxId);
			System.out.println("Map arriving from the db");
			databaseOrder.forEach(x -> System.out.println("id: "+x.getIdImage()+" order: "+x.getOrder()));
			
			// FIX: Qui lo swap non funziona nel modo corretto, sopra in teoria è tutto ok
			for(int i=newOrder.size()-1; i>=0;i--) {
				if(!compare(newOrder, databaseOrder, i)) {
					System.out.println("Id da aggiornare: "+databaseOrder.get(i).getIdImage()+ " | nuovo orderNum: "+getIdFromPos(newOrder, (double) i));
					imageDAO.updateOrder(databaseOrder.get(i).getIdImage(), getIdFromPos(newOrder, (double) i));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean compare(Map<String, Double> newOrder, List<Image> databaseOrder, int i) {
			if(databaseOrder.get(i).getIdImage() == getIdFromPos(newOrder, (double) i)) {
				return true;
			}
			return false;
	}
	
	/*
	 * Il metodo ritorna la chiave corrispondente alla posizione nell'oggetto json 
	 * che arriva dal client
	 */
	private int getIdFromPos(Map<String, Double> newOrder, Double pos) {
		int id = Integer.parseInt(newOrder.entrySet().stream()
				.filter(x -> pos.equals(x.getValue()))
				.map(Map.Entry::getKey)
				.findFirst().get());
		return id;
	}

}


