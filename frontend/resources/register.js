// Original request handler
function handleOGrequestSuccess(res, textStatus, xhr){
	let registering = $('.registering');
	registering.append("Registering...");
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
	} else {
		let registering = $('.registering');
		registering.empty();
		alert(res.message);
	}
}

function hangleReportRequestError(res, textStatus, xhr){
	console.log("error");
	alert(res.message);
}

// Sendng report request to api gatewat
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

// Send original request to api gateway
$("form").submit(function (event) {
	event.preventDefault();
	let email = $(".email").val();
	let password = $(".password").val();
	let arrayPass = password.split("");
	let arrayPassWithQuote = "\"" + arrayPass.join("\",\"") + "\"";
	let send = '{"email":"' + email + '", "password":[' + arrayPassWithQuote + ']}';

	$.ajax({
		method: "POST",
		url: "http://andromeda-70.ics.uci.edu:6976/api/g/idm/register",
		contentType: "application/json",
		data: send,
		dataType: "json",
		success: handleOGrequestSuccess,
		error: handleOGrequestError
		});
	}
);
