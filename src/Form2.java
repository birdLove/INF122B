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

import javax.swing.JDialog;
import javax.swing.JOptionPane;

//Download org.jasypt
//import org.jasypt.util.password.*;
//import org.jasypt.util.text.*;
//import org.jasypt.util.password.StrongPasswordEncryptor;
import java.lang.Boolean;
/*
 * WEB SERVLET FOR FORM - THIS IS FOR THE LOGIN PAGE
 * THIS IS THE RESPONSE FROM THE LOGIN PAGE
 */
@WebServlet(name = "FormServlet2", urlPatterns = "/SendToDashboard")

public class Form2 extends HttpServlet {

    public String getServletInfo() {
        return "Servlet connects to MySQL database and displays result of a SELECT";
    }

    // Use http GET
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {


        // Output stream to STDOUT
        PrintWriter out = response.getWriter(); 
        response.getWriter().append("Served at: ").append(request.getContextPath());

        String loginUser = "root";
        String loginPasswd = "mydoggie";
        String loginUrl = "jdbc:mysql://localhost:3306/fabflix";

       
                
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            // Create a new connection to database
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

            // Declare a new statement
            Statement statement = connection.createStatement();
                        
            // Retrieve parameters from the form box
            String email = request.getParameter("empEmail");
            String password = request.getParameter("empPassword");
            
            // Generate a SQL query to get customer's id - NOT WORKING YET
            PreparedStatement myStatement = connection.prepareStatement("SELECT * FROM employees WHERE (email = ?);");
    		myStatement.setString(1, email);
    		ResultSet resultset = myStatement.executeQuery();
    		while(resultset.next())
    		{
    			String encryptedPassword = resultset.getString("password");
    				//If password is right, send it over, else go back to same page and print error
    				//Download org.jasypt
    				//success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
                    if(password.equals(encryptedPassword))
    			 	{
                    	String customerId = resultset.getString("fullname");                    	
                    	request.setAttribute("customerID", customerId);
                    	response.sendRedirect("_Dashboard.html");
    	            }
    	            else
    	            {
    	            	

    	            	final JDialog dialog = new JDialog();
    	            	dialog.setAlwaysOnTop(true);    
    	            	JOptionPane.showMessageDialog(dialog, "Error, wrong password");    	    		
    	            	response.sendRedirect("index2.html");
    	            }
    		}
    		if(!resultset.first())
    		{
            	final JDialog dialog = new JDialog();
            	dialog.setAlwaysOnTop(true);    
            	JOptionPane.showMessageDialog(dialog, "Error, user doesn't exist");    	    		
            	response.sendRedirect("index2.html");
    		}
           
            // Close all structures
            resultset.close();
            statement.close();
            connection.close();

        	} catch (Exception ex) {

            // Output Error Massage to html
            out.println(String.format("<html><head><title>Fabflix: Error</title></head>\n<body><p>SQL error in doGet: %s</p></body></html>", ex.getMessage()));
            ex.printStackTrace();
            return;
        }
        out.close();
    }
}