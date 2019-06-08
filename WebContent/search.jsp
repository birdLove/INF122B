<%@page import="java.sql.*" %>
<%@page import="javax.servlet.RequestDispatcher" %>
<%@page import="javax.servlet.annotation.WebServlet" %>
<%@page import="javax.servlet.http.HttpServlet" %>
<%@page import="javax.servlet.http.HttpServletRequest" %>
<%@page import="javax.servlet.http.HttpServletResponse" %>
<%@page import="java.io.IOException" %>
<%@page import="java.io.PrintWriter" %>
<%@page import="java.sql.Connection" %>
<%@page import="java.sql.DriverManager" %>
<%@page import="java.sql.PreparedStatement" %>
<%@page import="java.sql.ResultSet" %>
<%@page import="java.sql.Statement" %>
<%@page import="java.util.ArrayList" %>
<%@page import="java.util.Iterator" %>
<%@page import="java.util.List" %>

<%@page import="java.io.BufferedReader" %>
<%@page import="java.*" %>
<%@page import="java.io.IOException" %>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.io.OutputStream"%>
<%@page import="java.net.HttpURLConnection"%>
<%@page import="java.net.URL "%>
<%@page language="java" contentType="text/html" pageEncoding="UTF-8" %>

<%-- these statements are just normal Java code, they need to be inside the <% %> brackets--%>
<%
Class.forName("com.mysql.jdbc.Driver").newInstance();
String loginUser = "root";
String loginPasswd = "mydoggie";
String loginUrl = "jdbc:mysql://localhost:3306/fabflix";
Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

// Declare a new statement
Statement statement = connection.createStatement();

//Print out the header
%>
<h1> Welcome Customer: 
<%= (String) session.getAttribute("customerId") %>
</h1>

<% 

//Get attributes
String searchTitle = (String) session.getAttribute("searchTitle");
String theYear = (String) session.getAttribute("theYear");
String searchDirector = (String) session.getAttribute("searchDirector");
String searchStar = (String) session.getAttribute("searchStar");
String choice = (String) session.getAttribute("choice");
String toggleTitle = (String) session.getAttribute("toggleTitle");
String toggleRating = (String) session.getAttribute("toggleRating");
String halfwayQuery = (String) session.getAttribute("halfwayQuery");
String myQuery;
PreparedStatement theStatement;
ResultSet resultSet;
ResultSetMetaData metadata;
String search = "search";

if(choice.equals("SearchBar"))
{
	myQuery = "SELECT distinct movies.id, ratings.rating, title, year, movies.director FROM stars_in_movies, stars, movies left join ratings on movies.id = ratings.movieId WHERE (movies.id = stars_in_movies.movieId) AND (stars.id = stars_in_movies.starId)";      	
	if (searchTitle != "" && searchTitle != null)
	{
		myQuery = myQuery + " AND (";
		String[] splitArray = searchTitle.split("\\s+");
		
		for(int index = 0; index < splitArray.length; index++)
		{
			System.out.println("splitArray is " + splitArray[index]);
			myQuery = myQuery + String.format("(title LIKE \"%s%%\")", splitArray[index]);
			if(splitArray.length-1 != index)
			{
			myQuery = myQuery + " OR ";
			}
		}
		myQuery = myQuery + ")"; 
		System.out.println("Query is " + myQuery);

		
	}
}
//Check if request is from search page or browse page
else if(choice.equals(search))
{
	myQuery = "SELECT distinct movies.id, ratings.rating, title, year, movies.director FROM stars_in_movies, stars, movies left join ratings on movies.id = ratings.movieId WHERE (movies.id = stars_in_movies.movieId) AND (stars.id = stars_in_movies.starId)";      	
	if (searchTitle != "" && searchTitle != null)
	{
		myQuery = myQuery + String.format(" AND (title LIKE \"%s%%\")", searchTitle);
	}

	if (theYear != "" && theYear != null)
	{
		int searchYear = Integer.parseInt(theYear);
		myQuery = myQuery + String.format(" AND (year = %d)", searchYear);
	}

	if (searchDirector != "" && searchDirector != null)
	{
		myQuery = myQuery + String.format(" AND (director LIKE \"%s%%\")", searchDirector);
	}

	if (searchStar != "" && searchStar != null)
	{
		myQuery = myQuery + String.format(" AND (stars.name LIKE \"%s%%\")", searchStar);
	}


	
}
else if (choice.equals("browse"))
{
	System.out.println("Session genre = " + session.getAttribute("genre"));
	
	 myQuery = "SELECT distinct movies.id, ratings.rating, title, year, movies.director FROM stars_in_movies, stars, genres_in_movies, genres, movies left join ratings on movies.id = ratings.movieId WHERE (movies.id = stars_in_movies.movieId) AND (stars.id = stars_in_movies.starId) AND genres_in_movies.movieId = movies.id AND genres_in_movies.genreId = genres.id  And genres.name = \""+ (String) session.getAttribute("genre") +"\"";
	
}
else
{
	
	 myQuery = "SELECT distinct movies.id, ratings.rating, title, year, movies.director FROM stars_in_movies, stars, genres_in_movies, genres, movies left join ratings on movies.id = ratings.movieId WHERE (movies.id = stars_in_movies.movieId) AND (stars.id = stars_in_movies.starId)  AND"
			+ " genres_in_movies.genreId = genres.id AND genres_in_movies.movieId = movies.id "
			+ "And  movies.title like \""+ (String) session.getAttribute("menu") +"%\" ";
	
}
System.out.println("Hi!!");
String toggleRequest = request.getParameter("id");
if(toggleRequest.equals("toggleTitleA"))
{
	myQuery = myQuery + "Order by title ASC";
	toggleTitle = "toggleTitleB";
}
else if(toggleRequest.equals("toggleRatingA"))
{
	myQuery = myQuery + "Order by ratings.rating ASC";
	toggleRating = "toggleRatingB";
}
else if(toggleRequest.equals("toggleRatingB"))
{
	myQuery = myQuery + "Order by ratings.rating DESC";
	toggleRating = "toggleRatingA";
}
else if(toggleRequest.equals("toggleTitleB"))
{
	myQuery = myQuery + "Order by title DESC";
	toggleTitle = "toggleTitleA";
}
else
{
	//Else don't do anything
	toggleTitle = "toggleTitleA";
	toggleRating = "toggleRatingA";
	toggleRequest= "none";
}

