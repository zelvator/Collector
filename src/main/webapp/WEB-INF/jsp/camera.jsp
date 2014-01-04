<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style type="text/css">
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
</style>
<title>Camera Management</title>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/2.0.0/jquery.min.js"></script>
<script type="text/javascript"
	src="https://maps.googleapis.com/maps/api/js?libraries=places&sensor=false">
	
</script>
<script type="text/javascript">
	var myLatlng = new google.maps.LatLng(49.818493001315, 18.27507019043);
	function initialize() {
		var mapOptions = {
			center : myLatlng,
			zoom : 12
		};
		var map = new google.maps.Map(document.getElementById("map-canvas"),
				mapOptions);

		markers = new Array();
		<c:forEach items="${cameraList}" var="camera">
		var myLatLng = new google.maps.LatLng("${camera.lat}", "${camera.lng}");

		var marker = new google.maps.Marker({
			position : myLatLng,
			map : map,
			title : "${camera.crossroadName}"
		});

		markers.push(marker);

		</c:forEach>
	}
	google.maps.event.addDomListener(window, 'load', initialize);
</script>

</head>
<body>

	<h1>Camera Data</h1>

	<div id="map-canvas"></div>

	<br />
	<table border="1">
		<th>ID</th>
		<th>Crossroad</th>
		<th>Direction</th>
		<th>Lat</th>
		<th>Lng</th>
		<th>IP</th>
		<th>Captured Image</th>
		<th>Time</th>
		<c:forEach items="${cameraList}" var="camera">
			<tr>
				<td>${camera.cameraId}</td>
				<td>${camera.crossroadName}</td>
				<td>${camera.direction}</td>
				<td>${camera.lat}</td>
				<td>${camera.lng}</td>
				<td>${camera.ipaddress}</td>
				<td><img src="/Collector/images/${camera.cameraId}.jpg"
					alt="camera"></td>
				<td>${camera.currentTime}</td>
			</tr>
		</c:forEach>
	</table>

</body>
</html>