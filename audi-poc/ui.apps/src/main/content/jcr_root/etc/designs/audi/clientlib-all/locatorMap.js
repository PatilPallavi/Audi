var map;
var service;
var infowindow;
var markers = [];
var place;
var hamburgLoc = {
	lat : 53.5710,
	lng : 9.9564
  };
var geocoder;
function initialize() {
	var mapCanvas = document.getElementById('map-canvas');
	var mapOptions = {
		center : hamburgLoc,
		zoom : 11,
        streetViewControl: false,
        mapTypeControl: false
	};
	map = new google.maps.Map(mapCanvas, mapOptions);
	infowindow = new google.maps.InfoWindow();
	service = new google.maps.places.PlacesService(map);
    geocoder = new google.maps.Geocoder();
	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(function(position) {
			var pos = {
				lat : position.coords.latitude,
				lng : position.coords.longitude
			};
            getPlaceFromLatLng(pos);
			searchAudiDealer(pos);
		}, function() {
			handleLocationError(true);
		});
	} else {
		// Browser doesn't support Geolocation
		handleLocationError(false);
	}
	document.getElementById('searchButton').addEventListener('click', function() {
		geocodeAddress(geocoder);
	});
	document.getElementById("searchLocation").addEventListener("keyup", function(event) {
		event.preventDefault();
		if (event.keyCode == 13) {
			document.getElementById("searchButton").click();
		}
	});
}

function addMarkerOnMap(place) {
	var marker = new google.maps.Marker({
		map : map,
		position : place.geometry.location,
		icon: "/content/dam/audi/images/desktop/audi_pin_active.png",
        title: place.name
	});
	google.maps.event.addListener(marker, 'click', function() {
		var infoHtml = "<p><span style='font-weight: 600;'>" + place.name + "</span></p><p>" + place.formatted_address + "</p>"
		infowindow.setContent(infoHtml);
		infowindow.open(map, this);
	});
	markers.push(marker);
}

function removeMarkers() {
	if (markers.length != 0) {
		for (var i = 0; i < markers.length; i++) {
			markers[i].setMap(null);
		}
		markers = [];
	}
}

function handleLocationError(browserHasGeolocation) {
    getPlaceFromLatLng(hamburgLoc);
	searchAudiDealer(hamburgLoc);
	console.log(browserHasGeolocation ? 'Error: The Geolocation is not allowed.'
					: 'Error: Your browser doesn\'t support geolocation.');
}

function geocodeAddress(geocoder) {
    $('#mapErrorMessage').hide();
	var address = document.getElementById('searchLocation').value;
	geocoder.geocode({
		'address' : address
	}, function(results, status) {
		if (status === 'OK') {
			searchAudiDealer(results[0].geometry.location);
		} else {
			console.log('Geocode was not successful for the following reason: ' + status);
			$('#mapErrorMessage').show();
		}
	});
}

function searchAudiDealer(location) {
	map.setCenter(location);
	removeMarkers();
	var request = {
		location : location,
		radius : '50',
		query : 'Audi Dealer'
	};
	service.textSearch(request, callback);
}

function callback(results, status) {
	if (status == google.maps.places.PlacesServiceStatus.OK) {
		for (var i = 0; i < results.length; i++) {
			addMarkerOnMap(results[i]);
		}
	}
}

function getPlaceFromLatLng(location) {
    geocoder.geocode({'location': location}, function(results, status) {
        if (status === 'OK') {
            if (results[3]) {
                document.getElementById('searchLocation').value = results[3].formatted_address;
            } else {
                console.log('No results found for reverse geocoder');
            }
        } else {
            console.log('Reverse Geocoder failed due to: ' + status);
        }
    });
}