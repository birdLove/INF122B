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

public class SAXParserExampleActors extends DefaultHandler {
    List<cs122b.stars> starList;
    List<String> errorsList;
    boolean actorsOpen = false;
    boolean actorOpen = false;
    boolean starNameOpen = false;
    boolean starIdOpen = false;
    boolean birthYearOpen = false;

    //to maintain context
    private cs122b.stars tempStar;

    public SAXParserExampleActors() {
        starList = new ArrayList<cs122b.stars>();
        errorsList = new ArrayList<String>();
    }

    public List<String> runExample() {
        parseDocument();
      //  printData();
       try {
			addToDatabase();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       return errorsList;
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("actorsNew.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void printData() {

        System.out.println("Stars in list\n");

        Iterator<cs122b.stars> it = starList.iterator();
        while (it.hasNext()) {
        	cs122b.stars val = it.next();
            System.out.println("StarId: " + val.name.toString());
            System.out.println("BirthYear: " + val.birthYear);
            System.out.println("\n");
        }
        
    }
    
    private void addToDatabase() throws Exception {
   	 String loginUser = "root";
     String loginPasswd = "mydoggie";
     String loginUrl = "jdbc:mysql://localhost:3306/fabflix";
     
     List<cs122b.stars> starListDB;
     starListDB = new ArrayList<cs122b.stars>();
     cs122b.stars tempStarDB;

     Connection connection = null;
     Class.forName("com.mysql.jdbc.Driver").newInstance();
     connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
     Statement statement = connection.createStatement();
     
		PreparedStatement getMaxStar = connection.prepareStatement("SELECT id from stars ORDER BY id DESC limit 1;");
	 		ResultSet maxStar = getMaxStar.executeQuery();
 			String starId = "";
	 		while(maxStar.next())
	 		{
	 			starId = maxStar.getString("id");
	 		}
	 		int theId = Integer.parseInt(starId.substring(2,starId.length()-1));
	 		
	        List<String> allStars;
	        allStars = new ArrayList<String>();
	        PreparedStatement theStars = connection.prepareStatement("SELECT name from stars");
	        ResultSet results = theStars.executeQuery();
	        while(results.next())
	        {
	        	allStars.add(results.getString("name"));
	        }
	        
	    Iterator<cs122b.stars> it = starList.iterator();
	    while(it.hasNext()) {
    	cs122b.stars val = it.next();
    	boolean alreadyExists = false;
    	int index = 0;
   	 	while(index < allStars.size() && !alreadyExists)
   	 	{
   	 		if(allStars.get(index).equals(val.name.toString()))
   	 		{
   	 			System.out.println("Allstars: " + allStars.get(index) + " value : " + val.name.toString());
   	 			alreadyExists = true; 
   	 		}	
   	 		else
   	 		{
   	 			index++;
   	 		}	
   	 	}	        	
 		
 		if(!alreadyExists)
 		{
	
 	 		theId++;
 	 		starId = starId.substring(0,2) + theId; 		
 	 		tempStarDB = new cs122b.stars();
 	 		tempStarDB.birthYear = val.birthYear;
 	 		tempStarDB.name = val.name.toString();
 	 		tempStarDB.id = starId;
 	 		allStars.add(tempStarDB.name);
 	 		starListDB.add(tempStarDB); 			
 		}
 		else
 		{
 			System.out.println("So sorry, can't add what u were looking for!" + val.name.toString());
 				errorsList.add(val.name.toString() + " Already exists in db");
 		}
     }
     PreparedStatement insertStarName=null;
     String sqlInsertRecord="";

     sqlInsertRecord="INSERT INTO stars(id, name, birthYear) VALUES (?, ?, ?);";
     try {
      connection.setAutoCommit(false);
      insertStarName = connection.prepareStatement(sqlInsertRecord);	
      
      for(int index = 0; index < starListDB.size(); index++)
      {
    	  insertStarName.setString(1, starListDB.get(index).id.toString());
			insertStarName.setString(2, starListDB.get(index).name.toString());
			insertStarName.setInt(3, starListDB.get(index).birthYear);
			insertStarName.addBatch();
 			System.out.println("Adding : " + starListDB.get(index).name.toString() + " with id = " + starListDB.get(index).id.toString());
      }
      
	   int [] iNoRows=insertStarName.executeBatch();
	        connection.commit();

	 } catch (Exception e) {
		 e.printStackTrace();
	 }
	        
     statement.close();
     connection.close();


    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //Start by reading 
        if (qName.equalsIgnoreCase("actor")) {
        	actorOpen=true;
        	tempStar = new cs122b.stars();
        }
        else if (qName.equalsIgnoreCase("stagename")) {
        	starIdOpen=true;	
        }        
        else if (qName.equalsIgnoreCase("dob")) {
        	birthYearOpen=true;
        	
        }
      
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
    	
        if (starIdOpen) {      	 
       	 String theName = new String(ch, start, length);     	
         tempStar.name = theName;
         starIdOpen = false;
        }
        if (actorOpen)
        {
        	actorOpen = false;
        }
        if (birthYearOpen) {
        	String year = new String(ch, start, length);
        	try
     	 	 {
           int birthYear = Integer.parseInt(year);
           tempStar.birthYear = birthYear;
     	 	 }
     	 	 catch(Exception ex)
     	 	 {
     	 		 tempStar.birthYear = 0;
     	 	 }
        	 birthYearOpen = false;          
       }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("actor")) {
        	if(tempStar.name != null)
        	{
        	starList.add(tempStar);
        	}
        }
        else if (qName.equalsIgnoreCase("stagename")) {
        }
        else if (qName.equalsIgnoreCase("dob")) {
        }
       

    }
}