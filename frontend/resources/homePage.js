let offsetValue = 0;
let searchingMethod = "";

function getDetail(movieId){
	console.log(movieId);
	localStorage.setItem("clickedMovieId", movieId);
	location.replace("movieDetailPage.html");
}

function getCookie(name){
	let cookieList = document.cookie.split("; ")
	for (let i = 0; i < cookieList.length; ++i){
		let elementList = cookieList[i].split("=");
		if (elementList[0] == name){
			return elementList[1];
		}
	}
}

function basicSearchTitle(){
	offsetValue = 0;
	let title = $(".title").val();
	let email = getCookie("email");
	let sessionId = getCookie("sessionId");
	searchingMethod = "title";

	$.ajax({
            method: "GET", // Declare request type
            url: "http://andromeda-70.ics.uci.edu:6976/api/g/movies/search?title=" + title + "&offset=" + offsetValue,
            dataType:"json",
            headers:{
            	"email":email,
            	"sessionId":sessionId
            },
            success: successHandler, // Bind event handler as a success callback
            error: errorHandler
        });
}

function basicSearchTitleSecond(){
	let title = $(".title").val();
	let email = getCookie("email");
	let sessionId = getCookie("sessionId");
	searchingMethod = "title";

	$.ajax({
            method: "GET", // Declare request type
            url: "http://andromeda-70.ics.uci.edu:6976/api/g/movies/search?title=" + title + "&offset=" + offsetValue,
            dataType:"json",
            headers:{
            	"email":email,
            	"sessionId":sessionId
            },
            success: successHandler, // Bind event handler as a success callback
            error: errorHandler
        });
}

function basicSearchGenre(){
	offsetValue = 0;
	let genre = $(".genre").val();
	let email = getCookie("email");
	let sessionId = getCookie("sessionId");
	searchingMethod = "genre";

	$.ajax({
            method: "GET", // Declare request type
            url: "http://andromeda-70.ics.uci.edu:6976/api/g/movies/search?genre=" + genre + "&offset=" + offsetValue,
            dataType:"json",
            headers:{
            	"email":email,
            	"sessionId":sessionId
            },
            success: successHandler, // Bind event handler as a success callback
            error: errorHandler
        });
}

function basicSearchGenreSecond(){
	let genre = $(".genre").val();
	let email = getCookie("email");
	let sessionId = getCookie("sessionId");
	searchingMethod = "genre";

	$.ajax({
            method: "GET", // Declare request type
            url: "http://andromeda-70.ics.uci.edu:6976/api/g/movies/search?genre=" + genre + "&offset=" + offsetValue,
            dataType:"json",
            headers:{
            	"email":email,
            	"sessionId":sessionId
            },
            success: successHandler, // Bind event handler as a success callback
            error: errorHandler
        });
}

function successHandler(res, textStatus, xhr){
	if (textStatus == "success"){
		alert(res.message);
		location.replace("index.html");
	}
	else{
		let movies = document.getElementById("movies");
		movies.innerHTML = "Searching.."
		let delay = parseInt(xhr.getResponseHeader("requestDelay"));
		let transactionId = xhr.getResponseHeader("transactionId");
		setTimeout(requestReport(transactionId), delay);
	}
}

function errorHandler(res, textStatus, xhr){
	alert("error occured when calling search.");
}

function requestReport(transactionId){
	$.ajax({
		method: "GET",
		url: "http://andromeda-70.ics.uci.edu:6976/api/g/report",
		dataType: "json",
		headers: {"transactionId":transactionId},
		contentType: "application/json",
		success: handleReportRequestSuccess,
		error: hangleReportRequestError
		});
}

function handleReportRequestSuccess(res, textStatus, xhr){
	if (xhr.status == 204){
		let delay = parseInt(xhr.getResponseHeader("requestDelay"));
		let transactionId = xhr.getResponseHeader("transactionId");
		setTimeout(requestReport(transactionId), delay);
	}
	else if (res.resultCode == 211){
		let sessionId = xhr.getResponseHeader("sessionId");
		let cookieString = "sessionId="+sessionId;
		document.cookie = cookieString;
		console.log(document.cookie);
		alert(res.message);
		let movies = document.getElementById("movies");
		movies.innerHTML = "";
	} else{
		let sessionId = xhr.getResponseHeader("sessionId");
		let cookieString = "sessionId="+sessionId;
		document.cookie = cookieString;
		console.log(document.cookie);
		let movies = document.getElementById("movies");
		movies.innerHTML = "";
		insertMovies(res.movies);
	}
}

function hangleReportRequestError(res, textStatus, xhr){
	alert("error occured when calling report.");
}

