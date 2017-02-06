$(document).ready(function() {
    var cookie_data= JSON.parse(readCookie('loginD'));
    if(cookie_data != null){
        $(".login.step1").hide();
        $(".eingeloggter_user").text(cookie_data[1].name);
		$(".login.step3").show();
	    $("#loginLabel").hide();
	    $("#userLabel").find("a").text(cookie_data[1].name);
	    $("#userLabel").show();
    }
	$(".kontakthilfe-login button").click(function() {
		console.log("clicked");
		var step = $(this).attr("rel");
		var userId, pass;
		if ("undefined" != typeof step) {
			$(".loginsteps").hide();
			if (step == "loginStep1") {
				$(".login.step2").show();
			} else if (step == "loginStep2") {
				var check = ValidateEmail($("#email").val());
				if (check) {
					userId = $("#email").val();
					pass = $("#password").val()

					makeAjaxToServer(userId, pass);
				}else{
					$(".login.step2").show();
					$("#invalid_email").show();
				}
			} else if (step == "loginStep3") {
                eraseCookie("loginD","",-1);
                eraseCookie("analyticsData","",-1);
				$(".login.step1").show();
				$("#userLabel").find("a").text();
				$("#userLabel").hide();
				$("#loginLabel").show();

			}
		}
	});

});
function readCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for(var i=0;i < ca.length;i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
    }
    return null;
}
function eraseCookie(name) {
    createCookie(name,"",-1);
}
function createCookie(name,value,days) {
    if (days) {
        var date = new Date();
        date.setTime(date.getTime()+(days*24*60*60*1000));
        var expires = "; expires="+date.toGMTString();
    }
    else var expires = "";
    document.cookie = name+"="+value+expires+"; path=/";
}
function makeAjaxToServer(userId, pass) {

	// make the AJAX request, dataType is set to json
	$.ajax({
		url : '/bin/audi/authentication',
		data : 'userId=' + userId + '&password=' + pass,
		dataType : "json",

		// if received a response from the server
		success : function(data) {
			$("#invalid_error").hide();
			if (data.user) {
				console.log('success data', data);
				$(".eingeloggter_user").text(data.user);
				$(".login.step3").show();
				$("#loginLabel").hide();
				$("#userLabel").find("a").text(data.user);
				$("#userLabel").show();
				var userFunctions = data.userFunctions.split(",");
				for(var i=0; i<userFunctions.length; i++){
					if(userFunctions[i] && userFunctions[i] != ""){
					$("."+userFunctions[i]).show();
					$("#"+userFunctions[i]).prop("checked", true);
					}
				}
				var loginD = [
				              { 'email' : userId},
				              { 'name' : data.user }
				             ];
				document.cookie = "loginD"+ "="+ JSON.stringify(loginD) + ";path=/";

			} else {
				$(".login.step2").show();
				$("#invalid_error").show();
			}
		},

		// If there was no resonse from the server
		// #errorMsg is assumption. Need clarification on this
		error : function(textStatus) {
			console.log("Internal API Error " + textStatus);
			$(".login.step2").show();
			$("#invalid_error").hide();
		}
	});
}

function ValidateEmail(email) {
	var expr = /^([\w-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([\w-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\]?)$/;
	return expr.test(email);
};

