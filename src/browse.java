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
 * Servlet implementation class BROWSE.JAVA
 * NOT IMPLEMENTED YET
 */
@WebServlet(name = "browse", urlPatterns = "/browse")

public class browse extends HttpServlet {
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
	            String customerID = (String) request.getAttribute("customerID");         
	            
	            PreparedStatement myStatement = connection.prepareStatement("SELECT * FROM customers WHERE (id = ?);");
	    		myStatement.setString(1, customerID);
	    		ResultSet resultset = myStatement.executeQuery();
	    		
	            while(resultset.next())
	    		{
	            	 out.println(String.format("<h2>Welcome %s %s</h2>", resultset.getString("firstname"), resultset.getString("lastname")));
	    		}
	            
	            resultset.close();
	            
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