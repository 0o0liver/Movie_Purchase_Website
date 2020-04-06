function getCookie(name){
	let cookieList = document.cookie.split("; ")
	for (let i = 0; i < cookieList.length; ++i){
		let elementList = cookieList[i].split("=");
		if (elementList[0] == name){
			return elementList[1];
		}
	}
}

email = getCookie("email");
sessionId = getCookie("sessionId");

function generateURL(title, genre, year, director, orderby, direction, limit, offset){
	let URL = "http://andromeda-70.ics.uci.edu:6976/api/g/movies/search?offset=" + offset + "&orderby=" + orderby + "&direction=" + direction + "&limit=" + limit;

	if (title != ""){
		URL += "&title=" + title;
	}
	if (genre != ""){
		URL += "&genre=" + genre;
	}
	if (year != ""){
		URL += "&year=" + year;
	}
	if (director != ""){
		URL += "&director=" + director;
	}

	return URL;
}

var globalTitle;
var globalGenre;
var globalYear;
var globalDirector;
var globalorderby;
var globalDirection;
var globalLimit;
var globalOffset;

$("form").submit(function (event) {
	event.preventDefault();

	let title = $(".title").val();
	globalTitle = title;

	let genre = $(".genre").val();
	globalGenre = genre;

	let year = $(".year").val();
	globalYear = year;

	let director = $(".director").val();
	globalDirector = director;

	let orderby = $(".orderby").val();
	globalorderby = orderby;

	let direction = $(".direction").val();
	globalDirection = direction;

	let limit = $(".limit").val();
	globalLimit = limit;

	let offset = 0;
	globalOffset = offset;

	let URL = generateURL(title, genre, year, director, orderby, direction, limit, offset);

	console.log(URL);

	$.ajax({
            method: "GET", // Declare request type
            url: URL,
            dataType:"json",
            headers:{
            	"email":email,
            	"sessionId":sessionId
            },
            success: successHandler, // Bind event handler as a success callback
            error: errorHandler
        });
	}
);

function search(URL){

	console.log(URL);

	$.ajax({
            method: "GET", // Declare request type
            url: URL,
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
	if (globalOffset > 0){
		globalOffset -= parseInt(globalLimit);
		let newURL = generateURL(globalTitle, globalGenre, globalYear, globalDirector, globalorderby, globalDirection, globalLimit, globalOffset);
		search(newURL);
	}
}

function nextPageCall(){
	globalOffset += parseInt(globalLimit);
	let newURL = generateURL(globalTitle, globalGenre, globalYear, globalDirector, globalorderby, globalDirection, globalLimit, globalOffset);
	search(newURL);
}

function getDetail(movieId){
	console.log(movieId);
	localStorage.setItem("clickedMovieId", movieId);
	location.replace("movieDetailPage.html");
}










