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

send = '{"email":"' + email + '"}';

$.ajax({
	method: "POST",
	url: "http://andromeda-70.ics.uci.edu:6976/api/g/billing/cart/retrieve",
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

function retrieve(){
	send = '{"email":"' + email + '"}';

	$.ajax({
		method: "POST",
		url: "http://andromeda-70.ics.uci.edu:6976/api/g/billing/cart/retrieve",
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
		let loadingDiv = document.getElementById("loading");
		loadingDiv.innerHTML = "Loading Shopping Cart item ...";
		let delay = parseInt(xhr.getResponseHeader("requestDelay"));
		let transactionId = xhr.getResponseHeader("transactionId");
		setTimeout(requestReport(transactionId), delay);
	}
}

function handleOGrequestError(res, textStatus, xhr){
	alert("error occured when retrieving a shopping cart.");
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
	}else if (res.resultCode == 312){
		let sessionId = xhr.getResponseHeader("sessionId");
		let cookieString = "sessionId="+sessionId;
		document.cookie = cookieString;
		console.log(document.cookie);
		let itemDiv = document.getElementById("items");
		items.innerHTML = "";
		let loadingDiv = document.getElementById("loading");
		loadingDiv.innerHTML = "Shopping Cart Is Empty.";
	} else if (res.resultCode == 3130){
		let sessionId = xhr.getResponseHeader("sessionId");
		let cookieString = "sessionId="+sessionId;
		document.cookie = cookieString;
		console.log(document.cookie);
		let loadingDiv = document.getElementById("loading");
		loadingDiv.innerHTML = "";
		insertItem(res.items);
	} else if (res.resultCode == 3110 || res.resultCode == 3120 || res.resultCode == 3140){
		let sessionId = xhr.getResponseHeader("sessionId");
		let cookieString = "sessionId="+sessionId;
		document.cookie = cookieString;
		console.log(document.cookie);
		let loadingDiv = document.getElementById("loading");
		loadingDiv.innerHTML = "";
		retrieve();
	}
}

function hangleReportRequestError(res, textStatus, xhr){
	alert("error occured when calling report.");
}

function insertItem(items){
	let itemDiv = document.getElementById("items");
	itemDiv.innerHTML = "Items: <br><br>";

	let rowHTML = "<table align=\"center\" border=\"1\"><tr><td>MovieID</td><td>Quantity</td><td>Update</td><td>Delete</td>";

	for (let i = 0; i < items.length; ++i){
		rowHTML += "<tr>";
		rowHTML += "<td>" + items[i]["movieId"] + "</td>";
		rowHTML += "<td>" + items[i]["quantity"] + "</td>";
		rowHTML += "<td><form action=\"#\"><input type=\"number\" min=\"1\" class=\"" + items[i]["movieId"] + "\" value=\"" + items[i]["quantity"] + "\"><button style=\"color:red; background-color:black;\" onclick=\"updateQuantity('" + items[i]["movieId"] + "')\">update</button></form></td>";
		rowHTML += "<td><button style=\"color:red; background-color:black;\" onclick=deleteItem('" + items[i]["movieId"] + "')>delete</button></td>"
		rowHTML += "</tr>";
	}
	rowHTML += "</table>";
	itemDiv.innerHTML += rowHTML;
}

function updateQuantity(movieId){
	let quantityClass = document.getElementsByClassName(movieId);
	let newQuantity = quantityClass[0].value;

	send = "{\"email\":\"" + email +"\", \"movieId\":\"" + movieId + "\", \"quantity\":" + newQuantity + "}";
	console.log(send);

	$.ajax({
		method: "POST",
		url: "http://andromeda-70.ics.uci.edu:6976/api/g/billing/cart/update",
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

function deleteItem(movieId){

	send = "{\"email\":\"" + email +"\", \"movieId\":\"" + movieId + "\"}";
	console.log(send);

	$.ajax({
		method: "POST",
		url: "http://andromeda-70.ics.uci.edu:6976/api/g/billing/cart/delete",
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

function clearCart(){

	send = "{\"email\":\"" + email +"\"}";
	console.log(send);

	$.ajax({
		method: "POST",
		url: "http://andromeda-70.ics.uci.edu:6976/api/g/billing/cart/clear",
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









