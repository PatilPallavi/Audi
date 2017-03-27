var mlparams = "";
var mluserId = "";

$(document).ready(function() {
	var pstring='';
    for (key in sessionStorage) {
        if(key.indexOf('__nemo__a6limo') != -1){
            pstring = jQuery.parseJSON(sessionStorage.getItem(key));
            var date = new Date();
            date.setTime(date.getTime()+(5*24*60*60*1000));
            var expires = "; expires="+date.toGMTString();
            document.cookie = "prString"+"="+jQuery.parseJSON(sessionStorage.getItem(key)).prString+expires+"; path=/";
        }
    }

    $("#config_select_link").click(function(e) {
         e.preventDefault();
         window.location.href="/content/audi/a6_configurator.html?wcmmode=disabled";
    });
    $("a.carLinkTypes").click(function() {
         $(".stageArea-content .container").css("position","");
         $(".stageArea-content .container").css("padding-top","0px");
         $(".stageArea-content .container").css("left","0");
    });
    
    getPersonalizedContent();
    
    var cookie_data= JSON.parse(readCookie('loginD'));
    var usr_data= JSON.parse(readCookie('userProfile'));
    if(cookie_data != null){
        $(".login.step1").hide();
        $(".eingeloggter_user").text(cookie_data[1].name);
		$(".login.step3").show();
	    $("#loginLabel").hide();
        $("#logout-link").show();
	    $("#userLabel").find("a").text(cookie_data[1].name);
	    $("#userLabel").show();
        getPersonalizedContent(true,usr_data.userId);
    }
    $("#loginStep3").click(function() {
        eraseCookie("loginD","",-1);
        eraseCookie("userProfile","",-1);
        eraseCookie("audicode","",-1);
        eraseCookie("prString","",-1);
        $(".step3 ").hide();
        $(".btn-mieten").show();
        $(".btn-machine-learning").show();
        $(".btn-machine-learning").hide();
		$(".login.step1").show();
		$("#userLabel").find("a").text();
        $("#userLabel").hide();
        $("#loginLabel").show();
        $("#logout-link").hide();
        $(".btn-konfigurieren").removeClass(".btn-machine-learning").css({"background-color":"transparent","border":"1px solid #fff"}).html('Konfigurieren'); 
        $(".btn-mehr-erfahren").removeClass(".btn-machine-learning").removeClass("btn-informieren").css({"background-color":"transparent","border":"1px solid #fff","color":"white"}).html('Mehr erhafern');
        $(".btn-informieren").removeClass(".btn-machine-learning").css({"background-color":"transparent","color":"white"}).html('Informieren');
        $("#owner_accesories").hide();
        $("#audi_original_reifen").hide();
        $(".content").css("display","block");
        $(".stageArea-content").removeClass("pull-right-with-text");
        getPersonalizedContent(false, false);
        $(".stageArea-copy").html("<h2>Es gibt nur einen<br>Weg: Voraus.</h2><p>Die neuen A3 Modelle. Jetzt bei Ihrem Audi Partner.</p>");
        $(".stageArea-image").find("img:eq(0)").attr("src",'/content/dam/audi/images/desktop/backgroundimages/default.jpg');
    });
	$(".kontakthilfe-login button").click(function() {
		var step = $(this).attr("rel");
		var userId, pass;
		if ("undefined" != typeof step) {
			$(".loginsteps").hide();
			if (step == "loginStep1") {
				$(".login.step2").show();
			} else if (step == "loginStep2") {
				mluserId = "";
				mlparams = "";
				var check = ValidateEmail($("#email").val());
				if (check) {
					userId = $("#email").val();
					pass = $("#password").val()
					if (userId.indexOf("Machine") >= 0) {
						getMLParamsForUser(userId);
						/*$.when(getMLParamsForUser(userId)).then(getMLRecommendation(mlparams)).then(writeMLRecommendationInJCR(recommendation)).done(function(){
							makeAjaxToServer(userId, pass);
						});*/
						makeAjaxToServer(userId, pass);
					} else {
						makeAjaxToServer(userId, pass);
					}
				}else{
					$(".login.step2").show();
					$("#invalid_email").show();
				}
			} else if (step == "loginStep3") {
                var cookie_data= JSON.parse(readCookie('loginD'));
                eraseCookie("loginD","",-1);
                eraseCookie("audicode","",-1);
                eraseCookie("prString","",-1);
                eraseCookie("userProfile","",-1);
                $(".btn-mieten").show();
                $(".btn-machine-learning").show();
                $(".btn-machine-learning").hide();
				$(".login.step1").show();
				$("#userLabel").find("a").text();
				$("#userLabel").hide();
				$("#loginLabel").show();
                $("#logout-link").hide();
                $(".btn-konfigurieren").removeClass(".btn-machine-learning").css({"background-color":"transparent","border":"1px solid #fff"}).html('Konfigurieren'); 
        		$(".btn-mehr-erfahren").removeClass(".btn-machine-learning").removeClass("btn-informieren").css({"background-color":"transparent","border":"1px solid #fff","color":"white"}).html('Mehr erhafern');
        		$(".btn-informieren").removeClass(".btn-machine-learning").css({"background-color":"transparent","color":"white"}).html('Informieren');
                $("#owner_accesories").hide();
                $("#audi_original_reifen").hide();
                $(".content").css("display","block");
                $(".stageArea-content").removeClass("pull-right-with-text");
                getPersonalizedContent(false,false);
                $(".stageArea-copy").html("<h2>Es gibt nur einen<br>Weg: Voraus.</h2><p>Die neuen A3 Modelle. Jetzt bei Ihrem Audi Partner.</p>");
               $(".stageArea-image").find("img:eq(0)").attr("src",'/content/dam/audi/images/desktop/backgroundimages/default.jpg');
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
    var expires = "";
    if (days) {
        var expiryDate = new Date();
        expiryDate.setTime(expiryDate.getTime() - 86400 * 1000);
        expires = "; expires="+expiryDate.toGMTString()+';max-age=0';
    }else {
        expires = "";
    }
    document.cookie = name+"="+value+expires+"; path=/";
}
function getPersonalizedContent(status,user){
    $( ".modelselector" ).find(".jumbotron").show();
	$( ".modelselector" ).find(".ownr_slider_section").hide();
    if(status){
		var data= JSON.parse(readCookie('userProfile'));
        var display_layout = 0;

        if(data.stored_settings.configured_cars == "" && user == 'audiconfiguser'){
            var prString = readCookie('prString');
            if(data.stored_settings.pString == "" && prString !== null){

                $.getJSON('/bin/audi/saveAudiCode?userId='+data.userId+'&pString='+prString, function(data) {});

            }
            if(data.stored_settings.pString != undefined && data.stored_settings.pString != "" ){
                 if(prString !== undefined){
                        var url = "https://www.audi.de/bin/dpu-de/configuration?context=nemo-de%3Ade&ids="+ encodeURIComponent(prString);
                        $.getJSON( url, function( data ) {});
                 }
				display_layout = 2;

            }

        }else{
            var a_code = JSON.parse(readCookie('audicode'));
			if(data.stored_settings.configured_cars != "" && user == 'audiconfiguser'){
                var prString = readCookie('prString');
                if(prString !== undefined){
                       var url = "https://www.audi.de/bin/dpu-de/configuration?context=nemo-de%3Ade&ids="+ encodeURIComponent(prString);
                       $.getJSON( url, function( data ) {});
                }
            	display_layout = 1;
            }
       }

        if(display_layout == 1){
            $(".content").css( {"margin-top":"75px!important","margin-bottom":"30px"});
            $(".stageArea-content .container").css("position","absolute");
            $(".stageArea-content .container").css("left","15%");
            $(".stageArea-content").addClass("pull-right-with-text");
 			$("#owner_config").hide();
            $("#config-section").show();
            $("#jumbo-section").hide();
            $("#intro-section").show();
            $("#footer_image").show();
            $("#zentrum_handburg").show();
            $("#personalized_content").show();
    		$("#default_content").hide();
        	$("#loginmodule").hide();
            $(".slider-container .config-select li").css("margin","0 150px");

            $(".slider-container").css("background-color","#e5e5e5");
            $(".slider-container").css("position","none");
            $(".stageArea-image").find("img:eq(0)").attr("src",'/content/dam/audi/images/configurator/1400x438_AA6_L_141006_2.jpg');
			var strVar="";
            strVar += "<div class=\"container\" style=\"padding-top: 0px; left: 0px;\">";
            strVar += "                        <div class=\"row\">";
            strVar += "                            <div class=\"col-lg-6\">";
            strVar += "                                <div class=\"stageArea-copy\"><h2>A6 Limousine<\/h2><p>Faszination in vielen Facetten.<\/p><\/div>";
            strVar += "                            <\/div>";
            strVar += "                        <\/div>";
            strVar += "                        <div class=\"row hidden-xs carChoose\" style=\"display: block;\">";
            strVar += "                            <div class=\"col-xs-12\">";
            strVar += "                                <button class=\"btn-informieren\" rel=\"A6 Limousine\">Informieren<\/button>";
            strVar += "                                <button class=\"btn-machine-learning\" style=\"display:none;\"><\/button>";
            strVar += "                                <button class=\"btn-konfigurieren\" rel=\"A6 Limousine\">Konfigurieren<\/button>";
            strVar += "                                <button class=\"btn-mieten\" rel=\"A6 Limousine\">Mieten<\/button>";
            strVar += "                            <\/div>";
            strVar += "                        <\/div>";
            strVar += "                        <div class=\"row hidden-xs carChoose stageArea-neu-gebrauchtwagen\" style=\"display: block;\">";
            strVar += "                            <div class=\"col-xs-12\">";
            strVar += "                                <a href=\"#\"><span id=\"nw\" class=\"bignumber\">40<\/span> Neuwagen &gt;<\/a>";
            strVar += "                                <a href=\"#\"><span id=\"gw\" class=\"bignumber\">1416<\/span> Gebrauchtwagen &gt;<\/a>";
            strVar += "                            <\/div>";
            strVar += "                        <\/div>";
            strVar += "                        <div class=\"row hidden-xs startCarChoose\" style=\"display: none;\">";
            strVar += "                            <div class=\"col-xs-12\">";
            strVar += "                                <button class=\"btn-mehr-erfahren\">Mehr erfahren<\/button>";
            strVar += "                                <button class=\"btn-machine-learning\" style=\"display:none\"><\/button>";
            strVar += "                                <button class=\"btn-konfigurieren\" rel=\"A6 Limousine\">Konfigurieren<\/button>";
            strVar += "                            <\/div>";
            strVar += "                        <\/div>";
            strVar += "                        <!--<div class=\"row hidden-sm hidden-md hidden-lg startCarChoose mobileStage\">";
            strVar += "                            <button class=\"btn-mehr-erfahren\">Mehr erfahren<\/button>";
            strVar += "                        <\/div>";
            strVar += "                        <div class=\"row hidden-sm hidden-md hidden-lg carChoose mobileStage\">";
            strVar += "                            <button class=\"btn-informieren\">Informieren<\/button>";
            strVar += "                            <button class=\"btn-konfigurieren\">Konfigurieren<\/button>";
            strVar += "                        <\/div>-->";
            strVar += "                        <form id=\"form_modell\" method=\"GET\" action=\"model.html\">";
            strVar += "                            <input id=\"input_modell\" type=\"hidden\" name=\"modell\" value=\"\">";
            strVar += "                        <\/form>";
            strVar += "                        <form id=\"form_service\" method=\"GET\" action=\"audiservice.html\">";
            strVar += "                            <input id=\"input_service\" type=\"hidden\" name=\"modell\" value=\"\">";
            strVar += "                        <\/form>";
            strVar += "                    <\/div>";
            $(".stageArea-content .container").html(strVar);

        }
        if(display_layout == 2){
            $(".content").css( {"margin-top":"75px!important","margin-bottom":"30px"});
            $(".stageArea-content .container").css("position","absolute");
            $(".stageArea-content .container").css("left","15%");
            $(".stageArea-content").addClass("pull-right-with-text");

            $("#owner_config").hide();
			$("#config-section").hide();
            $("#jumbo-section").show();
            $("#intro-section").show();
            $("#footer_image").show();
            $("#zentrum_handburg").show();
            $("#personalized_content").show();
    		$("#default_content").hide();
        	$("#loginmodule").hide();
            $(".slider-container").css("background-color","#e5e5e5");
            $(".slider-container").css("position","none");
            $(".slider-container .config-select li").css("margin","0 150px");
            $(".stageArea-image").find("img:eq(0)").attr("src",'/content/dam/audi/images/configurator/1400x438_AA6_L_141006_2.jpg');
            var strVar="";
            strVar += "<div class=\"container\" style=\"padding-top: 0px; left: 0px;\">";
            strVar += "                        <div class=\"row\">";
            strVar += "                            <div class=\"col-lg-6\">";
            strVar += "                                <div class=\"stageArea-copy\"><h2>A6 Limousine<\/h2><p>Faszination in vielen Facetten.<\/p><\/div>";
            strVar += "                            <\/div>";
            strVar += "                        <\/div>";
            strVar += "                        <div class=\"row hidden-xs carChoose\" style=\"display: block;\">";
            strVar += "                            <div class=\"col-xs-12\">";
            strVar += "                                <button class=\"btn-informieren\" rel=\"A6 Limousine\">Informieren<\/button>";
            strVar += "                                <button class=\"btn-machine-learning\" style=\"display:none;\"><\/button>";
            strVar += "                                <button class=\"btn-konfigurieren\" rel=\"A6 Limousine\">Konfigurieren<\/button>";
            strVar += "                                <button class=\"btn-mieten\" rel=\"A6 Limousine\">Mieten<\/button>";
            strVar += "                            <\/div>";
            strVar += "                        <\/div>";
            strVar += "                        <div class=\"row hidden-xs carChoose stageArea-neu-gebrauchtwagen\" style=\"display: block;\">";
            strVar += "                            <div class=\"col-xs-12\">";
            strVar += "                                <a href=\"#\"><span id=\"nw\" class=\"bignumber\">40<\/span> Neuwagen &gt;<\/a>";
            strVar += "                                <a href=\"#\"><span id=\"gw\" class=\"bignumber\">1416<\/span> Gebrauchtwagen &gt;<\/a>";
            strVar += "                            <\/div>";
            strVar += "                        <\/div>";
            strVar += "                        <div class=\"row hidden-xs startCarChoose\" style=\"display: none;\">";
            strVar += "                            <div class=\"col-xs-12\">";
            strVar += "                                <button class=\"btn-mehr-erfahren\">Mehr erfahren<\/button>";
            strVar += "                                <button class=\"btn-machine-learning\" style=\"display:none\"><\/button>";
            strVar += "                                <button class=\"btn-konfigurieren\" rel=\"A6 Limousine\">Konfigurieren<\/button>";
            strVar += "                            <\/div>";
            strVar += "                        <\/div>";
            strVar += "                        <!--<div class=\"row hidden-sm hidden-md hidden-lg startCarChoose mobileStage\">";
            strVar += "                            <button class=\"btn-mehr-erfahren\">Mehr erfahren<\/button>";
            strVar += "                        <\/div>";
            strVar += "                        <div class=\"row hidden-sm hidden-md hidden-lg carChoose mobileStage\">";
            strVar += "                            <button class=\"btn-informieren\">Informieren<\/button>";
            strVar += "                            <button class=\"btn-konfigurieren\">Konfigurieren<\/button>";
            strVar += "                        <\/div>-->";
            strVar += "                        <form id=\"form_modell\" method=\"GET\" action=\"model.html\">";
            strVar += "                            <input id=\"input_modell\" type=\"hidden\" name=\"modell\" value=\"\">";
            strVar += "                        <\/form>";
            strVar += "                        <form id=\"form_service\" method=\"GET\" action=\"audiservice.html\">";
            strVar += "                            <input id=\"input_service\" type=\"hidden\" name=\"modell\" value=\"\">";
            strVar += "                        <\/form>";
            strVar += "                    <\/div>";
            $(".stageArea-content .container").html(strVar);
        }
        if(user == 'audiq5owner'){
            $( ".modelselector" ).find(".ownr_slider_section").show();
            $( ".modelselector" ).find(".jumbotron").hide();
            $(".nm-homepage-md-configurator-teaser").find(".nm-homepage-md-configurator-teaser-vtp-used-cars").show();

            if(data.current_vehicle.period_owned <= 1) {
                $(".nm-homepage-md-configurator-teaser").show();
                $(".nm-homepage-md-configurator-teaser").find(".nm-homepage-md-configurator-teaser-vtp-used-cars").hide();
                $("#owner_accesories").hide();
                $("#audi_original_reifen").hide();
                $( ".modelselector" ).find( ".nm-homepage" ).css( "display","none" );
				$( ".modelselector" ).find( "#myCarousel" ).css( "display","block" );
            } else {
                $(".nm-homepage-md-configurator-teaser").hide();
                $("#owner_accesories").show();
                $("#audi_original_reifen").show();
                $( ".modelselector" ).find( ".nm-homepage" ).css( "display","block" );
				$( ".modelselector" ).find( "#myCarousel" ).css( "display","none" );
            }

            $(".content").css("display","none");
            $("#owner_config").show();
			$("#config-section").hide();
            $("#jumbo-section").hide();
            $("#intro-section").hide();
            $("#footer_image").hide();
            $("#zentrum_handburg").hide();
            $("#personalized_content").hide();
    		$("#default_content").show();
        	$("#loginmodule").hide();
            $(".dealerlocator ").hide();
            $(".notification-container").show();
            var slide_cont = '<ul class="slide-cont"><li><a href="#"><img class="no hidden-xs" width="175px" src="/content/dam/audi/images/configurator/1920x1080_AQ5_161002.jpg"></a></li>'
            slide_cont += '<li><a href="#"><img class="no hidden-xs" width="175px" src="/content/dam/audi/images/configurator/1920x1080_AQ5_161004.jpg"></a></li>';
            slide_cont += '<li><a href="#"><img class="no hidden-xs" width="175px" src="/content/dam/audi/images/configurator/1920x1080_AQ5_161005.jpg"></a></li>';
            slide_cont += '<li><a href="#"><img class="no hidden-xs" width="175px" src="/content/dam/audi/images/configurator/1920x1080_AQ5_161006.jpg"></a></li>';
            slide_cont += '<li><a href="#"><img class="no hidden-xs" width="175px" src="/content/dam/audi/images/configurator/1920x1080_AQ5_161007.jpg.resize.maxWidth=1180.jpg"></a></li>';
            slide_cont += '<li><a href="#"><img class="no hidden-xs" width="175px" src="/content/dam/audi/images/configurator/1920x1080-aas-preview.jpg"></a></li></ul>';
			$(".stageArea-copy").html("<h3>Geschaffen fÃ¼r nahezu jede Landschaft. Der neue Audi Q5</h3>");
            $(".stageArea-image").find("img:eq(0)").attr("src",'/content/dam/audi/images/configurator/1400x438_THJNK_benidorm_v07_so_final_eciv2.jpg');
        }
        if(user == 'oldaudiowner'){
            $( ".modelselector" ).find(".ownr_slider_section").show();
            $(".nm-homepage-md-configurator-teaser").find(".nm-homepage-md-configurator-teaser-vtp-used-cars").show();
            $( ".modelselector" ).find( ".nm-homepage" ).css( "display","block" );
			$( ".modelselector" ).find( "#myCarousel" ).css( "display","none" );
            $( ".modelselector" ).find(".jumbotron").hide();
            $(".nm-homepage-md-configurator-teaser").hide();
            $("#owner_accesories").show();
            $("#audi_original_reifen").show();
            $(".content").css("display","none");
            $("#owner_config").show();
			$("#config-section").hide();
            $("#jumbo-section").hide();
            $("#intro-section").hide();
            $("#footer_image").hide();
            $("#zentrum_handburg").hide();
            $("#personalized_content").hide();
    		$("#default_content").show();
        	$("#loginmodule").hide();
            $(".dealerlocator ").hide();
            $(".notification-container").show();
            var slide_cont = '<ul class="slide-cont"><li><a href="#"><img class="no hidden-xs" width="175px" src="/content/dam/audi/images/configurator/1920x1080_AQ5_161002.jpg"></a></li>'
            slide_cont += '<li><a href="#"><img class="no hidden-xs" width="175px" src="/content/dam/audi/images/configurator/1920x1080_AQ5_161004.jpg"></a></li>';
            slide_cont += '<li><a href="#"><img class="no hidden-xs" width="175px" src="/content/dam/audi/images/configurator/1920x1080_AQ5_161005.jpg"></a></li>';
            slide_cont += '<li><a href="#"><img class="no hidden-xs" width="175px" src="/content/dam/audi/images/configurator/1920x1080_AQ5_161006.jpg"></a></li>';
            slide_cont += '<li><a href="#"><img class="no hidden-xs" width="175px" src="/content/dam/audi/images/configurator/1920x1080_AQ5_161007.jpg.resize.maxWidth=1180.jpg"></a></li>';
            slide_cont += '<li><a href="#"><img class="no hidden-xs" width="175px" src="/content/dam/audi/images/configurator/1920x1080-aas-preview.jpg"></a></li></ul>';
			$(".stageArea-copy").html("<h3>Geschaffen fÃ¼r nahezu jede Landschaft. Der neue Audi Q5</h3>");
            $(".stageArea-image").find("img:eq(0)").attr("src",'/content/dam/audi/images/configurator/1400x438_THJNK_benidorm_v07_so_final_eciv2.jpg');
        }
		$("#logout-link").show();
		
    } else{
		$(".notification-container").hide();
        $(".dealerlocator").show();
        $(".stageArea-content .container").css("position","");
        $(".stageArea-content .container").css("padding-top","0px");
        $(".stageArea-content .container").css("left","0px");
        $(".content").css("margin-top","75px");
        $(".nm-homepage-md-configurator-teaser").hide();
        $("#owner_config").hide();
       // $(".stageArea-copy").html("<h2>Es gibt nur einen<br>Weg: Voraus.</h2><p>Die neuen A3 Modelle. Jetzt bei Ihrem Audi Partner.</p>");
       // $(".stageArea-image").find("img:eq(0)").attr("src",'/content/dam/audi/images/desktop/backgroundimages/default.jpg');
        $("#personalized_content").hide();
    	$("#default_content").show();
        $("#loginmodule").show();
        $("#logout-link").hide();
        $("#footer_image").hide();
        $("#zentrum_handburg").hide();
        $(".slider-container ul.types").show();
        $(".slider-container ul.config-select").remove();
	    $(".slider-container ul.slide-cont").remove();
        $(".slider-container").css("background-color","#e5e5e5");
        $(".slider-container").css("position","none");
	    $(".slider-container").css("left","0px");
 	    $(".slider-container li").css("margin","0px 30px");
    }
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
				//console.log('success data', data);
        		var loginD = [{'email' : userId}, {'name' : data.user}];
				document.cookie = "loginD"+ "="+ JSON.stringify(loginD) + ";path=/";
    			document.cookie = "userProfile"+ "="+ JSON.stringify(data) + ";path=/";
    			location.reload();

			} else {
				$(".login.step2").show();
				$("#invalid_error").show();
			}
		},

		error : function(textStatus) {
			console.log("Internal API Error " + textStatus);
			$(".login.step2").show();
			$("#invalid_error").hide();
		}
	});
}

function getMLParamsForUser(emailId) {
	//Get Machine Learning parameters for User
	$.ajax({
		url : '/bin/audi/getMLParams',
		data : 'userEmail=' + emailId,
		dataType : "json",
        async: false,

		success : function(data) {
			if (data.mlerror) {
				console.log("Error while getting ML params for user.")
			} else {
    			$.each(data, function(key, value) {
    				if(key == 'userId') {
    					mluserId = value;
    				} else {
    					if(mlparams) {
    						mlparams += "&"+key+"="+value;
    					} else {
    						mlparams += key+"="+value;
    					}
    				}
                });
    			console.log("ML Params = " + mlparams);
    			getMLRecommendation(mlparams);
			}
		},

		error : function(textStatus) {
			console.log("Internal API Error while getting ML params: " + textStatus);
		}
	});
}

function getMLRecommendation(mlparams) {
	if(mlparams) {
		$.ajax({
			url : 'http://localhost:8000/Machine_Learning_Recommended_Action',
			data : mlparams,
			dataType : "json",
			async: false,
	
			success : function(data) {
				console.log("ML recommendation for user = " + data);
				writeMLRecommendationInJCR(data);
			},
	
			error : function(textStatus) {
				console.log("Internal API Error while getting ML recommendation: " + textStatus);
			}
		});
	}
}

function writeMLRecommendationInJCR(recommendation) {
	if(mluserId && recommendation) {
		$.ajax({
			url : '/bin/audi/saveMLPrediction',
			data : "userId="+mluserId+"&recommendation="+recommendation,
			dataType : "json",
			async: false,
	
			success : function(data) {
				console.log("ML recommendation updated for user " + mluserId);
			},
	
			error : function(textStatus) {
				console.log("Internal API Error while saving ML recommendation: " + textStatus);
			}
		});
	}
}

function ValidateEmail(email) {
	var expr = /^([\w-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([\w-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\]?)$/;
	return expr.test(email);
};
