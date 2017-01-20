var map;
function initialize() {
	alert("Initializing Map");
	var mapCanvas = document.getElementById('map-canvas');
	var mapOptions = {
		center : {
			lat : 53.5710,
			lng : 9.9564
		},
		zoom : 10
	};
	map = new google.maps.Map(mapCanvas, mapOptions);
	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(function(position) {
			var pos = {
				lat : position.coords.latitude,
				lng : position.coords.longitude
			};
			map.setCenter(pos);
			var marker = new google.maps.Marker({
				position : pos,
				map : map
			});
		}, function() {
			handleLocationError(true);
		});
	} else {
		// Browser doesn't support Geolocation
		handleLocationError(false);
	}
	function handleLocationError(browserHasGeolocation) {
		var pos = {
			lat : 53.5710,
			lng : 9.9564
		};
		console
				.log(browserHasGeolocation ? 'Error: The Geolocation is not allowed.'
						: 'Error: Your browser doesn\'t support geolocation.');
	}
}
//google.maps.event.addDomListener(window, 'load', initialize);