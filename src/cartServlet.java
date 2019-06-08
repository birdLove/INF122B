import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import cs122b.Cart;

/**
 * Servlet implementation class cartServlet
 * THIS IS THE SERVLET FOR SHOPPING CART ACTUAL
 */
@WebServlet(name = "cartServlet", urlPatterns = "/api/cartServlet")
public class cartServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json"); // Response mime type
		// Retrieve parameter id from url request.

		// Output stream to STDOUT
		PrintWriter out = response.getWriter();
	  //     response.getWriter().append("Served at: ").append(request.getContextPath());

		try {
			//Create instance
       	 Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/fabflixWrite");

            // the following commented lines are direct connections without pooling
            //Class.forName("org.gjt.mm.mysql.Driver");
            //Class.forName("com.mysql.jdbc.Driver").newInstance();
            //Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
           

          
            if (ds == null)
                out.println("ds is null.");

            Connection connection = ds.getConnection();
            if (connection == null)
                out.println("connection is null.");
   
			
	            JsonArray jsonArray = new JsonArray();
	            for (int index=0; index < ShoppingCart.list.size(); index++) { 
	            String myQuery3 = "Select title FROM movies WHERE " + String.format("(id = \"%s\")", ShoppingCart.list.get(index).movieId);
	        	PreparedStatement theStatement3 = connection.prepareStatement(myQuery3);
	        	ResultSet resultSet3 = theStatement3.executeQuery();
	        	String title3 = null;
	        		 while(resultSet3.next()) {
	        				 title3 = resultSet3.getString("title");
	        		 }
	        		// Create a JsonObject based on the data we retrieve from rs
	 				JsonObject jsonObject = new JsonObject();
	 				if(ShoppingCart.list.get(index).customerId == Example.customerID)
	 				{
	 					jsonObject.addProperty("title", title3);
	 					jsonObject.addProperty("customerId2", ShoppingCart.list.get(index).customerId);
	 					jsonObject.addProperty("movieId", ShoppingCart.list.get(index).movieId);
	 					jsonObject.addProperty("saleDate", ShoppingCart.list.get(index).saleDate.toString());
	 					jsonObject.addProperty("numItem", ShoppingCart.list.get(index).numItem);	
	 					jsonArray.add(jsonObject);
	 					System.out.println("hi");
	 				}
	           }  
            String myQuery = "Select * FROM sales WHERE " + String.format("(customerId = \"%s\")", Example.customerID);
            PreparedStatement theStatement = connection.prepareStatement(myQuery);
            ResultSet resultSet = theStatement.executeQuery();			
			JsonArray jsonArray2 = new JsonArray();

			// Iterate through each row of rs
			while (resultSet.next()) {
				
				String customerId2 = resultSet.getString("customerId");
	        	String movieId2 = resultSet.getString("movieId");
	        	String saleDate = resultSet.getString("saleDate").toString();

	       		 String myQuery2 = "Select title FROM movies WHERE " + String.format("(id = \"%s\")", movieId2);
				 PreparedStatement theStatement2 = connection.prepareStatement(myQuery2);
				 ResultSet resultSet2 = theStatement2.executeQuery();
				 String title2 = "";
				 while(resultSet2.next()) { 
					 title2 = resultSet2.getString("title");
				  } 

				// Create a JsonObject based on the data we retrieve from rs
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("customerId", customerId2);
				jsonObject.addProperty("titleSale", title2);
				jsonObject.addProperty("movieIdSale", movieId2);
				jsonObject.addProperty("saleDateSale", saleDate);
				jsonArray2.add(jsonObject);

			}
			JsonObject largeObject = new JsonObject();
			largeObject.add("cart", jsonArray);
			largeObject.add("sales", jsonArray2);
			JsonArray large = new JsonArray();
			large.add(largeObject);
	           // write JSON string to output
            out.write(large.toString());
            
            // set response status to 200 (OK)
            response.setStatus(200);
            resultSet.close();
			theStatement.close();
			connection.close();

		} catch (Exception e) {

			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
			// set response status to 500 (Internal Server Error)
			response.setStatus(500);
		}
		out.close();

	}

}