function insertMovies(movies){
	let movieDiv = document.getElementById("movies");
	movieDiv.innerHTML = "";

	let rowHTML = "<table align=\"center\" border=\"1\"><tr><td>Title</td><td>Director</td><td>Year</td><td>Rating</td><td>Votes</td><td>MovieID</td>";

	for (let i = 0; i < movies.length; ++i){
		let movie = movies[i];

		rowHTML += "<tr>";
		rowHTML += "<td>" + "<a href=\"#\" onclick=\"getDetail('" + movie["movieId"] + "')\">" + movie["title"] + "</a>" + "</td>";
		rowHTML += "<td>" + movie["director"] + "</td>";
		rowHTML += "<td>" + movie["year"] + "</td>";
		rowHTML += "<td>" + movie["rating"] + "</td>";
		rowHTML += "<td>" + movie["numVotes"] + "</td>";
		rowHTML += "<td>" + movie["movieId"] + "</td>";
		rowHTML += "</tr>";
	}
	rowHTML += "</table>";

	movieDiv.innerHTML = rowHTML;

	movieDiv.appendChild(document.createElement("br"));

	let previousBtn = document.createElement("BUTTON");
	previousBtn.setAttribute("style", "color:red; background-color:black");
	previousBtn.setAttribute("onclick", "previousPageCall()");
	previousBtn.innerHTML = "Previous";
	movieDiv.appendChild(previousBtn);

	let nextBtn = document.createElement("BUTTON");
	nextBtn.setAttribute("style", "color:red; background-color:black");
	nextBtn.setAttribute("onclick", "nextPageCall()");
	nextBtn.innerHTML = "Next";
	movieDiv.appendChild(nextBtn);
}

function previousPageCall(){
	if (offsetValue > 0){
		offsetValue -= 10;
		if (searchingMethod == "title"){
			basicSearchTitleSecond();
		} else if (searchingMethod == "genre"){
			basicSearchGenreSecond();
		}
	}
}

function nextPageCall(){
	offsetValue += 10;
	if (searchingMethod == "title"){
		basicSearchTitleSecond();
	} else if (searchingMethod == "genre"){
		basicSearchGenreSecond();
	}

}

function searchByTitle(){
	let div = document.getElementById("basicSearch");
	div.innerHTML = "";

	//append form
	let form = document.createElement("FORM");
	form.setAttribute("id", "titleSearch");
	form.setAttribute("action", "#");
	form.setAttribute("onsubmit", "basicSearchTitle(); return false");
	div.appendChild(form);

	//append input
	let input = document.createElement("INPUT");
	input.setAttribute("type", "text");
	input.setAttribute("class", "title");
	input.setAttribute("size", "50");
	input.setAttribute("placeholder", "title");
	form.appendChild(input);

	//append breakline 
	form.appendChild(document.createElement("br"));
	form.appendChild(document.createElement("br"));

	//append search button
	let button = document.createElement("BUTTON");
	button.setAttribute("type", "submit");
	button.setAttribute("style", "color:red; background-color:black");
	button.innerHTML = "Search";	
	form.appendChild(button);
}

function searchByGenre(){
	let div = document.getElementById("basicSearch");
	div.innerHTML = "";

	//append form
	let form = document.createElement("FORM");
	form.setAttribute("id", "genreSearch");
	form.setAttribute("action", "#");
	form.setAttribute("onsubmit", "basicSearchGenre(); return false");
	div.appendChild(form);

	//append input
	let input = document.createElement("INPUT");
	input.setAttribute("list", "genres");
	input.setAttribute("class", "genre");
	input.setAttribute("placeholder", "genre");
	input.setAttribute("size", "50")
	form.appendChild(input);

	//append datalist
	let datalist = document.createElement("DATALIST");
	datalist.setAttribute("id", "genres");
	form.appendChild(datalist);

	//append option to datalist
	let list = document.getElementById("genres");
	let genreArray = ["Action", "Adult", "Adventure", "Animation", "Biography", "Comedy", "Crime", 
						"Documentary", "Drama", "Family", "Fantasy", "History", "Horror", "Music",
						"Musical", "Mystery", "Reality-TV", "Romance", "Sci-Fi", "Sport", "Thriller", "War", "Western"];
	for (let i = 0; i < genreArray.length; ++i){
		let option = document.createElement("OPTION");
		option.setAttribute("value", genreArray[i]);
		list.appendChild(option);
	}
	
	//append breakline
	form.appendChild(document.createElement("br"));
	form.appendChild(document.createElement("br"));


	//append button
	let button = document.createElement("BUTTON");
	button.setAttribute("type", "submit");
	button.setAttribute("style", "color:red; background-color:black");
	button.innerHTML = "Search";	
	form.appendChild(button);
}









