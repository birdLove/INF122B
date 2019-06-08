import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

//Class to handle Parsing information to stars_in_movies
public class SAXParserExampleStars extends DefaultHandler {
	
	//Create temporary lists
    List<cs122b.stars_in_movies> starsMovieList;
    List<String> errorsList;
    
    //Create variables to open parsing
    boolean dirFilmsOpen = false;
    boolean movieOpen = false;
    boolean movieIdOpen = false;
    boolean starIdOpen = false;
   
    //Create private variable to put in list
    private cs122b.stars_in_movies tempStarMovie;

    public SAXParserExampleStars() {
    	//Create new movie list
        starsMovieList = new ArrayList<cs122b.stars_in_movies>();
        errorsList = new ArrayList<String>();
    }

    //Run the sax parser
    public List<String> runExample() {
        parseDocument();
       // printData();
        try {
			addToDatabase();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return errorsList;
    }

    //Call parse to parse the document
    private void parseDocument() {
        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("starsInMoviesNew.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    //Prints the data in the list
    private void printData() {
        
        System.out.println("Star-Movies in list\n");
        Iterator<cs122b.stars_in_movies> it2 = starsMovieList.iterator();
        while(it2.hasNext()) {
        	cs122b.stars_in_movies val = it2.next();
            System.out.println("StarId " + val.starId.toString());
            System.out.println("MovieId " + val.movieId.toString());
        }
    }
    
    //Add to the database stars_in_movies
    private void addToDatabase() throws Exception {
    	 String loginUser = "root";
         String loginPasswd = "mydoggie";
         String loginUrl = "jdbc:mysql://localhost:3306/fabflix";
         
         //Lists for bulk initialization
         List<cs122b.stars_in_movies> starsMovieListDB;
         starsMovieListDB = new ArrayList<cs122b.stars_in_movies>();
         cs122b.stars_in_movies tempStarMovieDB;
         
         Connection connection = null;
         Class.forName("com.mysql.jdbc.Driver").newInstance();
         connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
         Statement statement = connection.createStatement();

         List<cs122b.stars> allStars;
	        allStars = new ArrayList<cs122b.stars>();
	        PreparedStatement theStars = connection.prepareStatement("SELECT id, name from stars");
	        ResultSet results = theStars.executeQuery();
	        while(results.next())
	        {
	        	cs122b.stars element = new cs122b.stars();
	        	element.name = results.getString("name");
	        	element.id = results.getString("id");
	        	allStars.add(element);
	        }
	        
	        List<String> allMovies;
	        allMovies = new ArrayList<String>();
	        PreparedStatement theMovie = connection.prepareStatement("SELECT id from movies");
	        ResultSet movieResults = theMovie.executeQuery();
	        while(movieResults.next())
	        {
	        	allMovies.add(movieResults.getString("id"));
	        }
	        
	        List<cs122b.stars_in_movies> allStarMovies;
	        allStarMovies = new ArrayList<cs122b.stars_in_movies>();
	        PreparedStatement movieStars = connection.prepareStatement("SELECT movieId, starId from stars_in_movies");
	        ResultSet results2 = movieStars.executeQuery();
	        while(results2.next())
	        {
	        	cs122b.stars_in_movies element = new cs122b.stars_in_movies();
	        	element.movieId = results2.getString("movieId");
	        	element.starId = results2.getString("starId");
	        	allStarMovies.add(element);
	        }
	        
         Iterator<cs122b.stars_in_movies> it = starsMovieList.iterator();
         while(it.hasNext()) {
        	 cs122b.stars_in_movies val = it.next();
        	//DOES NOT EXIST
        	 boolean starInDb = false;
        	 boolean movieInDb = false;
        	 String tempStarId = "";
         	int index = 0;
        	 	while(index < allStars.size() && !starInDb)
        	 	{
        	 		if(allStars.get(index).name.toString().equals(val.starId.toString()))
        	 		{
        	 			starInDb = true;
        	 			tempStarId = allStars.get(index).id.toString();
        	 		}	
        	 		else
        	 		{
        	 			index++;
        	 		}	
        	 	}	        	
        	 
             	index = 0;
        	 	while(index < allMovies.size() && !movieInDb)
        	 	{
        	 		if(allMovies.get(index).equals(val.movieId.toString()))
        	 		{
        	 			movieInDb = true;
        	 		}	
        	 		else
        	 		{
        	 			index++;
        	 		}	
        	 	}	        	
        	 
        	 	
     		if(starInDb && movieInDb)
     		{
            	boolean alreadyExists = false;
            	//DOES NOT EXIST
            	index = 0;
           	 	while(index < allStarMovies.size() && !alreadyExists)
           	 	{
           	 		if(allStarMovies.get(index).movieId.equals(val.movieId.toString()) && allStarMovies.get(index).starId.equals(tempStarId))
           	 		{
           	 			alreadyExists = true; 
           	 		}	
           	 		else
           	 		{
           	 			index++;
           	 		}	
           	 	}	        	
                
         		if(!alreadyExists)
         		{        			
         			tempStarMovieDB = new cs122b.stars_in_movies();
         			tempStarMovieDB.movieId = val.movieId.toString();
         			tempStarMovieDB.starId = tempStarId;
         			allStarMovies.add(tempStarMovieDB);
         			starsMovieListDB.add(tempStarMovieDB);
  
         		}
         		else
         		{
         			errorsList.add(val.movieId.toString() + " and " + val.starId.toString()
         					+ " already exists in db");
         		}
     		}
     		else
     		{
     			errorsList.add(val.starId.toString() + " or " + val.movieId.toString() + " doesn't exist in database");
     		}
     		
         }
         
         PreparedStatement insertStars=null;
	        String sqlInsertRecord="";
	        sqlInsertRecord="INSERT INTO stars_in_movies(starId, movieId) VALUES (?, ?);";
	        try {
	         connection.setAutoCommit(false);
	         insertStars = connection.prepareStatement(sqlInsertRecord);	

	         for(int index = 0; index < starsMovieListDB.size(); index++)
	         {
	        	 insertStars.setString(1, starsMovieListDB.get(index).starId.toString());
      			insertStars.setString(2, starsMovieListDB.get(index).movieId.toString());   
  				insertStars.addBatch();
	         }
      
	         
	      System.out.println(insertStars.toString());   
	      int[] rows =insertStars.executeBatch();
	        connection.commit();

	        
	 } catch (Exception e) {
		 e.printStackTrace();
	 }
 	        
         statement.close();
         connection.close();

    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    	//Starts element, opens variable
        if (qName.equalsIgnoreCase("dirfilms")) {
            dirFilmsOpen = true;        
        }
        else if (qName.equalsIgnoreCase("m")) {
        	tempStarMovie = new cs122b.stars_in_movies();
        	movieOpen=true;
        }
        else if (qName.equalsIgnoreCase("f")) {
        	movieIdOpen=true;
        }
        else if (qName.equalsIgnoreCase("a")) {
        	starIdOpen=true;
        } 
    }

    //Get string
    public void characters(char[] ch, int start, int length) throws SAXException {
    	//adds the element to the list
        if (movieOpen) {
        	movieOpen = false;
        }
        if(dirFilmsOpen) {
        	dirFilmsOpen = false;
        }
        if (movieIdOpen) {       
       	String movieId = new String(ch, start, length);
       	tempStarMovie.movieId = movieId;
        movieIdOpen = false;
        }
        if (starIdOpen) {
       	String starId = new String(ch, start, length);
         tempStarMovie.starId = starId;
         starIdOpen = false;
       }
        
    }

    //End the element
    public void endElement(String uri, String localName, String qName) throws SAXException {

    	//Ends the element in xml file - adds to list   	
        if (qName.equalsIgnoreCase("dirfilms")) {

        }
        else if (qName.equalsIgnoreCase("m")) {
        	if(tempStarMovie.movieId != null && tempStarMovie.starId != null)
        	{
        	starsMovieList.add(tempStarMovie);
        	}

        }
        else if (qName.equalsIgnoreCase("f")) {         

        }
        else if (qName.equalsIgnoreCase("a")) {

        }

    }
}