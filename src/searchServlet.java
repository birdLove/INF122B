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
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.File;
import java.io.FileWriter;

import javax.annotation.Resource;
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

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/singleStar"
@WebServlet(name = "searchServlet", urlPatterns = "/api/searchServlet")
public class searchServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		int TS = 0;
//    	String contextPath = getServletContext().getRealPath("/");
//
//    	String xmlFilePath=contextPath + "//TS" +TS;
//    	File myfile1 = new File(xmlFilePath);		
//
//    	if(!myfile1.exists())
//    	{
//    		myfile1.createNewFile();
//    	}
//    	else
//    	{
//    	while(myfile1.exists())
//		{
//    		++TS;
//    		myfile1 = new File(contextPath + "//TS" +TS);
//    		
//    		if(!myfile1.exists())
//        	{
//        		myfile1.createNewFile();
//        		xmlFilePath=contextPath + "//TS" +TS;
//        	}
//    		
//		}
//    	}
//    	String tsfile = xmlFilePath;
//    	//BufferedWriter writer = new BufferedWriter(new FileWriter("/home/ubuntu/tomcat/webapps/Project12/TS"));
//        //writer.write(xmlFilePath);
//         
//        //writer.close();
//    	
		int JS = 0;
//    	    	
//    	
//		
//    	contextPath = getServletContext().getRealPath("/");
//        	
//    	xmlFilePath=contextPath+"/JS" +JS;
//    	File myfile2 = new File(xmlFilePath);		
//    	if(!myfile2.exists())
//    	{
//    		myfile2.createNewFile();
//    	}
//    	else
//    	{
//    	while(myfile2.exists())
//		{
//    		++JS;
//    		myfile2 = new File(contextPath + "//JS" +JS);
//    		
//    		if(!myfile2.exists())
//        	{
//        		myfile2.createNewFile();
//        		xmlFilePath=contextPath + "//JS" +JS;
//        	}
//    			    	
//		}    	
//    	}
//    	String jsfile = xmlFilePath;
//		
		long startTime = System.nanoTime();
		
		 String loginUser = "root";
	     String loginPasswd = "mydoggie";
	     String loginUrl = "jdbc:mysql://localhost:3306/fabflix";

		response.setContentType("application/json"); // Response mime type
		// Retrieve parameter id from url request.

		// Output stream to STDOUT
		PrintWriter out = response.getWriter();
	  //     response.getWriter().append("Served at: ").append(request.getContextPath());

		try {
			 Class.forName("com.mysql.jdbc.Driver").newInstance();
	            // Create a new connection to database
	            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			
	            String searchTitle = request.getParameter("title");
		    	String theYear = request.getParameter("year");
		    	String searchDirector = request.getParameter("director");
		    	String searchStar = request.getParameter("star");
		    	String myQuery;
		    	String toggleTitle = request.getParameter("toggleTitle");
		    	String toggleRating = request.getParameter("toggleRating");
		    	PreparedStatement theStatement;
		    	ResultSet resultSet;
		    	
		    	System.out.println("Hi i'm here~~");
		    	long startTime1 = System.nanoTime();

		    	myQuery = "SELECT distinct movies.id, ratings.rating, title, year, movies.director FROM stars_in_movies, stars, movies left join ratings on movies.id = ratings.movieId WHERE (movies.id = stars_in_movies.movieId) AND (stars.id = stars_in_movies.starId)";      	
		    	if (searchTitle != "" && searchTitle != null)
		    	{
		    		myQuery = myQuery + String.format(" AND (title LIKE \"%s%%\")", searchTitle);
		    	}

		    	if (theYear != "" && theYear != null)
		    	{
		    		int searchYear = Integer.parseInt(theYear);
		    		myQuery = myQuery + String.format(" AND (year = %d)", searchYear);
		    	}

		    	if (searchDirector != "" && searchDirector != null)
		    	{
		    		myQuery = myQuery + String.format(" AND (director LIKE \"%s%%\")", searchDirector);
		    	}

		    	if (searchStar != "" && searchStar != null)
		    	{
		    		myQuery = myQuery + String.format(" AND (stars.name LIKE \"%s%%\")", searchStar);
		    	}
		    	
		    	
//		    	String toggleRequest = request.getParameter("id");
//		    	if(toggleRequest.equals("toggleTitleA"))
//		    	{
//		    		myQuery = myQuery + "Order by title ASC";
//		    		toggleTitle = "toggleTitleB";
//		    	}
//		    	else if(toggleRequest.equals("toggleRatingA"))
//		    	{
//		    		myQuery = myQuery + "Order by ratings.rating ASC";
//		    		toggleRating = "toggleRatingB";
//		    	}
//		    	else if(toggleRequest.equals("toggleRatingB"))
//		    	{
//		    		myQuery = myQuery + "Order by ratings.rating DESC";
//		    		toggleRating = "toggleRatingA";
//		    	}
//		    	else if(toggleRequest.equals("toggleTitleB"))
//		    	{
//		    		myQuery = myQuery + "Order by title DESC";
//		    		toggleTitle = "toggleTitleA";
//		    	}
//		    	else
//		    	{
//		    		//Else don't do anything
//		    		toggleTitle = "toggleTitleA";
//		    		toggleRating = "toggleRatingA";
//		    		toggleRequest= "none";
//		    	}
//
//		    	int numResults = Integer.parseInt(request.getParameter("num"));
//		    	if(numResults == 10)
//		    	{
//		    		myQuery = myQuery + " limit 10";
//		    	}
//		    	else if(numResults == 20)
//		    	{
//		    		myQuery = myQuery + " limit 20";	
//		    	}
//		    	else if(numResults == 50)
//		    	{
//		    		myQuery = myQuery + " limit 50";
//		    	}
//		    	else if(numResults == 100)
//		    	{
//		    		myQuery = myQuery + " limit 100";	
//		    	}
//		    	else
//		    	{
//		    		myQuery = myQuery + " limit 100";
//		    	}
//				if (request.getParameter("offset") != null)
//				{
//		    	myQuery = myQuery + " offset "+ request.getParameter("offset");
//				}
		    	myQuery = myQuery + ";";
		    	 theStatement = connection.prepareStatement(myQuery);
		    	 resultSet = theStatement.executeQuery();
		    	 System.out.println("Hi");
		    	 
		    	 
			JsonArray jsonArray = new JsonArray();
			long endTime1 = System.nanoTime();
			long elapsedTime1 = endTime1 - startTime1; 
			
			
		//	BufferedWriter writer = new BufferedWriter(new FileWriter(jsfile,true));
		 //   writer.write(elapsedTime1 + ",");
		     
		//    writer.close();
			
			// Iterate through each row of rs
			while (resultSet.next()) {
				String id = resultSet.getString("movies.id");
		        String title = resultSet.getString("title");
		        String thisYear = resultSet.getString("year");
		        int year = Integer.parseInt(thisYear);
		        String director = resultSet.getString("director");
		        String rating = resultSet.getString("rating");
		       
   			System.out.println("Title: " + resultSet.getString("title"));
	    		List<String> starList;
	    		starList = new ArrayList<String>();
	    		
	    		List<String> starIdList;
	    		starIdList = new ArrayList<String>();
   		
	    		List<String> genreList;
	    		genreList = new ArrayList<String>();

   			PreparedStatement state = connection.prepareStatement("SELECT name FROM movies, genres_in_movies, genres WHERE (movies.id = genres_in_movies.movieId) AND (genres_in_movies.genreId = genres.id) AND (movieId = ?);");
	    		state.setString(1, id);

	    		ResultSet rez1 = state.executeQuery();	
	    		while(rez1.next()) { 
		    		genreList.add(rez1.getString("name"));
	     		 } 
	    		
	    		PreparedStatement state2 = connection.prepareStatement("SELECT stars.name, stars.id FROM movies, stars_in_movies, stars WHERE (movies.id = stars_in_movies.movieId) AND (stars_in_movies.starId = stars.id) AND (movieId = ?);");
	    		state2.setString(1, id);
	    		ResultSet rez2 = state2.executeQuery();  
	    	     while(rez2.next()) { 
	 	    		starList.add(rez2.getString("name"));
	 	    		starIdList.add(rez2.getString("id"));
	    	    //	String myString = "<a href=\"singleStar.html?id=" + rez2.getString("id") + "\">" + rez2.getString("name") + "</a>, ";
	     		 } 
				// Create a JsonObject based on the data we retrieve from rs
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("movieId", id);
				jsonObject.addProperty("title", title);
				jsonObject.addProperty("year", year);
				jsonObject.addProperty("director", director);
				jsonObject.addProperty("rating", rating);
				jsonObject.addProperty("starList", starList.toString());
				jsonObject.addProperty("starIdList", starIdList.toString());
				jsonObject.addProperty("genreList", genreList.toString());
				System.out.println("In the loop");
				jsonArray.add(jsonObject);

			}
			System.out.println("hi");
           // write JSON string to output
           out.write(jsonArray.toString());
           
           // set response status to 200 (OK)
           response.setStatus(200);
			resultSet.close();
			theStatement.close();
			connection.close();
			
			long endTime = System.nanoTime();
			long elapsedTime = endTime - startTime;
//			BufferedWriter writer1 = new BufferedWriter(new FileWriter(tsfile,true));
//		    writer1.write(elapsedTime + ",");
//		     
//		    writer1.close();
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