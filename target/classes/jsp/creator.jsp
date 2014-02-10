<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/jsp/includes.jsp"%>

<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="initial-scale=1.0, user-scalable=no">
<meta charset="UTF-8">
<title>Drawing Tools</title>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/2.0.0/jquery.min.js"></script>
<script type="text/javascript"
	src="http://maps.google.com/maps/api/js?v=3.13&key=AIzaSyBd3u7ZLMiQWrYkhKRCd3CxKKArBEJYJqA&sensor=false&libraries=drawing"></script>
<style type="text/css">
#map,html,body {
	padding: 0;
	margin: 0;
	height: 100%;
}

#panel {
	width: 350px;
	font-family: Arial, sans-serif;
	font-size: 13px;
	float: right;
	margin: 10px;
}

#color-palette {
	clear: both;
}

.color-button {
	width: 14px;
	height: 14px;
	font-size: 0;
	margin: 2px;
	float: left;
	cursor: pointer;
}

#delete-button {
	margin-top: 5px;
}
}
</style>
<script type="text/javascript">
	var drawingManager;
	var selectedShape;
	var colors = [ '#1E90FF', '#FF1493', '#32CD32', '#FF8C00', '#4B0082' ];
	var selectedColor;
	var colorButtons = {};
	var kmlUrl = 'https://dl.dropboxusercontent.com/s/ql0z6n033138k9k/krizovatky.kml?dl=1&token_hash=AAHnrFd_9TNJDH6AJ_6J_kyQyVwf5qTQ8fCdtLOIlBlLOQ';

	function clearSelection() {
		if (selectedShape) {
			selectedShape.setEditable(false);
			selectedShape = null;
		}
	}

	function setSelection(shape) {
		clearSelection();
		selectedShape = shape;
		shape.setEditable(true);
		selectColor(shape.get('fillColor') || shape.get('strokeColor'));
	}

	function deleteSelectedShape() {
		if (selectedShape) {
			selectedShape.setMap(null);
		}
	}

	function selectColor(color) {
		selectedColor = color;
		for (var i = 0; i < colors.length; ++i) {
			var currColor = colors[i];
			colorButtons[currColor].style.border = currColor == color ? '2px solid #789'
					: '2px solid #fff';
		}

		// Retrieves the current options from the drawing manager and replaces the
		// stroke or fill color as appropriate.
		var polylineOptions = drawingManager.get('polylineOptions');
		polylineOptions.strokeColor = color;
		drawingManager.set('polylineOptions', polylineOptions);
	}

	function setSelectedShapeColor(color) {
		if (selectedShape) {
			if (selectedShape.type == google.maps.drawing.OverlayType.POLYLINE) {
				selectedShape.set('strokeColor', color);
			} else {
				selectedShape.set('fillColor', color);
			}
		}
	}

	function makeColorButton(color) {
		var button = document.createElement('span');
		button.className = 'color-button';
		button.style.backgroundColor = color;
		google.maps.event.addDomListener(button, 'click', function() {
			selectColor(color);
			setSelectedShapeColor(color);
		});

		return button;
	}

	function buildColorPalette() {
		var colorPalette = document.getElementById('color-palette');
		for (var i = 0; i < colors.length; ++i) {
			var currColor = colors[i];
			var colorButton = makeColorButton(currColor);
			colorPalette.appendChild(colorButton);
			colorButtons[currColor] = colorButton;
		}
		selectColor(colors[0]);
	}

	function initialize() {
		var myLatlng = new google.maps.LatLng(49.818493001315, 18.27507019043);
		var map = new google.maps.Map(document.getElementById('map'), {
			zoom : 12,
			center : myLatlng,
			mapTypeId : google.maps.MapTypeId.ROADMAP,
			disableDefaultUI : true,
			zoomControl : true
		});

		var polyOptions = {
			strokeWeight : 0,
			fillOpacity : 0.45,
			editable : true
		};

		// Creates a drawing manager attached to the map that allows the user to draw
		// lines.
		drawingManager = new google.maps.drawing.DrawingManager({
			drawingMode : google.maps.drawing.OverlayType.POLYLINE,
			drawingControl : true,
			drawingControlOptions : {
				drawingModes : [ google.maps.drawing.OverlayType.POLYLINE, ]
			},
			polylineOptions : {
				editable : true
			},
			map : map
		});

		google.maps.event.addListener(drawingManager, 'overlaycomplete',
				function(e) {
					if (e.type != google.maps.drawing.OverlayType.MARKER) {
						// Switch back to non-drawing mode after drawing a shape.
						drawingManager.setDrawingMode(null);

						// Add an event listener that selects the newly-drawn shape when the user
						// mouses down on it.
						var newShape = e.overlay;
						newShape.type = e.type;
						google.maps.event.addListener(newShape, 'click',
								function() {
									setSelection(newShape);
								});
						setSelection(newShape);
					}
				});

		google.maps.event
				.addListener(
						map,
						'click',
						function() {
							if (selectedShape) {
								if (selectedShape.type == google.maps.drawing.OverlayType.POLYLINE) {
									var coords = "";
									var lat = "";
									var lng = "";
									for (var i = 0; i < selectedShape.getPath()
											.getLength(); i++) {
										lat = selectedShape.getPath().getAt(i)
												.lat();
										lng = selectedShape.getPath().getAt(i)
												.lng();

										coords += (lng + "," + lat + ",0\n");
									}
									var color = selectedShape
											.get('strokeColor').toString();
									color = setKmlColor(color);

									var text = '<Style id=\"\">\n'
											+ '    <LineStyle>\n'
											+ '        <color>'
											+ color
											+ '</color>\n'
											+ '        <width>3</width>\n'
											+ '    </LineStyle>\n'
											+ '</Style>\n'
											+ '\n'
											+ '<Placemark>\n'
											+ '    <name></name>\n'
											+ '    <styleUrl>#</styleUrl>\n'
											+ '    <LineString>\n'
											+ '        <altitudeMode>relative</altitudeMode>\n'
											+ '        <coordinates>\n'
											+ coords
											+ '        </coordinates>\n'
											+ '    </LineString>\n'
											+ '</Placemark>';
									document.getElementById("generatedtext").innerText = text;
								}
							}
						});

		// Clear the current selection when the drawing mode is changed, or when the
		// map is clicked.
		google.maps.event.addListener(drawingManager, 'drawingmode_changed',
				clearSelection);
		google.maps.event.addListener(map, 'click', clearSelection);
		google.maps.event.addDomListener(document
				.getElementById('delete-button'), 'click', deleteSelectedShape);

		buildColorPalette();

		markers = new Array();
		<c:forEach items="${cameraList}" var="camera">
		var myLatLng = new google.maps.LatLng("${camera.lat}", "${camera.lng}");

		var marker = new google.maps.Marker(
				{
					position : myLatLng,
					map : map,
					title : "${camera.crossroadName} \n Lat (y): ${camera.lat} /Lng (x): ${camera.lng}"
				});

		markers.push(marker);

		</c:forEach>
		var ctaLayer = new google.maps.KmlLayer({
			url : kmlUrl
		});
		ctaLayer.setMap(map);

	}
	google.maps.event.addDomListener(window, 'load', initialize);

	function setKmlColor(color)

	{
		//rgb format for NORMAL color is #RRGGBB
		//KML has AABBGGRR...
		var newString = "";
		var alpha = "FF";
		var red = color.substring(1, 3);
		var green = color.substring(3, 5);
		var blue = color.substring(5, 7);

		newString = alpha + blue + green + red;
		return newString;
	};
</script>

</head>
<body>
	<jsp:include page="menu.jsp"></jsp:include>
	<div id="panel">
		<div id="color-palette"></div>
		<div>
			<button id="delete-button">Delete Selected Shape</button>
		</div>

		<p>
			XML formatted<br /> Fill in id, name and styleUrl (id):<br /> <br />
		</p>
		<div id="generatedtext">Text will generate after you select
			created polyline and polyline ONLY and then click on map somewhere
			else...</div>
	</div>
	<div id="map"></div>
</body>
</html>

