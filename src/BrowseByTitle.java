

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import java.util.*;
/**
 * Servlet implementation class BrowseByTitle
 */
@WebServlet("/BrowseByTitle")
public class BrowseByTitle extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BrowseByTitle() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

        response.setContentType("text/html");
		response.getWriter().append("Served at: ").append(request.getContextPath());

        // get the printwriter for writing response
        PrintWriter out = response.getWriter();
        
        //Print out the header
        out.println("<html>");
        out.println("<head><title>BrowsingByGenre</title></head>");
        ArrayList<String> letters = new ArrayList<String>();
        try 
        {
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
        		out.println("<h1 align = \\\"center\\\">Browsing By Title</h1>");


        			// prepare query
            		String query = "SELECT movies.title from movies;";
            		// execute query
            		ResultSet resultSet = statement.executeQuery(query);
            		
            		// add a row for every star result
            		while (resultSet.next()) 
            		{
            			String genre = resultSet.getString("title");
            			if(!letters.contains(genre.substring(0,1).toUpperCase()))
            			{
            				letters.add(genre.substring(0,1).toUpperCase());
            			}
            		}     		
            		Collections.sort(letters);
            		for(String counter: letters)
            		{
            			if(counter.equals("?"))
            			{
            				break;
            			}
        				out.print("<a href= \"SearchFromBrowse1?menu="+counter+ "\">" + counter +"</a>  ");

            		}
            		out.println("</body>");
            		resultSet.close();


        		
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
		out.println("</table>");
		out.println("</body>");
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
