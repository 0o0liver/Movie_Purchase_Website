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

send = "{\"email\":\"" + email + "\"}"

$.ajax({
	method: "POST",
	url: "http://andromeda-70.ics.uci.edu:6976/api/g/billing/order/retrieve",
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

function handleOGrequestSuccess(res, textStatus, xhr){
	if (textStatus == "success"){
		alert(res.message);
		location.replace("index.html");
	}
	else{
		let loadingDiv = document.getElementById("loading");
		loadingDiv.innerHTML = "Loading history ...";
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
	} else{
		let sessionId = xhr.getResponseHeader("sessionId");
		let cookieString = "sessionId="+sessionId;
		document.cookie = cookieString;
		console.log(document.cookie);
		let loadingDiv = document.getElementById("loading");
		loadingDiv.innerHTML = "";
		let historyDiv = document.getElementById("history");
		historyDiv.innerHTML = "";
		insertHistory(res.transactions);
	}
}


function hangleReportRequestError(res, textStatus, xhr){
	alert("error occured when calling report.");
}


function insertHistory(transactions){
	console.log(transactions);

	let historyDiv = document.getElementById("history");
	historyDiv.innerHTML = "";

	let rowHTML = "<table align=\"center\" border=\"1\"><tr><td>MovieID</td><td>Quantity</td><td>Unit Price</td><td>Discount</td><td>SaleDate</td>";

	for (let i = 0; i < transactions.length; ++i){
		let transactionItems = transactions[i]["items"];
		for (let j = 0; j < transactionItems.length; ++j){
			rowHTML += "<tr>";
			rowHTML += "<td>" + transactionItems[j]["movieId"] + "</td>";
			rowHTML += "<td>" + transactionItems[j]["quantity"] + "</td>";
			rowHTML += "<td>" + transactionItems[j]["unit_price"] + "</td>";
			rowHTML += "<td>" + transactionItems[j]["discount"] + "</td>";
			rowHTML += "<td>" + transactionItems[j]["saleDate"] + "</td>";
			rowHTML += "</tr>";
		}
	}
	rowHTML += "</table>";
	historyDiv.innerHTML += rowHTML; 
}




















