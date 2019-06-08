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

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/singleStar"
@WebServlet(name = "singleMovie", urlPatterns = "/api/singleMovie")
public class singleMovie extends HttpServlet {
	private static final long serialVersionUID = 2L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

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
            DataSource ds = (DataSource) envCtx.lookup("jdbc/fabflix");

            // the following commented lines are direct connections without pooling
            //Class.forName("org.gjt.mm.mysql.Driver");
            //Class.forName("com.mysql.jdbc.Driver").newInstance();
            //Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
           

          
            if (ds == null)
                out.println("ds is null.");

            Connection connection = ds.getConnection();
            if (connection == null)
                out.println("connection is null.");
  

			
	            String id = request.getParameter("id");

			// Declare our statement
			PreparedStatement statement = connection.prepareStatement("SELECT movies.id, ratings.rating, title, year, movies.director, stars.id, stars.name FROM stars_in_movies, stars, movies left join ratings on movies.id = ratings.movieId WHERE (movies.id = stars_in_movies.movieId) AND (stars.id = stars_in_movies.starId) AND (movies.id = ?);");

			// Set the parameter represented by "?" in the query to the id we get from url,
			// num 1 indicates the first "?" in the query
			statement.setString(1, id);

			// Perform the query
			ResultSet resultSet = statement.executeQuery();
			JsonArray jsonArray = new JsonArray();

			// Iterate through each row of rs
			while (resultSet.next()) {
				
     			String movieId = resultSet.getString("movies.id");
     			String title = resultSet.getString("title");
    			System.out.println("Title: " + resultSet.getString("title"));
     			String thisYear = resultSet.getString("year");
     			int year = Integer.parseInt(thisYear);
				String starId = resultSet.getString("stars.id");
				String starName = resultSet.getString("name");
     			String director = resultSet.getString("director");
     			String rating = resultSet.getString("rating");

				// Create a JsonObject based on the data we retrieve from rs
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("movieId", movieId);
				jsonObject.addProperty("title", title);
				jsonObject.addProperty("year", year);
				jsonObject.addProperty("starId", starId);
				jsonObject.addProperty("starName", starName);
				jsonObject.addProperty("director", director);
				jsonObject.addProperty("rating", rating);

		jsonArray.add(jsonObject);

			}
			System.out.println("hi");
            // write JSON string to output
            out.write(jsonArray.toString());
            
            // set response status to 200 (OK)
            response.setStatus(200);
			resultSet.close();
			statement.close();
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