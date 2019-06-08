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

public class SAXParserExample extends DefaultHandler {

	//Create temporary lists
    List<cs122b.Movie> movieList;
    List<cs122b.genres_in_movies> genresMovieList;
    List<cs122b.genres> genreList;
    List<String> errorsList;
    
    //Create boolean variables to check context
    boolean movieOpen = false;
    boolean filmOpen = false;
    boolean catOpen = false;
    boolean dirNameOpen = false;
    boolean titleNameOpen = false;
    boolean movieIdOpen = false;
    boolean yearOpen = false;
    boolean genreIdOpen = false;
    boolean directorFilmsOpen = false;
    boolean specToggle = false;

    //to maintain context
    private cs122b.Movie tempMovie;
    private cs122b.genres_in_movies tempGenreMovie;
    private cs122b.genres tempGenre;

    //Create the lists
    public SAXParserExample() {
        movieList = new ArrayList<cs122b.Movie>();
        genresMovieList = new ArrayList<cs122b.genres_in_movies>();
        genreList = new ArrayList<cs122b.genres>();
        errorsList = new ArrayList<String>();
    }

    //Run the example
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

    //Actually parse the document
    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("moviesNew.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    //Print out the lists
    private void printData() {

        System.out.println("Movies in list\n");

        Iterator<cs122b.Movie> it = movieList.iterator();
        while (it.hasNext()) {
        	cs122b.Movie val = it.next();
            System.out.println("Director: " + val.director.toString());
            System.out.println("Title: " + val.title.toString());
            System.out.println("Id: " + val.id.toString());
            System.out.println("Year: " + val.year);
            System.out.println("\n");
        }
        
        System.out.println("Genres in list\n");
        Iterator<cs122b.genres> it2 = genreList.iterator();
        while(it2.hasNext()) {
        	cs122b.genres val = it2.next();
            System.out.println("GenreName " + val.toString());
            
        }
        
        System.out.println("Genre-Movie List: \n");
        Iterator<cs122b.genres_in_movies> it3 = genresMovieList.iterator();
        while(it3.hasNext())
        {
        	cs122b.genres_in_movies val = it3.next();
        	System.out.println("GenreId: " + val.genreId.toString());
        	System.out.println("MovieId: " + val.movieId.toString());      			
        }
    }
    
    //Add to database
    private void addToDatabase() throws Exception {
    	//Create new lists for bulk insertion
        List<cs122b.Movie> movieListDB;
        List<cs122b.genres_in_movies> genresMovieListDB;
        List<cs122b.genres> genreListDB;
        movieListDB = new ArrayList<cs122b.Movie>();
        genresMovieListDB = new ArrayList<cs122b.genres_in_movies>();
        genreListDB = new ArrayList<cs122b.genres>();
        cs122b.Movie tempMovieDB;
        cs122b.genres_in_movies tempGenreMovieDB;
        cs122b.genres tempGenreDB;
        
    	//Should add information to the database upon execution
        String loginUser = "root";
        String loginPasswd = "mydoggie";
        String loginUrl = "jdbc:mysql://localhost:3306/fabflix";
        Connection connection = null;
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        Statement statement = connection.createStatement();

	         Iterator<cs122b.Movie> it = movieList.iterator();	         
	         while (it.hasNext()) {
        	cs122b.Movie val = it.next();
        	//Check if already exists and insert movie into database
        	boolean alreadyExists = false;
            PreparedStatement checkMovie = connection.prepareStatement("SELECT id from movies WHERE (id = ?);");
    		checkMovie.setString(1, val.id.toString());
    		ResultSet resultMovie = checkMovie.executeQuery();
    		//check that it exists
    		if(resultMovie.first())
    		{
    			alreadyExists = true;
    		}
    		
    		if(!alreadyExists)
    		{
    			tempMovieDB = new cs122b.Movie();
    			tempMovieDB.director = val.director.toString();
    		    tempMovieDB.id =  val.id.toString();
    		    tempMovieDB.title = val.title.toString();
    		    tempMovieDB.year = val.year;
    		    movieListDB.add(tempMovieDB);
    		}
    		else
    		{
    			errorsList.add(val.title.toString() + " Already exists in db");
    			System.out.println(val.title.toString() + "Already exists in db");
    		}    	           
        }
	         
		        PreparedStatement insertMovie=null;
		        String sqlInsertRecord="";

		        sqlInsertRecord="INSERT INTO movies(id, title, director, year) VALUES (?, ?, ?, ?);";
		        try {
		         connection.setAutoCommit(false);
		         insertMovie = connection.prepareStatement(sqlInsertRecord);
		       // System.out.println("Size = " + movieListDB.size());
		         for(int index = 0; index < movieListDB.size(); index++)
		         {
	    			insertMovie.setString(1, movieListDB.get(index).id.toString());
	    			insertMovie.setString(2, movieListDB.get(index).title.toString());
	    			insertMovie.setString(3, movieListDB.get(index).director.toString());
	    			insertMovie.setInt(4, movieListDB.get(index).year);
	    			System.out.println("Adding " + movieListDB.get(index).title.toString());
			        insertMovie.addBatch();
		         }
		         
		         int[] iNoRows=insertMovie.executeBatch();
		         connection.commit();

		        } catch (Exception e) {
		        	e.printStackTrace();
		        }

		        //STARTING INSERT INTO GENRES
		        List<String> allGenres;
		        allGenres = new ArrayList<String>();
		        PreparedStatement theGenres = connection.prepareStatement("SELECT name from genres");
		        ResultSet results = theGenres.executeQuery();
		        while(results.next())
		        {
		        	allGenres.add(results.getString("name"));
		        }
		        
	     	
		         //Check if exists in genre/insert into genre
		         Iterator<cs122b.genres> it2 = genreList.iterator();
		         while(it2.hasNext()) {
		        	 cs122b.genres val = it2.next();
		        	 boolean alreadyExists = false;
		        	 int index = 0;
		        	 while(index < allGenres.size() && !alreadyExists)
		        	 {
		        		 if(allGenres.get(index).equals(val.name.toString()))
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
		        		 tempGenreDB = new cs122b.genres();		        		
		        		 tempGenreDB.name = val.name.toString();
		        		 genreListDB.add(tempGenreDB);
		        		 allGenres.add(tempGenreDB.name);
		        		System.out.println("Added Genre TO LIST: " + tempGenreDB.name);
		        	 }
		        	 else
		        	 {
		        		 errorsList.add("Genre" + val.name.toString() + " Already exists in db");
		        		 System.out.println("Genre " + val.name.toString() + " Already in db ");
		        	 }
    		    
       }
		         
	PreparedStatement insertGenre=null;
	String sqlInsertRecord2="";
	sqlInsertRecord2="INSERT INTO genres(name) VALUES (?);";

	try {
		connection.setAutoCommit(false);
		insertGenre = connection.prepareStatement(sqlInsertRecord2);
		
		for(int index = 0; index < genreListDB.size(); index++)
		{
			 insertGenre.setString(1, genreListDB.get(index).name.toString()); 
    		 insertGenre.addBatch();
    		 System.out.println("Inserting Genre: " + genreListDB.get(index).name.toString());
		}
		
        int [] iNoRows2=insertGenre.executeBatch();
        connection.commit();

 } catch (Exception e) {
	 e.printStackTrace();
 }  
		        
    List<cs122b.genres_in_movies> allGenreMovies;
    allGenreMovies = new ArrayList<cs122b.genres_in_movies>();
    
    PreparedStatement theGenreMovies = connection.prepareStatement("SELECT movieId, genreId from genres_in_movies");
    ResultSet results2 = theGenreMovies.executeQuery();
    while(results2.next())
    {
    	cs122b.genres_in_movies element = new cs122b.genres_in_movies();
    	element.movieId = results2.getString("movieId");
    	element.actualid = results2.getInt("genreId");
    	allGenreMovies.add(element);
    }
    
        //Add to genres in movies if ready 
        Iterator<cs122b.genres_in_movies> it3 = genresMovieList.iterator();
        while(it3.hasNext()) {
        	cs122b.genres_in_movies val = it3.next();
        	
            PreparedStatement findIdGenre = connection.prepareStatement("SELECT id from genres WHERE (name = ?);");
            findIdGenre.setString(1, val.genreId.toString());
    		ResultSet resultsId = findIdGenre.executeQuery();    
    		
    		if(resultsId.first())
    		{
    			int myInt = resultsId.getInt("id");
    			System.out.println("Genre id is equal to" + myInt);
    			boolean alreadyExists = false;
            	//DOES NOT EXIST
            	int index = 0;
           	 	while(index < allGenreMovies.size() && !alreadyExists)
           	 	{
           	 		if(allGenreMovies.get(index).movieId.equals(val.movieId.toString()) && allGenreMovies.get(index).actualid == myInt)
           	 		{
               	 		System.out.println("List Movie = "  + allGenreMovies.get(index).movieId + " Value from file = " + val.movieId.toString());
               	 		System.out.println("List Genre = "  + allGenreMovies.get(index).actualid + " Value from file = " + myInt);
           	 			alreadyExists = true; 
           	 		}	
           	 		else
           	 		{
           	 			index++;
           	 		}	
           	 	}	        			
    		   
          		if(!alreadyExists)
          		{
          			tempGenreMovieDB = new cs122b.genres_in_movies();
          			tempGenreMovieDB.actualid = myInt;
          			tempGenreMovieDB.movieId = val.movieId.toString();
          			allGenreMovies.add(tempGenreMovieDB);        			
          			genresMovieListDB.add(tempGenreMovieDB);         			
 
          		}
          		else
          		{
          			errorsList.add(val.movieId.toString() + "  Already exists in the database");
          			System.out.println("Already exists in db");
          		}
    			 
    		}
    		else
    		{
    			System.out.println("Couldn't find ID in database");
    		}    		    
        }
        
   PreparedStatement insertGenreMovie=null;
    String sqlInsertRecord3="";
    sqlInsertRecord3="INSERT INTO genres_in_movies(movieId, genreId) VALUES (?, ?);";
    int[] iNoRows3=null;
    System.out.println("Hi i came here!!!!");

    try {
         connection.setAutoCommit(false);
         insertGenreMovie = connection.prepareStatement(sqlInsertRecord3);
	        
         for(int index = 0; index < genresMovieListDB.size(); index++)
         {
        		insertGenreMovie.setString(1, genresMovieListDB.get(index).movieId.toString());			
      			insertGenreMovie.setInt(2, genresMovieListDB.get(index).actualid);
          		insertGenreMovie.addBatch();
          		System.out.println("Adding : " + genresMovieListDB.get(index).movieId.toString());
          		System.out.println("Adding Genre : " + genresMovieListDB.get(index).actualid);
         }
            
	        iNoRows3=insertGenreMovie.executeBatch();
	        connection.commit();

	 } catch (Exception e) {
		 e.printStackTrace();
	 }
        statement.close();
        connection.close();

    }

    public String myString;
    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    	//Opens the element, creating objects
        if (qName.equalsIgnoreCase("movies")) {
             movieOpen=true;          
        }
        else if (qName.equalsIgnoreCase("film")) {
        	tempMovie = new cs122b.Movie();
        	filmOpen = true;
        	specToggle = true;
        }
        else if (qName.equalsIgnoreCase("directorfilms")) {
        	
        	directorFilmsOpen=true;
        }
        else if (qName.equalsIgnoreCase("fid")) {
        	movieIdOpen=true;
        }
        else if (qName.equalsIgnoreCase("t")) {
        	titleNameOpen=true;
        }
        else if (qName.equalsIgnoreCase("year")) {
        	yearOpen=true;
        }
        else if (qName.equalsIgnoreCase("dirn")) {
        	dirNameOpen=true;
        }
        else if (qName.equalsIgnoreCase("cats")) {
        	tempGenreMovie = new cs122b.genres_in_movies();
        	tempGenre = new cs122b.genres();
        	catOpen=true;
        }
        else if (qName.equalsIgnoreCase("cat")) {	
        	genreIdOpen=true;
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
    	//Adds in the value of whatever is in the file
        if (movieOpen) {
        	movieOpen = false;
        }
        if(directorFilmsOpen) {
        	directorFilmsOpen = false;
        }
        if(filmOpen = true) {
        	filmOpen = false;
        	
        }
        if (movieIdOpen) {
       	String movieId = new String(ch, start, length);
         tempMovie.id = movieId;
         myString = tempMovie.id;
         
         movieIdOpen = false;
       }
        if (titleNameOpen) {
       	  String title = new String(ch, start, length);
          tempMovie.title = title;
          titleNameOpen = false;
       }
        if (yearOpen) {
       	 String year = new String(ch, start, length);  
       	try
 	 	 {
       int theYear = Integer.parseInt(year);
       tempMovie.year = theYear;
 	 	 }
 	 	 catch(Exception ex)
 	 	 {
 	 		 tempMovie.year = 0;
 	 	 }
       yearOpen = false;
       }
        if (dirNameOpen) {
        	String director = new String(ch, start, length);
             tempMovie.director = director;
             dirNameOpen = false;
        }
        if (catOpen) {
        	catOpen = false;
        }
        if (genreIdOpen) {
        	if(specToggle)
        	{
        	String genreId = new String(ch, start, length);
       	 	tempGenreMovie.movieId = myString;
       	 	tempGenreMovie.genreId = genreId;
       	 	tempGenre.name = genreId;
        	}
        	genreIdOpen = false;
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
    	//Checks the end of the element, adds it into list
        if (qName.equalsIgnoreCase("movies")) {	
        }
        else if (qName.equalsIgnoreCase("directorfilms")) {

        }
        else if (qName.equalsIgnoreCase("film")) {
        	if(tempMovie.director == null)
        	{
        		tempMovie.director = "N/A";
        	}
        	if(tempMovie.id != null && tempMovie.title != null)
        	{
        		movieList.add(tempMovie);
        	}
        	specToggle = false;
        }
        else if (qName.equalsIgnoreCase("fid")) {

        }
        else if (qName.equalsIgnoreCase("t")) {
 
        }
        else if (qName.equalsIgnoreCase("year")) {
        	
        }
        else if (qName.equalsIgnoreCase("dirn")) {

        }
        else if (qName.equalsIgnoreCase("cats")) {
        	if(tempGenre.name != null)
        	{
        		genreList.add(tempGenre);
        	}
        	if(tempGenreMovie.genreId != null && tempGenreMovie.movieId != null)
        	{
            	genresMovieList.add(tempGenreMovie);
        	}
        }
        else if (qName.equalsIgnoreCase("cat")) {	
        }

    }
}