<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/jsp/includes.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Camera Management</title>
</head>
<body>
	<jsp:include page="menu.jsp"></jsp:include>
	<h1>Camera Data</h1>

	<div id="map-canvas"></div>

	<br />
	<table border="1">
		<tr>
			<th>ID</th>
			<th>Crossroad</th>
			<th>Direction</th>
			<th>Lat</th>
			<th>Lng</th>
			<th>IP</th>
			<th>Captured Image</th>
			
		</tr>
		<c:forEach items="${cameraList}" var="camera">
			<tr>
				<td>${camera.cameraId}</td>
				<td>${camera.crossroadName}</td>
				<td>${camera.direction}</td>
				<td>${camera.lat}</td>
				<td>${camera.lng}</td>
				<td>${camera.ipaddress}</td>
				<c:forEach items="${camera.capturedImages}" var="pic">
					<td><img src="/Collector/images/${camera.cameraId}/${pic.id}.jpg"
					alt="camera${pic.id}" /><br />
					${pic.currentTime}</td>
				</c:forEach>
			</tr>
		</c:forEach>
	</table>

</body>
</html>