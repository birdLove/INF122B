import javax.servlet.RequestDispatcher;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.File;

import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.lang.Boolean;
import org.jasypt.util.password.*;
import org.jasypt.util.text.*;
import javax.swing.JOptionPane;
import org.jasypt.util.password.StrongPasswordEncryptor;
/*
 * Login page to Fabflix
 */

@WebServlet(name = "FormServlet", urlPatterns = "/form")
public class Form extends HttpServlet {

    public String getServletInfo() {
        return "Servlet connects to MySQL database and displays result of a SELECT";
    }

    //Using post, get the response back from the index page
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
    	String contextPath = getServletContext().getRealPath("/");

    	String xmlFilePath=contextPath + "//TS";
    	File myfile = new File(xmlFilePath);		

    	if(!myfile.exists())
		{
	    	myfile.createNewFile();
		}
    	//BufferedWriter writer = new BufferedWriter(new FileWriter("/home/ubuntu/tomcat/webapps/Project12/TS"));
        //writer.write(xmlFilePath);
         
        //writer.close();
    	myfile.createNewFile();
    	
    	contextPath = getServletContext().getRealPath("/");
        	
    	xmlFilePath=contextPath+"/JS";
    	
    	myfile = new File(xmlFilePath);
    	
    	if(!myfile.exists())
		{
	    	myfile.createNewFile();
		}    	




        //Get the Recaptcha response
        PrintWriter out = response.getWriter(); 
    	String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);
        
        response.setContentType("text/html");

        // Verify reCAPTCHA
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
        	final JDialog dialog = new JDialog();
        	dialog.setAlwaysOnTop(true);    
        	JOptionPane.showMessageDialog(dialog, "Error: Click Recaptcha");
            response.sendRedirect("index.html");
            return;
        }
        
        //ONLY USE ONCE!!!!
        //try {
		//	UpdateSecurePassword.update();
		//} catch (Exception e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
        
       
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
                        
            // Retrieve parameters from the form box
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            
            // Generate a SQL query to get customer's id
            PreparedStatement myStatement = connection.prepareStatement("SELECT * FROM customers WHERE (email = ?);");
    		myStatement.setString(1, email);
    		ResultSet resultset = myStatement.executeQuery();
    		Boolean success = false;
  
    		while(resultset.next())
    		{
    			String encryptedPassword = resultset.getString("password");
    			success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
                if(success)
    			{
                   String customerId = resultset.getString("id");                    	  
                   Example.customerID = customerId;
                   RequestDispatcher dispatcher=request.getRequestDispatcher("main.html");  
                   dispatcher.forward(request, response);
    	        }
    	        else
    	        {
    	        	final JDialog dialog = new JDialog();
    	        	dialog.setAlwaysOnTop(true);    
    	        	JOptionPane.showMessageDialog(dialog, "Error: Incorrect password"); 	          
    	           response.sendRedirect("index.html");
    	        }
    		}
      		if(!resultset.first())
    		{
            	final JDialog dialog = new JDialog();
            	dialog.setAlwaysOnTop(true);    
            	JOptionPane.showMessageDialog(dialog, "Error, user does not exist!");    	    		
            	response.sendRedirect("index.html");
    		}
            // Close all structures
            resultset.close();
            statement.close();
            connection.close();

        	} catch (Exception ex) {

            // Output Error Massage to html
           ex.printStackTrace();
        		out.println(String.format("<html><head><title>Fabflix: Error</title></head>\n<body><p>SQL error in doGet: %s</p></body></html>", ex.getMessage()));
            return;
        }
        out.close();
    }
}