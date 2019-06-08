import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
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
import java.lang.Boolean;
import org.jasypt.util.password.*;
import org.jasypt.util.text.*;
import org.jasypt.util.password.StrongPasswordEncryptor;
/**
 * Servlet implementation class showTables
 */
@WebServlet("/showTables")
public class showTables extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public showTables() {
        // TODO Auto-generated constructor stub
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
        out.println("<head><title>Tables</title></head>");

        try 
        {
        		//Create instance
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            // Create a new connection to database
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

            // Declare a new statement
            Statement statement = connection.createStatement();
        		
        		
        		out.println("<body>");
        		out.println("<h1>MetaData</h1>");
        		out.println("<table border>");
        		
        		// add table header row
        		out.println("<tr>");
        		out.println("<td>Field</td>");
        		out.println("<td>Type</td>");
        		out.println("<td>Null</td>");
        		out.println("<td>Key</td>");
        		out.println("<td>Default</td>");
        		out.println("<td>Extra</td>");
        		out.println("</tr>");
        		//String newLine = System.getProperty("line.separator");
        		
        		// prepare query
        		String table = request.getParameter("table");
        		
        		String query = "Show columns from " + table + ";";
        		ResultSet resultSet = statement.executeQuery(query);
        		// add a row for every star result
        		while (resultSet.next()) 
        		{
        			// get a star from result set
        			String field = resultSet.getString("Field");
        			String type = resultSet.getString("Type");
        			String isnull = resultSet.getString("Null");
        			String key = resultSet.getString("Key");
        			String dfault = resultSet.getString("Default");
        			String extra = resultSet.getString("Extra");        			
        			
        			out.println("<tr>");
        			out.println("<td>" + field + "</td>");
        			out.println("<td>" + type + "</td>");
        			out.println("<td>" + isnull + "</td>");
        			out.println("<td>" + key + "</td>");
        			out.println("<td>" + dfault + "</td>");
        			out.println("<td>" + extra + "</td>");

            		out.println("</td>");
        			
        			out.println("</tr>");
        			

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
	


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
