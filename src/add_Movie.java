import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class add_Movie
 */
@WebServlet("/add_Movie")
public class add_Movie extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public add_Movie() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//HttpSession session = request.getSession(true); 
		PrintWriter out = response.getWriter();
		 try {
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

    	//Get variables from form
    	String movieID = request.getParameter("movieID");
    	String title = request.getParameter("title");
    	String year = request.getParameter("year");
    	String director = request.getParameter("director");	
    	String star = request.getParameter("star");
    	String genre = request.getParameter("genre");
    	String query1 = "Select * from movies;";
    	ResultSet resultSet = statement.executeQuery(query1);
    	
    	String query = "";
    	boolean bool = resultSet.first();

		if(movieID == null && title == null && star != null)
		{
			response.setContentType("text/html");  
    		out.println("<script type=\"text/javascript\">");  
    		out.println("alert('Movie Not Added');");  
    		out.println("</script>");
		}
		resultSet.close();
		statement.close();
    	if(bool)
    	{
    		try(CallableStatement myStoredProcedureCall = connection.prepareCall("{call fabflix.HELP(?, ?, ?, ?, ?, ?)};"))
    		{
    			myStoredProcedureCall.setString("ID2", movieID);
    			myStoredProcedureCall.setString("title2", title);
    			myStoredProcedureCall.setString("year2", year);
    			myStoredProcedureCall.setString("director2", director);
    			myStoredProcedureCall.setString("name2", star);
    			myStoredProcedureCall.setString("genre2", genre);
    			int i = myStoredProcedureCall.executeUpdate();
    			if(i >= 1)
    			{
    				System.out.println("This should work!!!");    				
    			}
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    		
//    		try(CallableStatement myStoredProcedureCall = connection.prepareCall("{call fabflix.add_movie(?,?,?,?,?,?)};"))
//    		{
//    			myStoredProcedureCall.setString("ID2", movieID);
//    			myStoredProcedureCall.setString("title2", title);
//    			myStoredProcedureCall.setString("year2", year);
//    			myStoredProcedureCall.setString("director2", director);
//    			myStoredProcedureCall.setString("name2", star);
//    			myStoredProcedureCall.setString("genre2", genre);
//    			System.out.println("Statement call = " + myStoredProcedureCall.toString());
//    			boolean b = myStoredProcedureCall.execute();
//    			
//    		}
//    		catch(Exception e)
//    		{
//    			e.printStackTrace();
//    		}

    		response.setContentType("text/html");  
    		out.println("<script type=\"text/javascript\">");  
    		out.println("alert('Movie Added');");  
    		out.println("</script>");
    	}
    	else
    	{
    		response.setContentType("text/html");  
    		out.println("<script type=\"text/javascript\">");  
    		out.println("alert('Movie Not Added');");  
    		out.println("</script>");
    	}
    	//resultSet.close();
		statement.close();
    	response.sendRedirect("_Dashboard.html");
    	
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
