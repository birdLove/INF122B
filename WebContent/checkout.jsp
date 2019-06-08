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
<%@page import="java.util.*" %>
<%@page import="java.io.BufferedReader" %>
<%@page import="java.io.IOException" %>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.io.OutputStream"%>
<%@page import="java.net.HttpURLConnection"%>
<%@page import="java.util.Collections" %>
<%@page import="java.net.URL "%>
<%@page import="java.util.ArrayList "%>
<%@page import="java.util.List"%>
<%@page import="cs122b.Cart"%>
<%@page language="java" contentType="text/html" pageEncoding="UTF-8" %>

<%-- these statements are just normal Java code, they need to be inside the <% %> brackets--%>
<%
Class.forName("com.mysql.jdbc.Driver").newInstance();
String loginUser = "root";
String loginPasswd = "mydoggie";
String loginUrl = "jdbc:mysql://172.31.39.37:3306/fabflix";
Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

// Declare a new statement
Statement statement = connection.createStatement();

//Print out the header
%>

<h1>CONFIRMATION PAGE</h1>

<h1>Shopping Cart Page</h1>
<h2> Welcome Customer: 
<%=(String) session.getAttribute("customerId")%>
</h2>

<a href="main.html">Main Page</a>


<%
	//Get attributes
String movieId = (String) session.getAttribute("movieId");

String myQuery = "Select * FROM sales WHERE " + String.format("(customerId = \"%s\")", (String) session.getAttribute("customerId"));
PreparedStatement theStatement = connection.prepareStatement(myQuery);
ResultSet resultSet = theStatement.executeQuery();
ResultSetMetaData metadata = resultSet.getMetaData();
%>


<%-- the following are HTML mixed with java code, 
     you can see for loops are used to generate a dynamic table.
     normal Java code still needs to be in the <% %> tag
     
     <%= %> is the expression tag. the java code inside needs to be a value
     and that value will be directly write to the html, it's equivalent to out.print()
--%>

    <h1>Confirmation page!!!</h1>

<h2>All sales</h2>
<table border=1>
    <%-- generate table header: name and type of each row --%>
    <tr>
        <%-- Print out header table --%>
        <td>customerId</td>
        <td>movieTitle</td>
        <td>movieId</td>
        <td>saleDate</td>
    </tr>
    
    <%-- generate table content: for each row in result set, display a html table row --%>
    <% while (resultSet.next()) { %>
	<tr>
        <%-- generate table row: iterate through the values for this column --%>
        
            <% 
        	String customerId2 = resultSet.getString("customerId");
        	String movieId2 = resultSet.getString("movieId");
        	String saleDate = resultSet.getString("saleDate").toString();
        	%>
        	
        	<% 
       		 String myQuery2 = "Select title FROM movies WHERE " + String.format("(id = \"%s\")", movieId2);
			 PreparedStatement theStatement2 = connection.prepareStatement(myQuery2);
			 ResultSet resultSet2 = theStatement2.executeQuery();
			 ResultSetMetaData metadata2 = resultSet2.getMetaData();
			 String title2 = "hi";
			 
			 while(resultSet2.next()) { 
				 title2 = resultSet2.getString("title");
			  } %>
			
        	<td><%= customerId2 %>
        	<td><%= title2 %> </td>
           	<td><%= movieId2 %> </td>
        	<td><%= saleDate %> </td>
        	   
    </tr>
  
    <% } 
    resultSet.close();
    statement.close();
    connection.close();%>
    </table>