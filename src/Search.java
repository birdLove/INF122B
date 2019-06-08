//Login page servlet
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
/**
 * Servlet implementation class Main
 */

@WebServlet(name = "Search", urlPatterns = "/form2")

public class Search extends HttpServlet {
	 public String getServletInfo() {
	        return "Servlet connects to MySQL database and displays result of a SELECT";
	    }

	    // Use http GET
	    public void doGet(HttpServletRequest request, HttpServletResponse response)
	            throws IOException {
	        String loginUser = "root";
	        String loginPasswd = "mydoggie";
	        String loginUrl = "jdbc:mysql://localhost:3306/fabflix";

	        response.setContentType("text/html");    // Response mime type

	        // Output stream to STDOUT
	        PrintWriter out = response.getWriter();
	       // response.getWriter().append("Served at: ").append(request.getContextPath());
	                
	        try {
	            Class.forName("com.mysql.jdbc.Driver").newInstance();

	            // Create a new connection to database
	            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

	            // Declare a new statement
	            Statement statement = connection.createStatement();
	            //String customerID = (String) request.getAttribute("customerID");         
	            
	            PreparedStatement myStatement = connection.prepareStatement("SELECT * FROM customers WHERE (id = ?);");
	    		myStatement.setString(1, Example.customerID);
	    		ResultSet resultset = myStatement.executeQuery();
	            out.println("<body>");
	            while(resultset.next())
	    		{
	            	 out.println(String.format("<h2>Still here %s %s</h2>", resultset.getString("firstname"), resultset.getString("lastname")));
	    		}	            
	            resultset.close();
        		out.println("<h1>Fabflix Movies</h1>");
        		out.println("<table border>");

        		// add table header row
        		out.println("<tr>");
        		out.println("<td>Id</td>");
        		out.println("<td>Title</td>");
        		out.println("<td>Year</td>");
        		out.println("<td>Director</td>");
        		out.println("<td>Rating</td>");
        		out.println("<td>List of Genres</td>");
        		out.println("<td>List of Stars</td>");
        		out.println("</tr>");
        		String newLine = System.getProperty("line.separator");
        		
        		// prepare query
                String searchTitle = request.getParameter("title");
                String theYear = request.getParameter("year");
                String searchDirector = request.getParameter("director");
                String searchStar = request.getParameter("star");
                
                String myQuery = "SELECT distinct movies.id, ratings.rating, title, year, movies.director FROM movies, stars_in_movies, stars, ratings WHERE (movies.id = stars_in_movies.movieId) AND (stars.id = stars_in_movies.starId) AND (movies.id = ratings.movieId)";      	
                if (searchTitle.substring(0,1) == "%")
                {
                	myQuery = myQuery + String.format(" AND (title LIKE \"%s%\")", searchTitle);
                }
                else if(searchTitle != "")
                {
                	myQuery = myQuery + String.format(" AND (title = \"%s\")", searchTitle);
                }
                
                if (theYear.substring(0,1) == "%")
                {
                	int searchYear = Integer.parseInt(theYear);
                	myQuery = myQuery + String.format(" AND (year Like %d%)", searchYear);                	
                }
                else if(theYear != "")
                {
                	int searchYear = Integer.parseInt(theYear);
                	myQuery = myQuery + String.format(" AND (year = %d)", searchYear);
                }
  
                if (searchDirector.substring(0,1) == "%")
                {
                	myQuery = myQuery + String.format(" AND (director LIKE \"%s%\")", searchDirector);
                }
                else if(searchDirector != "")
                {
                	myQuery = myQuery + String.format(" AND (director = \"%s\")", searchDirector);
                }
                if (searchStar.substring(0,1) == "%")
                {
                	myQuery = myQuery + String.format(" AND (stars.name = \"%s\")", searchStar);
                }
                else if(searchStar != "")
                {
                	myQuery = myQuery + String.format(" AND (stars.name LIKE \"%s%\")", searchStar);
                }
   
                myQuery = myQuery + " limit 100;";

                out.println("<p>" + myQuery + "</p>");
                
	            PreparedStatement theStatement = connection.prepareStatement(myQuery);
	           
	    		ResultSet resultSet = theStatement.executeQuery();
        		
        		// add a row for every star result
        		while (resultSet.next()) 
        		{
        			// get a star from result set
        			String id = resultSet.getString("movies.id");
        			String title = resultSet.getString("title");
        			String thisYear = resultSet.getString("year");
        			int year = Integer.parseInt(thisYear);
        			String director = resultSet.getString("director");
        			String rating = resultSet.getString("rating");

        			out.println("<tr>");
        			out.println("<td>" + id + "</td>");
        			out.println("<td><a href=\"singleMovie?id=" + id + "\">" + title + "</a></td>");
        			out.println("<td>" + year + "</td>");
        			out.println("<td>" + director + "</td>");
        			out.println("<td>" + rating + "</td>");
        			
        			PreparedStatement state = connection.prepareStatement("SELECT name FROM movies, genres_in_movies, genres WHERE (movies.id = genres_in_movies.movieId) AND (genres_in_movies.genreId = genres.id) AND (movieId = ?);");
            		state.setString(1, id);
            		ResultSet rez1 = state.executeQuery();
            		out.println("<td>");
            		while(rez1.next())
            		{
            			out.println(rez1.getString("name") + ", ");
            		}
            		out.println("</td>");
            		PreparedStatement state2 = connection.prepareStatement("SELECT stars.name, stars.id FROM movies, stars_in_movies, stars WHERE (movies.id = stars_in_movies.movieId) AND (stars_in_movies.starId = stars.id) AND (movieId = ?);");
            		state2.setString(1, id);
            		ResultSet rez2 = state2.executeQuery();
            		out.println("<td>");
            		while(rez2.next())
            		{
            			
            			out.println("<a href=\"singleStar?id=" + rez2.getString("id") + "\">" + rez2.getString("name") + "</a>, ");
            		}
            		out.println("</td>");
        			
        			out.println("</tr>");
        		}     		

        		out.println("</table>");
        		out.println("</body>");
        		resultSet.close();
        		
        		statement.close();
        		connection.close();
	        	} catch (Exception ex) {

	            // Output Error Massage to html
	            out.println(String.format("<html><head><title>Fabflix: Error</title></head>\n<body><p>SQL error in doGet: %s</p></body></html>", ex.getMessage()));
	            return;
	        }
	        
	        out.close();
	    }

}

