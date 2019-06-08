import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.RequestDispatcher;
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
import java.sql.Statement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Servlet implementation class - FOR THE MAIN PAGE OF THE DIRECTORY
 */
@WebServlet("/Main")

public class Main extends HttpServlet {
	 public String getServletInfo() {
	        return "Servlet connects to MySQL database and displays result of a SELECT";
	    }
	    // Use http GET
	    public void doPost(HttpServletRequest request, HttpServletResponse response)
	            throws IOException {
	        
	        response.setContentType("text/html");    // Response mime type

	        // Output stream to STDOUT
	        PrintWriter out = response.getWriter();
	       // response.getWriter().append("Served at: ").append(request.getContextPath());

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
	             // Declare a new statement
	             Statement statement = connection.createStatement();

	             Example.customerID = (String) request.getAttribute("customerID");         
	            
	            PreparedStatement myStatement = connection.prepareStatement("SELECT * FROM customers WHERE (id = ?);");
	    		myStatement.setString(1, Example.customerID);
	    		ResultSet resultset = myStatement.executeQuery();
	    		
	            while(resultset.next())
	    		{
	            	 out.println(String.format("<h2>Welcome %s %s</h2>", resultset.getString("firstname"), resultset.getString("lastname")));
	    		}
	            
	           
//	            out.println("<p>Options:</p>");
//	            
//	            //Links
//	            response.setContentType("text/html");
//	            PrintWriter out2 = response.getWriter();
//	            //Search page
//	            out2.println("<a href=\"search.html\">Search</a>");
//
//	            //Browse page
//	            out2.println("<a href=\"Browsing\">Browse</a>");
//	            
//	            //Logout page
//	            out2.println("<a href=\"index.html\">Logout</a>");
//	            
//	            //View shopping cart page
//	            out2.println("<a href=\"cart.html\">View Shopping Cart</a>");
//	            out2.close();
//	            
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
