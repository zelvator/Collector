<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Camera Management</title>
</head>
<body>
	<h1>Camera Data</h1>
	<!--<form:form action="camera.do" method="POST" commandName="camera">
		<table>
			<tr>
				<td>Camera ID</td>
				<td><form:input path="cameraId" /></td>
			</tr>
			<tr>
				<td>Crossroad name</td>
				<td><form:input path="crossroadName" /></td>
			</tr>
			<tr>
				<td>Direction</td>
				<td><form:input path="direction" /></td>
			</tr>
			<tr>
				<td>IP address</td>
				<td><form:input path="ipaddress" /></td>
			</tr>
			<tr>
				<td colspan="2"><input type="submit" name="action" value="Add" />
					<input type="submit" name="action" value="Edit" /> <input
					type="submit" name="action" value="Delete" /> <input type="submit"
					name="action" value="Search" /></td>
			</tr>
		</table>
	</form:form>-->

	<br />
	<table border="1">
		<th>ID</th>
		<th>Crossorad</th>
		<th>Direction</th>
		<th>IP</th>
		<th>Captured Image</th>
		<th>Time</th>
		<c:forEach items="${cameraList}" var="camera">
			<tr>
				<td>${camera.cameraId}</td>
				<td>${camera.crossroadName}</td>
				<td>${camera.direction}</td>
				<td>${camera.ipaddress}</td>
				<td><img  src="/Collector/images/${camera.cameraId}.jpg" alt="camera"></td>
				<td>${camera.currentTime}</td>
			</tr>
		</c:forEach>
	</table>

</body>
</html>