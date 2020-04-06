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

$("form").submit(
	function(event) {
		event.preventDefault();

		let firstname = $(".firstname").val();
		let lastname = $(".lastname").val();
		let address = $(".address").val();

		send = "{\"email\":\"" + email + "\", \"firstName\":\"" + firstname + "\", \"lastName\":\"" + lastname + "\", \"ccId\":\"0103935954019747504\", \"address\":\"" + address + "\"}";

		console.log(send);

		$.ajax({
			method: "POST",
			url: "http://andromeda-70.ics.uci.edu:6976/api/g/billing/customer/insert",
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
);

function handleOGrequestSuccess(res, textStatus, xhr){
	if (textStatus == "success"){
		alert(res.message);
		location.replace("index.html");
	}
	else{
		let loadingDiv = document.getElementById("loading");
		loadingDiv.innerHTML = "Registering customer ...";
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
	}else if (res.resultCode == 333){
		let sessionId = xhr.getResponseHeader("sessionId");
		let cookieString = "sessionId="+sessionId;
		document.cookie = cookieString;
		console.log(document.cookie);
		let loading = document.getElementById("loading");
		loading.innerHTML = "";
		let btnDiv = document.getElementById("placeOrder")
		btnDiv.innerHTML = "<button style=\"color:red; background-color:black\" onclick=\"window.location.href='placeOrder.html'\">Place Order via PayPal</button>"
		alert("Customer already exist. Go ahead and place order.")
	} else if (res.resultCode == 3300){
		let sessionId = xhr.getResponseHeader("sessionId");
		let cookieString = "sessionId="+sessionId;
		document.cookie = cookieString;
		console.log(document.cookie);
		let loadingDiv = document.getElementById("loading");
		loadingDiv.innerHTML = "";
		let btnDiv = document.getElementById("placeOrder")
		btnDiv.innerHTML = "<button style=\"color:red; background-color:black\" onclick=\"window.location.href='placeOrder.html'\">Place Order via PayPal</button>"
		alert("Customer registered. Go ahead and place order.");
	}
}


function hangleReportRequestError(res, textStatus, xhr){
	alert("error occured when calling report.");
}



























