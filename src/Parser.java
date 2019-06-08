import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;
import cs122b.Movie;
import cs122b.genres_in_movies;
import cs122b.stars_in_movies;
import cs122b.genres;
import cs122b.stars;
/**
 * Servlet implementation class Parser
 */
@WebServlet("/Parser")
public class Parser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Parser() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
     
   	 //Links
     response.setContentType("text/html");
     PrintWriter out = response.getWriter();
     
     //View shopping cart page
     out.println("<a href=\"index.html\">Back to index</a>");

      int choice = Integer.parseInt(request.getParameter("insert"));

      List<String> newList = new ArrayList<String>();
      
      if(choice == 0)
      {
    	  out.println("<p>" + "Parsing Movies" + "</p>");
    	  out.flush();
           SAXParserExample spe = new SAXParserExample();
    	   newList.addAll(spe.runExample());
    	   out.flush();
      }
      else if(choice == 1)
      {
    	  out.println("<p>" + "Parsing Stars in movies" + "</p>");   
    	  out.flush();
    	    SAXParserExampleStars spe2 = new SAXParserExampleStars();
    	    newList.addAll(spe2.runExample());
    	    out.flush();
      }
      else if(choice == 2)
      {
    	  out.println("<p>" + "Parsing actors" + "</p>");
    	  out.flush();
          SAXParserExampleActors spe3 = new SAXParserExampleActors();     
          newList.addAll(spe3.runExample());
          out.flush();
      }
      else
      {
    	  //Not sure how it got here
    	 response.sendRedirect("index.html");
      }
      
      //Output errors
      for(int index = 0; index < newList.size(); index++)
      {
	        out.println("<p>" + newList.get(index) + "</p>");
      }
      out.flush();
       
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
