import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

/**
 * Servlet implementation class Browsing
 */
@WebServlet("/Browsing")
public class Browsing extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Browsing() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub - add in your own password/username
		
        response.setContentType("text/html");
		response.getWriter().append("Served at: ").append(request.getContextPath());

        // get the printwriter for writing response
        PrintWriter out = response.getWriter();
        
        //Print out the header
        out.println("<html>");
        out.println("<head><title>Browsing</title></head>");

        try 
        {
        		//Create instance
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
            // Declare a new statement
            Statement statement = connection.createStatement();

        		out.println("<body>");
        		out.println("<h1 align = \\\"center\\\">Browsing</h1>");


        		out.print("<font size=\"5\" color=\"black\"> <p style = \"text-align:left;\"> Browse by Genre </p> <p style = \"text-align:center;\">Browse by Title </p></div></font>");
        		
        		out.print("<a href=\"BrowseByGenre?genre=default\">click here to sort by genre</a>");
        		out.print("<div align=\"center\"> <a href=\"BrowseByTitle?menu=default\">click here to sort by title</a> </div>");

        		//String newLine = System.getProperty("line.separator");
        		
        		// prepare query
        		String query = "SELECT movies.id, ratings.rating, title, year, movies.director FROM ratings, movies WHERE (movies.id = ratings.movieId) ORDER BY rating DESC limit 20;";
        		// execute query
        		ResultSet resultSet = statement.executeQuery(query);
        		
        		// add a row for every star result
        		while (resultSet.next()) 
        		{
        			// get a star from result set

        			String id = resultSet.getString("id");
 
        			
        			PreparedStatement state = connection.prepareStatement("SELECT name FROM movies, genres_in_movies, genres WHERE (movies.id = genres_in_movies.movieId) AND (genres_in_movies.genreId = genres.id) AND (movieId = ?);");
            		state.setString(1, id);
            		ResultSet rez1 = state.executeQuery();

            		PreparedStatement state2 = connection.prepareStatement("SELECT stars.name FROM movies, stars_in_movies, stars WHERE (movies.id = stars_in_movies.movieId) AND (stars_in_movies.starId = stars.id) AND (movieId = ?);");
            		state2.setString(1, id);
            		ResultSet rez2 = state2.executeQuery();
            		
        			
        			rez1.close();
        			rez2.close();
        		}     		
        		out.println("</table>");
        		 HttpSession session = request.getSession(true);
                 String heading;
                 
                 Integer accessCount = (Integer)session.getAttribute("accessCount");

                 if (accessCount == null) {
                   accessCount = new Integer(0);
                   heading = "Welcome, Newcomer";
                 } else {
                   heading = "Welcome Back";
                   accessCount = new Integer(accessCount.intValue() + 1);
                 }


                 session.setAttribute("accessCount", accessCount); 
                 out.println("</H1>\n" +
                             "<H2>Information on Your Session:</H2>\n" +
                             "<TABLE BORDER=1 ALIGN=\"CENTER\">\n" +
                             "<TR BGCOLOR=\"#FFAD00\">\n" +
             		"  <TH>Info Type<TH>Value\n" +
                             "<TR>\n" +
                             "  <TD>ID\n" +
                             "  <TD>" + session.getId() + "\n" +
                             "<TR>\n" +
                             "  <TD>Creation Time\n" +
                             "  <TD>" +
                             new Date(session.getCreationTime()) + "\n" +
                             "<TR>\n" +
                             "  <TD>Time of Last Access\n" +
                             "  <TD>" +
                             new Date(session.getLastAccessedTime()) + "\n" +
                             "<TR>\n" +
                             "  <TD>Number of Previous Accesses\n" +
                             "  <TD>" + accessCount + "\n" +
                             "</TR>"+                
             		"</TABLE>\n");

                 // the following two statements show how to retrieve parameters in
                 // the request.  The URL format is something like:
                 //http://localhost:8080/project2/servlet/ShowSession?myname=Chen%20Li


                 out.println("</BODY></HTML>");

        
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
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
