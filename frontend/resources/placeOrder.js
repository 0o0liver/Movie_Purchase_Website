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
	url: "http://andromeda-70.ics.uci.edu:6976/api/g/billing/order/place",
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
	}else if (res.resultCode == 341){
		let sessionId = xhr.getResponseHeader("sessionId");
		let cookieString = "sessionId="+sessionId;
		document.cookie = cookieString;
		console.log(document.cookie);
		alert(res.message);
		location.replace("homePage.html");
	} else if (res.resultCode == 3400){
		let sessionId = xhr.getResponseHeader("sessionId");
		let cookieString = "sessionId="+sessionId;
		document.cookie = cookieString;
		console.log(document.cookie);
		let paypalWindow = window.open(res.redirectURL);
		paypalWindow.onunload = function(){location.replace("paymentSuccess.html");};
	}
}


function hangleReportRequestError(res, textStatus, xhr){
	alert("error occured when calling report.");
}












