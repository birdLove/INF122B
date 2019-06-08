
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import cs122b.Cart;
import java.util.ArrayList;
import java.net.HttpURLConnection;
import javax.sql.DataSource;
/*
 * WEB SERVLET FOR FORM - THIS IS FOR THE LOGIN PAGE
 * THIS IS THE RESPONSE FROM THE LOGIN PAGE
 */
@WebServlet(name = "checkout", urlPatterns = "/checkout")

public class checkout extends HttpServlet {

    public String getServletInfo() {
        return "Servlet connects to MySQL database and displays result of a SELECT";
    }

    // Use http GET
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
      

        response.setContentType("text/html");    // Response mime type

        //Output stream to STDOUT
        PrintWriter out = response.getWriter();
        response.getWriter().append("Served at: ").append(request.getContextPath());
                
        try {
        	//Create instance
       	 Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/fabflixWrite");

            // the following commented lines are direct connections without pooling
            //Class.forName("org.gjt.mm.mysql.Driver");
            //Class.forName("com.mysql.jdbc.Driver").newInstance();
            //Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
           
            HttpSession session = request.getSession(true);
            
            Integer accessCount = (Integer)session.getAttribute("accessCount");

            session.setAttribute("customerId", Example.customerID);
            session.setAttribute("accessCount", accessCount); 
          
            if (ds == null)
                out.println("ds is null.");

            Connection connection = ds.getConnection();
            if (connection == null)
                out.println("connection is null.");
            // Declare a new statement
            Statement statement = connection.createStatement();

                        
            // Retrieve parameter "name" from request, which refers to the value of <input name="name"> in index.html
            String firstName = request.getParameter("firstname");
            String lastName = request.getParameter("lastname");
            String creditcard = request.getParameter("creditcard");
          //  LocalDateTime expirationDate = request.getParameter("expiredate");
    
            // Get information
            String getCreditCard = String.format("Select ccId from customers where (id = \"%s\");", Example.customerID);
            
            //Generate the actual query
            PreparedStatement myStatement = connection.prepareStatement(getCreditCard);
    		ResultSet resultset = myStatement.executeQuery();
    		String ccId = "";
    		
    		while(resultset.next())
    		{
    			ccId = resultset.getString("ccId");
    		}	
    		
            String getInfo = String.format("Select id, firstname, lastname, expiration from creditcards where (id = \"%s\");", ccId);
            PreparedStatement myStatement2 = connection.prepareStatement(getInfo);
        	ResultSet creditCardInfo = myStatement2.executeQuery();
        		while(creditCardInfo.next())
        		{
    				//If right, insert
                    if( (creditCardInfo.getString("id").equals(creditcard)) && (creditCardInfo.getString("firstname").equals(firstName)) && (creditCardInfo.getString("lastname").equals(lastName)))
    			 	{        
                    	//Insert to sales in database
                    	for(int index = 0; index < ShoppingCart.list.size(); index++)
                    	{	
                			if(ShoppingCart.list.get(index).customerId == Example.customerID)
                			{
                				System.out.println(ShoppingCart.list.get(index).movieId);
                				//Insert into sales.
                				for(int index2 = 0; index2 < ShoppingCart.list.get(index).numItem; index2++)
                				{
                					DateTimeFormatter myFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                					
                						String insertQuery = String.format("INSERT INTO sales(customerId, movieId, saleDate) VALUES(\"%s\", \"%s\", ", ShoppingCart.list.get(index).customerId, ShoppingCart.list.get(index).movieId);
                						insertQuery = insertQuery + "\"" + ShoppingCart.list.get(index).saleDate.format(myFormat)  +  "\");";
                						System.out.println(insertQuery);
                						PreparedStatement myStatement4 = connection.prepareStatement(insertQuery);
                						myStatement4.execute();                    				               			
                				}
                			}
                     
                    	}
                    	//Delete everything
                    	for(int index = 0; index < ShoppingCart.list.size(); index++)
                    	{
                    		ShoppingCart.list.remove(index);
                    	}
                    	response.sendRedirect("checkout.jsp");
    	            }
    	            else
    	            {
    	            	final JDialog dialog = new JDialog();
    	            	dialog.setAlwaysOnTop(true);    
    	            	JOptionPane.showMessageDialog(dialog, "Sorry, wrong information");    	    		
    	            	response.sendRedirect("checkout.html");
    	            }
        		}
       
            // Close all structures
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