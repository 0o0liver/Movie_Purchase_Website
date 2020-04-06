function handleOGrequestSuccess(res, textStatus, xhr){
	let logingIn = $('.logingIn');
	logingIn.append("Loging in...");
	let delay = parseInt(xhr.getResponseHeader("requestDelay"));
	let transactionId = xhr.getResponseHeader("transactionId");
	setTimeout(requestReport(transactionId), delay);
}

function handleOGrequestError(res, textStatus, xhr){
	console.log("error");
	alert(res.message);
}

// Report request handler
function handleReportRequestSuccess(res, textStatus, xhr){
	if (xhr.status == 204){
		let delay = parseInt(xhr.getResponseHeader("requestDelay"));
		let transactionId = xhr.getResponseHeader("transactionId");
		setTimeout(requestReport(transactionId), delay);
	} else if (res.resultCode != 120){
		let logingIn = $('.logingIn');
		logingIn.empty();
		alert(res.message);
	} else {
		let cookieString = "sessionId="+res.sessionID;
		document.cookie = cookieString;
		console.log(document.cookie);
		//debugger;
		location.replace("homePage.html");
	}
}

function hangleReportRequestError(res, textStatus, xhr){
	console.log("error");
	alert(res.message);
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

$("form").submit(function (event) {
	event.preventDefault();
	let email = $(".email").val();
	let password = $(".password").val();
	let arrayPass = password.split("");
	let arrayPassWithQuote = "\"" + arrayPass.join("\",\"") + "\"";
	let send = '{"email":"' + email + '", "password":[' + arrayPassWithQuote + ']}';
	document.cookie = "email="+email;

	$.ajax({
		method: "POST",
		url: "http://andromeda-70.ics.uci.edu:6976/api/g/idm/login",
		contentType: "application/json",
		data: send,
		dataType: "json",
		async:false,
		success: handleOGrequestSuccess,
		error: handleOGrequestError
		});

	}
);