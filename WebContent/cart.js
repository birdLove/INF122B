$(function() {
	// Get id from URL
	// Makes the HTTP GET request and registers on success callback function handleResult
	jQuery.ajax({
	    dataType: "json",  // Setting return data type
	    method: "GET",// Setting request method
	    url: "/Project1/api/cartServlet", // Setting request url, which is mapped by StarsServlet in Stars.java
	    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
	});	
});


/**
 * This example is following frontend and backend separation.
 * Before this .js is loaded, the html skeleton is created.
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */
/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");
    // Use regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}




/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleResult(resultData) {
    console.log("handleResult: populating movie info from resultData");
    console.log("Rez = " + resultData );
    
    //Output customer id
    let custInfo = jQuery("#custId");
    custInfo.append("<p>Welcome Customer: " + resultData[0]['sales'][0]['customerId'] + "</p>");
    
    console.log("handleResult: populating cart #1 from resultData");
    // Populate the star table
    let cartElement = jQuery("#shoppingCart");
    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData[0].cart.length; i++) {
    		let rowHTML = "";
    		rowHTML += "<tr>";
    		rowHTML += "<th>" + resultData[0].cart[i]['title'] + "</th>";
    		rowHTML += "<th>" + resultData[0].cart[i]['movieId'] + "</th>";
    		rowHTML += "<th>" + resultData[0].cart[i]['saleDate'] + "</th>";
    		rowHTML += "<th>" + resultData[0].cart[i]['numItem'] + "</th>";
    		rowHTML +=
    			"<th>" + "<input type=\"TEXT\" placeholder=\"Enter #\" id=\"numQuantity\" required><button id=\"addButton\" name=\"addButton\" onclick='addHere(\"" + resultData[0].cart[i]['movieId']
    			+ "\"," + i + ")'>Add Quantity</button>" + "</th>";
        		rowHTML +=
            	"<th>" + "<button id=\"delButton\" name=\"delButton\" onclick='deleteHere(\"" + resultData[0].cart[i]['movieId'] + "\"," + i + ")'>Delete this</button>" + "</th>";
        		rowHTML += "</tr>";
        		// Append the row created to the table body, which will refresh the page
        		cartElement.append(rowHTML);
    }

		console.log("handleResult: populating sales #2 from resultData");
		
		// Populate the star table
		let sale = jQuery("#previousSales");
		for (let i = 0; i <  resultData[0].sales.length; i++) {
		    let rowHTML = "";
		    rowHTML += "<tr>";
		    rowHTML += "<th>" + resultData[0].sales[i]['titleSale'] + "</th>";
		    rowHTML += "<th>" + resultData[0].sales[i]['movieIdSale'] + "</th>";
		    rowHTML += "<th>" + resultData[0].sales[i]['saleDateSale'] + "</th>";
		    rowHTML += "</tr>";
		    // Append the row created to the table body, which will refresh the page
		    sale.append(rowHTML);
		}
}

//Add button
function addHere(movieId,index)
{
		var x = document.getElementById('numQuantity').value;
		if(x > 0)
			{
			$.ajax({dataType: "json",  // Setting return data type
			    method: "GET", url: "addServlet", data: { 
		        movieId: movieId, 
		        index: index, 
		        quantity: x
		      },
		      success: function(response) {
		  	    	alert("Just added to " + movieId); 	  
		      },
		      error: function(xhr) {
		        //Do Something to handle error
		      }
		      });
			var y = document.getElementById("shoppingCart").rows[index+1].cells;
			y[3].innerHTML = x;
			}
		else
			{
			alert("Value is lower than 0, cannot do!");
			}
	    
	
}

//Delete button
function deleteHere(movieId,index)
{
	//document.getElementById("shoppingCart").deleteRow(index);
	    $.ajax({dataType: "json",  // Setting return data type
		    method: "GET", url: "delServlet", data: { 
	        movieId: movieId, 
	        index: index
	      },
	      success: function(response) {
	  	    	alert("Just deleted " + movieId); 	  
	      },
	      error: function(xhr) {
	        //Do Something to handle error
	      }
	    });
	    document.getElementById("shoppingCart").deleteRow(index+1);


}


