import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime; 

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cs122b.Cart;

/**
 * Servlet implementation class cartServlet
 * THIS IS THE SERVLET FOR SHOPPING CART MAPS TO CART.JSP
 */
@WebServlet("/addMovieToCart")
public class addMovieToCart extends HttpServlet {
	public String getServletInfo() {
        return "Servlet connects to MySQL database and displays result of a SELECT";
    }

    // Use http GET
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {   
    	HttpSession session = request.getSession(true); 
    	
    	//Get variables from form
    	String movieId = request.getParameter("id");
    	
    	if(movieId != null && !movieId.isEmpty())
    	{
    		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
        	LocalDateTime now = LocalDateTime.now();  
    		Cart addMovie = new Cart();
    		addMovie.customerId = Example.customerID;
    		addMovie.movieId = movieId;
    		addMovie.saleDate = now; 
    		addMovie.numItem = 1;
    		ShoppingCart.list.add(addMovie);
    		
    		for(int index = 0; index < ShoppingCart.list.size(); index++)
    		{
    			System.out.println((ShoppingCart.list.get(index).movieId));
    		}
    	}
    	response.sendRedirect("cart.html");

    }

}