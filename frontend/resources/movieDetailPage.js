let movieId = localStorage.getItem("clickedMovieId");

function getCookie(name){
	let cookieList = document.cookie.split("; ")
	for (let i = 0; i < cookieList.length; ++i){
		let elementList = cookieList[i].split("=");
		if (elementList[0] == name){
			return elementList[1];
		}
	}
}

let email = getCookie("email");
let sessionId = getCookie("sessionId");

$.ajax({
	method: "GET", // Declare request type
    url: "http://andromeda-70.ics.uci.edu:6976/api/g/movies/get/" + movieId,
    dataType:"json",
    headers:{
    	"email":email,
     	"sessionId":sessionId
    },
    success: successHandler, // Bind event handler as a success callback
    error: errorHandler
});

function successHandler(res, textStatus, xhr){
	if (textStatus == "success"){
		alert(res.message);
		location.replace("index.html");
	}
	else{
		let loadingDiv = document.getElementById("loading");
		loadingDiv.innerHTML = "Loading page ...";
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
	}else if (res.resultCode == 211 || res.resultCode == 141){
		let sessionId = xhr.getResponseHeader("sessionId");
		let cookieString = "sessionId="+sessionId;
		document.cookie = cookieString;
		console.log(document.cookie);
		alert(res.message);
	} else{
		let sessionId = xhr.getResponseHeader("sessionId");
		let cookieString = "sessionId="+sessionId;
		document.cookie = cookieString;
		console.log(document.cookie);
		fillPage(res.movie);
	}
}

function hangleReportRequestError(res, textStatus, xhr){
	alert("error occured when calling report.");
}

function fillPage(movieInfo){
	let loadingDiv = document.getElementById("loading");
	loadingDiv.innerHTML = "";

	// fill title
	let titleDiv = document.getElementById("title");
	titleDiv.innerHTML = "Title: " + movieInfo["title"] + "<br><br>";

	// fill movieId
	let movieIdDiv = document.getElementById("movieId");
	movieIdDiv.innerHTML = "MoiveID: " + movieInfo["movieId"] + "<br><br>";

	// fill year
	let yearDiv = document.getElementById("year");
	yearDiv.innerHTML = "Year: " + movieInfo["year"] + "<br><br>";

	// fill director
	let directorDiv = document.getElementById("director");
	directorDiv.innerHTML = "Director: " + movieInfo["director"] + "<br><br>";

	// fill overview
	let overviewDiv = document.getElementById("overview");
	if (movieInfo["overview"] == null || movieInfo["overview"] == undefined){
		;
	} else {
		overviewDiv.innerHTML = "Overview: " + movieInfo["overview"] + "<br><br>";
	}

	// fill rating
	let ratingDiv = document.getElementById("rating");
	ratingDiv.innerHTML = "Rating: " + movieInfo["rating"] + "<br><br>";

	// fill numVote
	let numVoteDiv = document.getElementById("numVote");
	numVoteDiv.innerHTML = "Votes: " + movieInfo["numVotes"] + "<br><br>";

	// fill genre
	let genreDiv = document.getElementById("genre");
	if (movieInfo["genres"].length != 0){
		genreDiv.innerHTML = "Genre: * "
		for (let i = 0; i < movieInfo["genres"].length; ++i){
			let genre = movieInfo["genres"][i];
			genreDiv.innerHTML += genre["name"] + " * ";
		}
		genreDiv.innerHTML += "<br><br>";
	}

	// fill star
	let starDiv = document.getElementById("stars");
	if (movieInfo["stars"].length != 0){
		starDiv.innerHTML = "Stars: * ";
		for (let i = 0; i < movieInfo["stars"].length; ++i){
			let star = movieInfo["stars"][i];
			starDiv.innerHTML += star["name"] + " * ";
		}
		starDiv.innerHTML += "<br><br>";
	}
}

function insertMovie(){
	let email = getCookie("email");
	let sessionId = getCookie("sessionId");

	let quantity = document.getElementById("quantity");

	console.log("quantity: " + quantity.value);

	let send = '{"email":"' + email + '", "movieId":"' + movieId + '", "quantity":' + quantity.value + '}'; 
	console.log(send);

	$.ajax({
		method: "POST",
		url: "http://andromeda-70.ics.uci.edu:6976/api/g/billing/cart/insert",
		contentType: "application/json",
		headers:{
			"email":email,
			"sessionId":sessionId
		},
		data: send,
		dataType: "json",
		success: handleOGrequestSuccess,
		error: handleOGrequestError
	});
}

function handleOGrequestSuccess(res, textStatus, xhr){
	if (textStatus == "success"){
		alert(res.message);
		location.replace("index.html");
	}
	else{
		let addingDiv = document.getElementById("adding");
		addingDiv.innerHTML = "Adding To Cart ...";
		let delay = parseInt(xhr.getResponseHeader("requestDelay"));
		let transactionId = xhr.getResponseHeader("transactionId");
		setTimeout(requestReportCart(transactionId), delay);
	}
}

function handleOGrequestError(res, textStatus, xhr){
	alert("error occured when inserting movies");
}

function requestReportCart(transactionId){
	$.ajax({
		method: "GET",
		url: "http://andromeda-70.ics.uci.edu:6976/api/g/report",
		dataType: "json",
		headers: {"transactionId":transactionId},
		contentType: "application/json",
		success: handleReportRequestSuccessCart,
		error: hangleReportRequestErrorCart
	});
}

function handleReportRequestSuccessCart(res, textStatus, xhr){
	if (xhr.status == 204){
		let delay = parseInt(xhr.getResponseHeader("requestDelay"));
		let transactionId = xhr.getResponseHeader("transactionId");
		setTimeout(requestReportCart(transactionId), delay);
	}else {
		let sessionId = xhr.getResponseHeader("sessionId");
		let cookieString = "sessionId="+sessionId;
		document.cookie = cookieString;
		console.log(document.cookie);
		let addingDiv = document.getElementById("adding");
		addingDiv.innerHTML = "";
		alert(res.message);
	}
}

function hangleReportRequestErrorCart(res, textStatus, xhr){
	alert("error occured when calling report.");
}