int numResults = Integer.parseInt(request.getParameter("num"));
if(numResults == 10)
{
	myQuery = myQuery + " limit 10";
}
else if(numResults == 20)
{
	myQuery = myQuery + " limit 20";	
}
else if(numResults == 50)
{
	myQuery = myQuery + " limit 50";
}
else if(numResults == 100)
{
	myQuery = myQuery + " limit 100";	
}
else
{
	myQuery = myQuery + " limit 100";
}

myQuery = myQuery + " offset "+ request.getParameter("offset");
myQuery = myQuery + ";";
System.out.println("QUERY IS " + myQuery);
 theStatement = connection.prepareStatement(myQuery);
 resultSet = theStatement.executeQuery();
 metadata = resultSet.getMetaData();

System.out.println("Got here~~~");
%>

<%-- the following are HTML mixed with java code, 
     you can see for loops are used to generate a dynamic table.
     normal Java code still needs to be in the <% %> tag
     
     <%= %> is the expression tag. the java code inside needs to be a value
     and that value will be directly write to the html, it's equivalent to out.print()
--%>
<h4>View amount of results</h4>
<p>Search by </p>
<p><%= "<a href=\"search.jsp?id=" + toggleRequest + "&num=10" + "&offset=0\"> 10 Results </a>" %></p>
<p><%= "<a href=\"search.jsp?id=" + toggleRequest + "&num=20" + "&offset=0\"> 20 Results </a>" %></p>
<p><%= "<a href=\"search.jsp?id=" + toggleRequest + "&num=50" + "&offset=0\"> 50 Results </a>" %></p>
<p><%= "<a href=\"search.jsp?id=" + toggleRequest + "&num=100" + "&offset=0\"> 100 Results </a>" %></p>



<h3> Movie Results </h3>
<%="<a href=\"search.jsp?id=" + toggleRequest + "&num=" + numResults + "&offset=" +(Integer.parseInt(request.getParameter("offset"))+ numResults)+"\"> Next </a>" %>
<%="<a href=\"search.jsp?id=" + toggleRequest + "&num=" + numResults + "&offset=" +((Integer.parseInt(request.getParameter("offset"))==0) ? "0" : Integer.parseInt(request.getParameter("offset")) -  numResults ) + "\"> Previous </a>"%>

<table border=1>
    <%-- generate table header: name and type of each row --%>
    <tr>
        <%-- Print out header table --%>
        <td>Id</td>
        <td><%= "<a href=\"search.jsp?id=" + toggleTitle + "&num=" + numResults + "&offset="+request.getParameter("offset")+"\"> Title - Click to sort </a>" %> </td>
        <td>Year</td>
        <td>Director</td>
        <td><%= "<a href=\"search.jsp?id=" + toggleRating + "&num=" + numResults +  "&offset="+request.getParameter("offset")+ "\"> Rating - Click to sort </a>" %> </td>
        <td>List of Genres</td>
        <td>List of Stars</td>
    </tr>
    
    <%-- generate table content: for each row in result set, display a html table row --%>
    <% while (resultSet.next()) { %>
	<tr>
        <%-- generate table row: iterate through the values for this column --%>
        
            <% 
            String id = resultSet.getString("movies.id");
        	String title = resultSet.getString("title");
        	String thisYear = resultSet.getString("year");
        	int year = Integer.parseInt(thisYear);
        	String director = resultSet.getString("director");
        	String rating = resultSet.getString("rating");
        	%>
        	
        	<td><%= id %> </td>
        	<td><%= "<a href=\"singleMovie.html?id=" + id + "\">" + title + "</a>" %> </td>
        	<td><%= year %> </td>
        	<td><%= director %> </td>
        	<td><%= rating %> </td>
        	
        	<% 
        	PreparedStatement state = connection.prepareStatement("SELECT name FROM movies, genres_in_movies, genres WHERE (movies.id = genres_in_movies.movieId) AND (genres_in_movies.genreId = genres.id) AND (movieId = ?);");
    		state.setString(1, id);
    		ResultSet rez1 = state.executeQuery();		
        	%>
        	
        	<td>   		
    		<% while(rez1.next()) { %>
     			<%= rez1.getString("name") + ", " %>
     		<% } %>
     		</td>
     		
     		<%    		
    		PreparedStatement state2 = connection.prepareStatement("SELECT stars.name, stars.id FROM movies, stars_in_movies, stars WHERE (movies.id = stars_in_movies.movieId) AND (stars_in_movies.starId = stars.id) AND (movieId = ?);");
    		state2.setString(1, id);
    		ResultSet rez2 = state2.executeQuery();  
    		%>
     		
     		<td>
    	    <% while(rez2.next()) { %>
    	    	<%= "<a href=\"singleStar.html?id=" + rez2.getString("id") + "\">" + rez2.getString("name") + "</a>, " %> 
     		<% } %>
    		</td>        
    </tr>
    <% }
    resultSet.close();
    statement.close();
    connection.close();
    %>
</table>