var pstring='';
for (key in sessionStorage) {
    if(key != '__nemo__a3sbetron-8V-20170124-172623' && key.indexOf('_nemo') != -1){
		pstring = jQuery.parseJSON(sessionStorage.getItem(key));
    	console.log(jQuery.parseJSON(sessionStorage.getItem(key)));
		console.log(jQuery.parseJSON(sessionStorage.getItem(key)).prString);
        sessionStorage.setItem('__nemo__a3sbetron-8V-20170124-172623', JSON.stringify(key));
		createCookie("prString",jQuery.parseJSON(sessionStorage.getItem(key)).prString,5);
    }

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
$( document ).ready(function() { 

    $.getJSON('/etc/designs/audi/user/user.json', function(data) { 
        if(document.cookie.indexOf("loginD") == -1){
                data.username = '';  
        }
        else{
                var cookie_data= JSON.parse(readCookie('loginD'));
				data.username = cookie_data[1].name;  
        }
         document.cookie = "userProfile"+ "="+ JSON.stringify(data) + ";path=/";
   		}).fail(function(e, t, n) {
        var i = t + ", " + n;
        console.log("Request Failed: " + i)
    });

    if(!pstring.prString){
        var $sessionValue = {'prString': '8VFBPX1|Y1Y1|JS','carlineID': 'a3sbetron'};
	    sessionStorage.setItem('__nemo__a3sbetron-8V-20170124-172623', JSON.stringify($sessionValue));
        createCookie("prString",'8VFBPX1|Y1Y1|JS',5);

    }
    if(pstring.prString === undefined ){
	    var $sessionValue = {'prString': '8VFBPX1|Y1Y1|JS','carlineID': 'a3sbetron'};
	    sessionStorage.setItem('__nemo__a3sbetron-8V-20170124-172623', JSON.stringify($sessionValue));
        createCookie("prString",'8VFBPX1|Y1Y1|JS',5);
    }else{
		var $sessionValue = {'prString': pstring.prString,'carlineID': 'a3sbetron'};
	    sessionStorage.setItem('__nemo__a3sbetron-8V-20170124-172623', JSON.stringify($sessionValue));
    }
	$(".nm-navigation-c").on("click",function(){
		eraseCookie('prString');
		eraseCookie('audicode');
	});
	$(".nm-layerLink_n").on("click",function(){
		var url = "https://www.audi.de/bin/dpu-de/audicode?context=nemo-de%3Ade&subsession=87482132-40&ids="+ encodeURIComponent(pstring.prString);
		$.getJSON( url, function( data ) {
			$(".g_code").css({"display":"block","background-color":"rgba(0,0,0,0.4)"});
			$(".nm-audicode").html(data.audicode.id);

            var a_code = readCookie('audicode');
            var aud_code = {'id':data.audicode.id};
            var co_data = [];
            if(document.cookie.indexOf("audicode") == -1 ){  
				co_data[0] =  aud_code;
                createCookie("audicode",JSON.stringify(co_data),5);

            }else{
                a_code = jQuery.parseJSON(a_code);
                if(a_code instanceof Array){
                  a_code.push(aud_code);
                  createCookie("audicode",JSON.stringify(a_code),5);
                }else{
                   console.log(JSON.stringify(aud_code));
					co_data[0] =  aud_code;
                    createCookie("audicode",JSON.stringify(co_data),5);
                }

            }
			//$("span.nm-at-pg-b").html(location.origin+"/"+data.audicode.id).css({"display":"none"});
        }).success(function(){updateData();});
	});
	$(".nm-button-close").on("click",function(){
		$(".g_code").css({"display":"none"});
		$(".s_code").css({"display":"none"});
		$(".l_code").css({"display":"none"});
	});
	$(".nm-layerLink_a").on("click",function(){
		var url = "https://www.audi.de/bin/dpu-de/audicode?context=nemo-de%3Ade&subsession=87482132-40&ids="+ encodeURIComponent(pstring.prString);
		$.getJSON( url, function( data ) {
			$(".s_code").css({"display":"block","background-color":"rgba(0,0,0,0.4)"});
			$(".nm-audicode").html(data.audicode.id);
			//$("span.nm-at-pg-b").html(location.origin+"/"+data.audicode.id).css({"display":"none"});
            var a_code = readCookie('audicode');
            var aud_code = {'id':data.audicode.id};
            var co_data = [];
            if(document.cookie.indexOf("audicode") == -1 ){
				co_data[0] =  aud_code;
                createCookie("audicode",JSON.stringify(co_data),5);

            }else{
                a_code = jQuery.parseJSON(a_code);
                if(a_code instanceof Array){
                  a_code.push(aud_code);
                  createCookie("audicode",JSON.stringify(a_code),5);
                }else{
                   console.log(JSON.stringify(aud_code));
					co_data[0] =  aud_code;
                    createCookie("audicode",JSON.stringify(co_data),5);
                }


            }

		}).success(function(){updateData();});
	});
	$(".nm-layerLink_l").on("click",function(){
		$(".l_code").css({"display":"block","background-color":"rgba(0,0,0,0.4)"});	
	});
	if(pstring.prString !== undefined){
		var url = "https://www.audi.de/bin/dpu-de/configuration?context=nemo-de%3Ade&ids="+ encodeURIComponent(pstring.prString);
		$.getJSON( url, function( data ) {});
	}
    $(".s_code_submit").on("click",function(){
       var audicode = $("#s_aud_code").val();
       var url = "https://www.audi.de/bin/dpu-de/audicode?context=nemo-de%3Ade&subsession=87482132-40&ids="+ encodeURIComponent(pstring.prString)+"&audicode="+audicode;
       $.getJSON( url, function( data ) {}).success(function(data){
            var prstring = data.configuration.prstring;
            var url = "https://www.audi.de/bin/dpu-de/configuration?context=nemo-de%3Ade&ids="+ encodeURIComponent(prstring);
            var $sessionValue = {'prString': prstring,'carlineID': 'a3sbetron'};
	    	sessionStorage.setItem('__nemo__a3sbetron-8V-20170124-172623', JSON.stringify($sessionValue));
		    $.getJSON( url, function( data ) { 
				window.location.reload();
                $(".s_code").css({"display":"none"});
            });

       }); 

    });

});
function updateData(){
    $.getJSON('/etc/designs/audi/user/user.json', function(data) { 
        if(document.cookie.indexOf("loginD") != -1){
            var cookie_data= JSON.parse(readCookie('loginD'));
            var aud_code = JSON.parse(readCookie('audicode'));
            data.username = cookie_data[1].name; 
            data.audicodes = aud_code;
            document.cookie = "userProfile"+ "="+ JSON.stringify(data) + ";path=/";
        }else{
			document.cookie = "userProfile"+ "="+ JSON.stringify(data) + ";path=/";
        }
    }).fail(function(e, t, n) {
        var i = t + ", " + n;
        console.log("Request Failed: " + i)
    })
}