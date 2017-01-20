$(document).ready(function() {

	$(".kontakthilfe-login button").click(function() {
		console.log("clicked");
		var step = $(this).attr("rel");
		var userId, pass;

		if ("undefined" != typeof step) {
			$(".login").hide();
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

				$(".login.step1").show();


			}

		}
	});

});

function makeAjaxToServer(userId, pass) {

	// make the AJAX request, dataType is set to json
	$.ajax({
		url : '/bin/audi/authentication',
		data : 'userId=' + userId + '&password=' + pass,
		dataType : "json",

		// if received a response from the server
		success : function(data) {
			if (data.user) {
				console.log('success data', data);
				$(".eingeloggter_user").text(data.user);
				$(".login.step3").show();
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
