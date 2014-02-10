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
      #map, html, body {
        padding: 0;
        margin: 0;
        height: 100%;
      }

      #panel {
        width: 200px;
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
      
      .gmap-control-container {
    margin: 5px;
}
.gmap-control {
    cursor: pointer;
    background-color: -moz-linear-gradient(center top , #FEFEFE, #F3F3F3);
    background-color: #FEFEFE;
    border: 1px solid #A9BBDF;
    border-radius: 2px;
    padding: 0 6px;
    line-height: 160%;
    font-size: 12px;
    font-family: Arial,sans-serif;
    box-shadow: 2px 2px 3px rgba(0, 0, 0, 0.35);
    -webkit-user-select: none;
    -khtml-user-select: none;
    -moz-user-select: none;
    -o-user-select: none;
    user-select: none;
}
.gmap-control:hover {
    border: 1px solid #678AC7;
}
.gmap-control-active {
    background-color: -moz-linear-gradient(center top , #6D8ACC, #7B98D9);
    background-color: #6D8ACC;
    color: #fff;
    font-weight: bold;
    border: 1px solid #678AC7;
}
.gmap-control-legend {
    position: absolute;
    text-align: left;
    z-index: -1;
    top: 20px;
    right: 0;
    width: 150px;
    height: 66px;
    font-size: 10px;
    background: #FEFEFE;
    border: 1px solid #A9BBDF;
    padding: 10px;
    box-shadow: 2px 2px 3px rgba(0, 0, 0, 0.35);
}
.gmap-control-legend ul {
    margin: 0;
    padding: 0;
    list-style-type: none;
}
.gmap-control-legend li {
    line-height: 160%;
}
    </style>
    <script type="text/javascript">
      var drawingManager;
      var selectedShape;
      var colors = ['#1E90FF', '#FF1493', '#32CD32', '#FF8C00', '#4B0082'];
      var selectedColor;
      var colorButtons = {};

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
          colorButtons[currColor].style.border = currColor == color ? '2px solid #789' : '2px solid #fff';
        }

        // Retrieves the current options from the drawing manager and replaces the
        // stroke or fill color as appropriate.
        var polylineOptions = drawingManager.get('polylineOptions');
        polylineOptions.strokeColor = color;
        drawingManager.set('polylineOptions', polylineOptions);

        var rectangleOptions = drawingManager.get('rectangleOptions');
        rectangleOptions.fillColor = color;
        drawingManager.set('rectangleOptions', rectangleOptions);

        var circleOptions = drawingManager.get('circleOptions');
        circleOptions.fillColor = color;
        drawingManager.set('circleOptions', circleOptions);

        var polygonOptions = drawingManager.get('polygonOptions');
        polygonOptions.fillColor = color;
        drawingManager.set('polygonOptions', polygonOptions);
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
    	var polylines;
        var map = new google.maps.Map(document.getElementById('map'), {
        	zoom : 12,
          center: myLatlng,
          mapTypeId: google.maps.MapTypeId.ROADMAP,
          disableDefaultUI: true,
          zoomControl: true
        });

        var polyOptions = {
          strokeWeight: 0,
          fillOpacity: 0.45,
          editable: true
        };
        var controlDiv = document.createElement('DIV');
        $(controlDiv).addClass('gmap-control-container')
                     .addClass('gmnoprint');
                  
        var controlUI = document.createElement('DIV');
        $(controlUI).addClass('gmap-control');
        $(controlUI).text('Provoz');
        $(controlDiv).append(controlUI);
                  
        var legend = '<ul>'
                   + '<li><span style="background-color: #30ac3e">&nbsp;&nbsp;</span><span style="color: #30ac3e"> &gt; 80 km per hour</span></li>'
                   + '<li><span style="background-color: #ffcf00">&nbsp;&nbsp;</span><span style="color: #ffcf00"> 40 - 80 km per hour</span></li>'
                   + '<li><span style="background-color: #ff0000">&nbsp;&nbsp;</span><span style="color: #ff0000"> &lt; 40 km per hour</span></li>'
                   + '<li><span style="background-color: #c0c0c0">&nbsp;&nbsp;</span><span style="color: #c0c0c0"> No data available</span></li>'
                   + '</ul>';
                  
        var controlLegend = document.createElement('DIV');
        $(controlLegend).addClass('gmap-control-legend');
        $(controlLegend).html(legend);
        $(controlLegend).hide();
        $(controlDiv).append(controlLegend);
                  
        // Set hover toggle event
        $(controlUI)
            .mouseenter(function() {
                $(controlLegend).show();
            })
            .mouseleave(function() {
                $(controlLegend).hide();
            });
                  
        var trafficLayer = new google.maps.TrafficLayer();
                  
        google.maps.event.addDomListener(controlUI, 'click', function() {
            if (typeof trafficLayer.getMap() == 'undefined' || trafficLayer.getMap() === null) {
                $(controlUI).addClass('gmap-control-active');
                trafficLayer.setMap(map);
            } else {
                trafficLayer.setMap(null);
                $(controlUI).removeClass('gmap-control-active');
            }
        });
                  
        map.controls[google.maps.ControlPosition.TOP_RIGHT].push(controlDiv);
        // Creates a drawing manager attached to the map that allows the user to draw
        // markers, lines, and shapes.
        drawingManager = new google.maps.drawing.DrawingManager({
          drawingMode: google.maps.drawing.OverlayType.POLYGON,
          markerOptions: {
            draggable: true
          },
          polylineOptions: {
            editable: true
          },
          rectangleOptions: polyOptions,
          circleOptions: polyOptions,
          polygonOptions: polyOptions,
          map: map
        });

        google.maps.event.addListener(drawingManager, 'overlaycomplete', function(e) {
            if (e.type != google.maps.drawing.OverlayType.MARKER) {
            // Switch back to non-drawing mode after drawing a shape.
            drawingManager.setDrawingMode(null);

            // Add an event listener that selects the newly-drawn shape when the user
            // mouses down on it.
            var newShape = e.overlay;
            newShape.type = e.type;
            google.maps.event.addListener(newShape, 'click', function() {
              setSelection(newShape);
            });
            setSelection(newShape);
          }
        });
 

        // Clear the current selection when the drawing mode is changed, or when the
        // map is clicked.
        google.maps.event.addListener(drawingManager, 'drawingmode_changed', clearSelection);
        google.maps.event.addListener(map, 'click', clearSelection);
        google.maps.event.addDomListener(document.getElementById('delete-button'), 'click', deleteSelectedShape);

        buildColorPalette();

        markers = new Array();
		<c:forEach items="${cameraList}" var="camera">
		var myLatLng = new google.maps.LatLng("${camera.lat}", "${camera.lng}");

		var marker = new google.maps.Marker({
			position : myLatLng,
			map : map,
			title : "${camera.crossroadName} \n Lat (y): ${camera.lat} /Lng (x): ${camera.lng}"
		});

		markers.push(marker);

		</c:forEach>
		/*polylines = new Array();
		<c:forEach items="${polylineList}" var="polyline">
		
		var pathCoordinates = [
		         new google.maps.LatLng("${polyline.pointA.lat}", "${polyline.pointA.lng}"),
		         new google.maps.LatLng("${polyline.pointB.lat}", "${polyline.pointB.lng}")
		          ];
		var crossPath = new google.maps.Polyline({
		      path: pathCoordinates,
			  editable: true,
		      strokeColor: "#0000FF",
		      strokeOpacity: 1.0,
		      strokeWeight: 2,
		      map: map
		});

		polylines.push(crossPath);	
		</c:forEach>
		
		 google.maps.event.addListener(map, 'click', function() {
			 for (var i = 0; i < polylines.length; i++) {
				 console.log("Polyline: " + i);
				 console.log(polylines[i].getPath().getArray().toString());
				
			}
				 
		 });*/
      }
      google.maps.event.addDomListener(window, 'load', initialize);
    </script>
  </head>
  <body>
    <div id="panel">
      <div id="color-palette"></div>
      <div>
        <button id="delete-button">Delete Selected Shape</button>
      </div>
    </div>
    <div id="map"></div>
  </body>
</html>

