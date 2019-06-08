
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.mysql.jdbc.ResultSetMetaData;

/*
 * FABFLIX - USED FOR FIRST PROJECT ONLY, THIS JUST OUTPUTS TOP 20 MOVIES FOR THE SITE
 */
@WebServlet("/Fabflix")
public class Fabflix extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Fabflix() 
    {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		// TODO Auto-generated method stub - add in your own password/username
		String loginUser = "root";
        String loginPasswd = "mydoggie";
        String loginUrl = "jdbc:mysql://localhost:3306/fabflix";
        response.setContentType("text/html");
		response.getWriter().append("Served at: ").append(request.getContextPath());

        // get the printwriter for writing response
        PrintWriter out = response.getWriter();
        
        //Print out the header
        out.println("<html>");
        out.println("<head><title>Fabflix</title></head>");

        try 
        {
        		//Create instance
        		Class.forName("com.mysql.jdbc.Driver").newInstance();
        		// create database connection
        		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        		// declare statement
        		Statement statement = connection.createStatement();

        		out.println("<body>");
        		out.println("<h1>Fabflix Movies</h1>");
        		out.println("<table border>");

        		// add table header row
        		out.println("<tr>");
        		out.println("<td>Title</td>");
        		out.println("<td>Year</td>");
        		out.println("<td>Director</td>");
        		out.println("<td>Rating</td>");
        		out.println("<td>List of Genres</td>");
        		out.println("<td>List of Stars</td>");
        		out.println("</tr>");
        		//String newLine = System.getProperty("line.separator");
        		
        		// prepare query
        		String query = "SELECT movies.id, ratings.rating, title, year, movies.director FROM ratings, movies WHERE (movies.id = ratings.movieId) ORDER BY rating DESC limit 20;";
        		// execute query
        		ResultSet resultSet = statement.executeQuery(query);
        		
        		// add a row for every star result
        		while (resultSet.next()) 
        		{
        			// get a star from result set
        			String title = resultSet.getString("title");
        			String year = resultSet.getString("year");
        			String director = resultSet.getString("director");
        			String rating = resultSet.getString("rating");
        			String id = resultSet.getString("id");
        			
        			out.println("<tr>");
        			out.println("<td>" + title + "</td>");
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
            		PreparedStatement state2 = connection.prepareStatement("SELECT stars.name FROM movies, stars_in_movies, stars WHERE (movies.id = stars_in_movies.movieId) AND (stars_in_movies.starId = stars.id) AND (movieId = ?);");
            		state2.setString(1, id);
            		ResultSet rez2 = state2.executeQuery();
            		out.println("<td>");
            		while(rez2.next())
            		{
            			out.println(rez2.getString("name") + ", ");
            		}
            		out.println("</td>");
        			
        			out.println("</tr>");
        			
        			rez1.close();
        			rez2.close();
        		}     		

        		out.println("</table>");
        		out.println("</body>");
        		resultSet.close();
        		statement.close();
        		connection.close();
        } 
        catch (Exception e) 
        {

        		/*
        		 * After you deploy the WAR file through tomcat manager webpage,
        		 *   there's no console to see the print messages.
        		 * Tomcat append all the print messages to the file: tomcat_directory/logs/catalina.out
        		 * 
        		 * To view the last n lines (for example, 100 lines) of messages you can use:
        		 *   tail -100 catalina.out
        		 * This can help you debug your program after deploying it on AWS.
        		 */

        		e.printStackTrace();
        		out.println("<body>");
        		out.println("<p>");
        		out.println("Exception in doGet: " + e.getMessage());
        		out.println("</p>");
        		out.print("</body>");
        }
        
        out.println("</html>");
        out.close();
	}
	
}
