// Event handler, the callback function to handle the API response
function handleResult(res) {
    console.log(res);
    debugger;
    let movieDom = $('.movies');
    movieDom.empty(); // Cear the previous results

    // Manually build the HTML Table with the response
    let rowHTML = "<table border=\"1\"><tr><td>Movie ID</td><td>Title</td><td>Director</td>";
    let movieList = res.movieList;
    console.log(movieList);
    
    for (let i = 0; i < movieList.length; ++i) {
        rowHTML += "<tr>";
        let movieObject = movieList[i];

        rowHTML += "<td>" + movieObject["movieId"] + "</td>";
        rowHTML += "<td>" + movieObject["title"] + "</td>";
        rowHTML += "<td>" + movieObject["director"] + "</td>";
        rowHTML += "</tr>";
    }
    rowHTML += "</table>";
    debugger;
    movieDom.append(rowHTML);
}

function handleError() {
    console.log("erorr")
}

// Overwrite the default submit behaviour of the HTML Form
$("form").submit(function (event) {
        event.preventDefault(); // Prevent the default form submit event, using ajax instead
        
        let title = $(".title").val() // Extract data from search input box to be the title argument
        console.log("Search title: " + title);
        debugger;


        $.ajax({
            method: "GET", // Declare request type
            url: "http://andromeda-70.ics.uci.edu:6978/api/movies/search?limit=10&offset=10&title=" + title,
            dataType:"json",
            headers:{
                "Access-Control-Allow-Origin": "*",
                "email":"binghal@uci.edu",
                "sessionID":"sessionID"
            },
            crossDomain: true,
            success: handleResult, // Bind event handler as a success callback
            error: handleError
        });
    }
);
