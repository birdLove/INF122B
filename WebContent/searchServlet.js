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

function splitString(str)
{
	var temp = new Array();
	// this will return an array with strings "1", "2", etc.
	temp = str.split(",");

	return temp;
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleResult(resultData) {
    console.log("handleResult: populating LIST info from resultData");
    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let starInfoElement = jQuery("#table_movies_info");

    console.log("handleResult: populating movie table from resultData");
    // Populate the star table
    // Find the empty table body by id "mymovie_table_body"
    let movieTableBodyElement = jQuery("#table_movies_body");
    // Concatenate the html tags with resultData jsonObject to create table rows

    for (let i = 0; i < Math.min(100, resultData.length); i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]['movieId'] + "</th>";
        rowHTML +=
            "<th>" +
            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="singleMovie.html?id=' + resultData[i]['movieId'] + '">'
            + resultData[i]["title"] +     // display star_name for the link text
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]['year'] + "</th>";
        rowHTML += "<th>" + resultData[i]['director'] + "</th>";
        rowHTML += "<th>" + resultData[i]['rating'] + "</th>";
        
        //Create the separated lists
        var tempGenre = splitString(resultData[i]["genreList"]);
        var tempId = splitString(resultData[i]["starIdList"]);
        var tempName = splitString(resultData[i]["starList"])
        rowHTML +=
        "<th>";
        rowHTML += tempGenre.join(",");
            // Add a link to single-star.html with id passed with GET url parameter
                // display star_name for the link text 
         rowHTML += "</th>";
         rowHTML +=
             "<th>";
         for(var index = 0; index < tempId.length; index++)
         {
        	 tempId[index] = tempId[index].replace(/\s+/g, '');
        	 tempId[index] = tempId[index].replace(/[\[\]']+/g,'');
        	// Add a link to single-star.html with id passed with GET url parameter
        rowHTML += '<a href="singleStar.html?id=' + tempId[index] + '">'
             + tempName[index] +     // display star_name for the link text
             '</a>' + "," ;
         }

            rowHTML += "</th>";
        rowHTML += "</tr>";
        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}
/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */
// Get id from URL
let star = getParameterByName('star');
let title = getParameterByName('title');
let director = getParameterByName('director');
let year = getParameterByName('year');
let offset = getParameterByName('offset');
// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "/Project1/api/searchServlet?title=" + title + "&year=" + year + "&director=" + director + "&star=" + star + "&offset=" + offset, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});