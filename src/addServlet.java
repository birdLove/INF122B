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

import com.google.gson.JsonObject;

import cs122b.Cart;

/**
 * Servlet implementation class cartServlet
 * THIS IS THE SERVLET FOR SHOPPING CART MAPS TO CART.JSP
 */
@WebServlet("/addServlet")
public class addServlet extends HttpServlet {
	public String getServletInfo() {
        return "Servlet connects to MySQL database and displays result of a SELECT";
    }

    // Use http GET
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {   
    	String id = request.getParameter("movieId");
    	int quantity = Integer.parseInt(request.getParameter("quantity"));
    	int index = 0;
    	int found = 0; 	
    	while(index < ShoppingCart.list.size())
    	{
    		System.out.println("MovieId = " + ShoppingCart.list.get(index).movieId + " Sent = " + id);
    		if(ShoppingCart.list.get(index).movieId.equals(id) && found == 0)
    		{
    			ShoppingCart.list.get(index).numItem = quantity;
    			found = 1;
    		}
    		else
    		{
    			index++;
    		}
    	}
    	
    	if(found == 0)
    	{
    		response.setStatus(500);
    	}
    	else
    	{
	    	PrintWriter out = response.getWriter();
	    	JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("Message", "Just added " + quantity + " to " + id + " from the list");
			out.write(jsonObject.toString());
			response.setStatus(200);
    	}
    }

}