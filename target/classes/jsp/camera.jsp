<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/jsp/includes.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style type="text/css" >
html {
	height: 100%
}

body {
	height: 100%;
	margin: 0;
	padding: 0
}

#map-canvas {
	height: 90%
}
.mapWindow{
	width: 300px;
	height: 240px;
}

.info-window{
	width: 650px;
}

.mapContent{
	width: 300px; 
	float: left; 
	margin-right: 10px;
}

</style>
<title>Camera Management</title>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/2.0.0/jquery.min.js"></script>
<script src="https://maps.googleapis.com/maps/api/js?v=3.13&key=AIzaSyBd3u7ZLMiQWrYkhKRCd3CxKKArBEJYJqA&sensor=false">
	
</script>
<script type="text/javascript">
	var myLatlng = new google.maps.LatLng(49.818493001315, 18.27507019043);
	var polylines;
	var kmlUrl = 'https://dl.dropboxusercontent.com/s/ql0z6n033138k9k/krizovatky.kml?dl=1&token_hash=AAHnrFd_9TNJDH6AJ_6J_kyQyVwf5qTQ8fCdtLOIlBlLOQ';
	
	function initialize() {
		  var infowindow = new google.maps.InfoWindow();
		var mapOptions = {
			center : myLatlng,
			zoom : 12
		};
		var map = new google.maps.Map(document.getElementById("map-canvas"),
				mapOptions);

		/*var trafficLayer = new google.maps.TrafficLayer();
		trafficLayer.setMap(map);*/
		  
		markers = new Array();
		<c:forEach items="${cameraList}" var="camera">
		var myLatLng = new google.maps.LatLng("${camera.lat}", "${camera.lng}");

		var marker = new google.maps.Marker({
			position : myLatLng,
			map : map,
			icon: 'webstuff/images/Camera-icon.png',
			title : "${camera.crossroadName} \n Lat (y): ${camera.lat} /Lng (x): ${camera.lng}"
		});

		makeInfoWindowEvent(map, infowindow, "${camera.lat}", "${camera.lng}", "${camera.crossroadName}", marker);
		
		markers.push(marker);

		</c:forEach>

		function makeInfoWindowEvent(map, infowindow, lat, lng, crossroad, marker) {
			
			  google.maps.event.addListener(marker, 'click', function() {
				  lat = lat + '';
				  lat = lat.replace('.', 'x');
				  lng = lng + '';
				  lng = lng.replace('.', 'x');
				  console.log(lat);	
				  console.log(lng);	
				  var content = '<div class="info-window"><h3>'+ crossroad+'</h3>';

				  var url ='./crossroad/'+lat+'/'+lng;
					  var data = jQuery.parseJSON(
					          jQuery.ajax({
					              url: url, 
					              async: false,
					              dataType: 'json'
					          }).responseText
					      );
				  console.log(data);
				  for(var i=0;i<data.length;i++) {
					  content += '<div class="mapContent"><img class = "mapWindow"src="/Collector/images/'+data[i]['id']+ '.jpg" alt="camera'+data[i]['id']+'" /><br />'
					  +new Date(data[i]['currentTime']).toString() + '<br />' + data[i]['direction'] + '</div>';
				}
				content += '</div>';
			    infowindow.setContent(content);
			    infowindow.open(map, marker);
			  });
		}

		var ctaLayer = new google.maps.KmlLayer({
			url : kmlUrl
		});
		ctaLayer.setMap(map);
	}
	google.maps.event.addDomListener(window, 'load', initialize);
</script>

</head>
<body>
<jsp:include page="menu.jsp"></jsp:include>
	<h1>Camera Map</h1>

	<div id="map-canvas"></div>

</body>
</html>