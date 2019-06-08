import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;
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

// server endpoint URL
@WebServlet("/HeroSuggestion")
public class HeroSuggestion extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/*
	 * populate the Marvel heros and DC heros hash map.
	 * Key is hero ID. Value is hero name.
	 */
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String loginUser = "root";
	    String loginPasswd = "mydoggie";
	    String loginUrl = "jdbc:mysql://localhost:3306/fabflix";
	  //  List<String> list = new ArrayList<String>();
		response.setContentType("application/json"); // Response mime type
		// Retrieve parameter id from url request.

		// Output stream to STDOUT
		PrintWriter out = response.getWriter();
	    // response.getWriter().append("Served at: ").append(request.getContextPath());
		

		try {
			 Class.forName("com.mysql.jdbc.Driver").newInstance();
	           // Create a new connection to database
	           Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
	           
				JsonArray jsonArray = new JsonArray();
				
	           String selMovie = request.getParameter("query");
				// return the empty json array if query is null or empty
				if (selMovie == null || selMovie.trim().isEmpty()) {
					response.getWriter().write(jsonArray.toString());
					return;
				}
			

			String query = "SELECT distinct movies.id, movies.title from movies, stars, stars_in_movies where movies.id = stars_in_movies.movieId AND stars_in_movies.starId = stars.id";
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet resultSet = statement.executeQuery();
			int index = 0;
			// Iterate through each row of rs
			while (resultSet.next() && index < 10) {
				String movieName = resultSet.getString("title");			
				String movieId = resultSet.getString("id");
				
				if (movieName.toLowerCase().contains(selMovie.toLowerCase()) && movieId!=null && movieId!="") {
					jsonArray.add(generateJsonObject(movieId, movieName));

					index++;
				}
				

			}
		
			response.getWriter().write(jsonArray.toString());
			return;

	}
	catch(Exception e)
	{
		System.out.println(e);
		response.sendError(500, e.getMessage());	
	}

	}
	
	
	private static JsonObject generateJsonObject(String movieId, String movieName) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("value", movieName);
		jsonObject.addProperty("movieId", movieId);
		
		JsonObject additionalDataJsonObject = new JsonObject();
		jsonObject.addProperty("category", "movie");
		jsonObject.addProperty("movieId", movieId);
		
		System.out.println("Movie Id = " + movieId);
		System.out.println("Movie NAMe = " + movieName);
		
		jsonObject.add("data", additionalDataJsonObject);
		return jsonObject;
	}
	
}
