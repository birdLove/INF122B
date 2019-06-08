
import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * Servlet implementation class SEARCH - THIS IS THE RESPONSE FROM SEARCH.HTML
 * This class is just temporary, used to send information to search.jsp
 */

@WebServlet(name = "searchBarThis", urlPatterns = "/searchBarThis")

public class searchBarThis extends HttpServlet {
	 public String getServletInfo() {
	        return "Servlet connects to MySQL database and displays result of a SELECT";
	    }

	    // Use http GET
	    public void doGet(HttpServletRequest request, HttpServletResponse response)
	            throws IOException {   	
	    	HttpSession session = request.getSession(true); 
	    	String choice = "SearchBar";
	    	
	    	//Get variables
	    	String searchTitle = request.getParameter("title");
	    	
	    	//Forward to JSP file
	    	session.setAttribute("searchTitle", searchTitle);
	    	session.setAttribute("choice", choice);
	    	session.setAttribute("customerId", Example.customerID);
	    	session.setAttribute("cart", ShoppingCart.list);
	    	response.sendRedirect("search.jsp?id=\"none\"&num=100&offset=0");
	    	
	    	

	    }

}

