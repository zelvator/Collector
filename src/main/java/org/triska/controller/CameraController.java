package org.triska.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.triska.model.Camera;
import org.triska.model.Crossroad;
import org.triska.model.LatLng;
import org.triska.model.Polyline;
import org.triska.service.CameraService;
import org.triska.service.PolylineService;

@Controller
public class CameraController {
	@Autowired
	private CameraService cameraService;
	@Autowired
	private PolylineService polylineService;

	@Scheduled(cron = "0 0 0 * * ?")
	public void onSchedule() {
		// cameraService.deleteAllCameras();
		polylineService.deleteAllPolylines();
		// createStructure();
		createPolylines();
	}

	@PostConstruct
	public void onStartup() {
		// cameraService.deleteAllCameras();
		polylineService.deleteAllPolylines();
		// createStructure();
		createPolylines();
	}

	@Scheduled(fixedDelay = 5000)
	public void repeated() {
		List<Camera> cameras = cameraService.getAllCamera();
		for (Camera camera : cameras) {
			BufferedImage img = getCapturedImage(camera.getIpaddress());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] imageInByte = null;
			try {
				ImageIO.write(img, "jpg", baos);
				baos.flush();
				imageInByte = baos.toByteArray();
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			camera.setCaptureIdmage(imageInByte);
			camera.setCurrentTime(new Date());
			cameraService.edit(camera);

		}
	}

	@RequestMapping("/")
	public String setupForm(Map<String, Object> map) {
		Camera camera = new Camera();
		map.put("camera", camera);
		map.put("cameraList", cameraService.getAllCamera());
		map.put("polylineList", polylineService.getAllPolyline());
		return "camera";
	}

	//
	// @RequestMapping(value = "/camera.do", method = RequestMethod.POST)
	// public String doActions(@ModelAttribute Camera camera, BindingResult result, @RequestParam String action, Map<String, Object> map) {
	// Camera cameraResult = new Camera();
	// switch (action.toLowerCase()) {
	// case "add":
	// cameraService.add(camera);
	// cameraResult = camera;
	// break;
	// case "edit":
	// cameraService.edit(camera);
	// cameraResult = camera;
	// break;
	// case "delete":
	// cameraService.delete(camera.getCameraId());
	// cameraResult = new Camera();
	// break;
	// case "search":
	// Camera searchedCamera = cameraService.getCamera(camera.getCameraId());
	// cameraResult = searchedCamera != null ? searchedCamera : new Camera();
	// break;
	//
	// }
	// map.put("camera", cameraResult);
	// map.put("cameraList", cameraService.getAllCamera());
	// return "camera";
	// }

	public void createPolylines() {
		List<Camera> cameras = cameraService.getAllCamera();

		List<Polyline> list = new ArrayList<>();

		Set<Camera> s = new HashSet<Camera>();
		s.addAll(cameras);

		Stack<Crossroad> crossroads = new Stack<>();
		List<Polyline> pom = new ArrayList<>();

		for (Camera cam : s) {
			String[] crossRoadSeparated = getCrossroadSeperated(cam.getCrossroadName());
			crossroads.push(new Crossroad(crossRoadSeparated, cam.getLat(), cam.getLng()));
		}

		while (!crossroads.empty()) {
			Crossroad crossroad = crossroads.peek();
			Stack<Crossroad> crossroads2 = new Stack<>();
			crossroads2.addAll(crossroads);
			boolean found = false;
			for (Crossroad crossroad2 : crossroads2) {

				for (int i = 0; i < crossroad.getName().length; i++) {
					for (int j = 0; j < crossroad2.getName().length; j++) {
						if (crossroad.getName()[i].equals(crossroad2.getName()[j])) {

							StringBuilder str = new StringBuilder();
							for (String nam : crossroad.getName()) {
								if (str.toString().equals(""))
									str.append(nam);
								else
									str.append(" x " + nam);
							}
							StringBuilder str2 = new StringBuilder();
							for (String nam : crossroad2.getName()) {
								if (str2.toString().equals(""))
									str2.append(nam);
								else
									str2.append(" x " + nam);
							}
							pom.add(new Polyline(new String[] { str.toString(), str2.toString() },
									new LatLng(crossroad.getLat(), crossroad.getLng()), new LatLng(crossroad2.getLat(), crossroad2.getLng())));
							found = true;
						}
					}
				}
			}

			if (found) {
				// remove
				crossroads.pop();
			} else {
				// find other crossroad and remove
				crossroads.pop();
			}

			Set<Polyline> pomSet = new HashSet<Polyline>();
			pomSet.addAll(pom);

			// found nearest...

			list.addAll(getNearestPolylines(pomSet));
			pom.clear();
			pomSet.clear();

		}

		int count = 0;

		// do databaze
		for (Polyline polyline : list) {
			// count++;
			polylineService.add(polyline);
			// System.out.println("Cislo: " + count+" " + polyline.toString());
		}

		List<Polyline> poll = polylineService.getAllPolyline();
		for (Polyline polyline : poll) {
			count++;
			// polylineService.add(polyline);
			System.out.println("Cislo: " + count + " " + polyline.toString());
		}
	}

	public double distance(double lat1, double lng1, double lat2, double lng2) {
		double diffLat = lat2 - lat1;
		double diffLng = lng2 - lng2;

		return Math.sqrt(Math.pow(diffLat, 2) + Math.pow(diffLng, 2));
	}

	public List<Polyline> getNearestPolylines(Set<Polyline> pomSet) {
		List<Polyline> polylines = new ArrayList<>();
		List<Polyline> polylineStack = new ArrayList<>();
		polylineStack.addAll(pomSet);
		double distance = 0;
		Polyline nearest = null;
		Polyline secondNearest = null;
		double shortest = Double.MAX_VALUE;
		
		for (Polyline polyline : polylineStack) {
			distance = distance(polyline.getPointA().getLat(), polyline.getPointA().getLng(), polyline.getPointB().getLat(), polyline.getPointB().getLng());
			if (distance < shortest && distance >= 0.0001) {
				shortest = distance;
				 
				secondNearest = nearest;
				nearest = polyline;
				
			}
		}
		
		if (nearest != null) {
			 polylines.add(nearest);
			 
			 if(secondNearest != null){
				 polylines.add(secondNearest);
			 }
		}


		return polylines;

	}


	public String[] getCrossroadSeperated(String crossRoad) {
		String splitBySlash = "/";
		String splitByCross = "x";
		String splitByBigCross = "X";

		String[] crossRoadSeparated = null;

		if (crossRoad.contains(splitBySlash)) {
			crossRoadSeparated = crossRoad.split(splitBySlash);
		} else if (crossRoad.contains(splitByCross) && !crossRoad.contains(splitBySlash)) {
			crossRoadSeparated = crossRoad.split(splitByCross);
		} else if (crossRoad.contains(splitByBigCross) && !crossRoad.contains(splitBySlash)) {
			crossRoadSeparated = crossRoad.split(splitByBigCross);
		} else {
			crossRoadSeparated = new String[1];
			crossRoadSeparated[0] = crossRoad.trim();
		}
		for (int i = 0; i < crossRoadSeparated.length; i++) {
			crossRoadSeparated[i] = crossRoadSeparated[i].trim();
		}
		return crossRoadSeparated;
	}

	public void createStructure() {
		String address = "http://mapy.ovanet.cz/krizovatky/";
		URL url = null;
		Document doc = null;
		try {
			url = new URL(address);
			doc = Jsoup.parse(url, 3000);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Elements scripts = doc.select("script");
		Pattern p = Pattern.compile("(?is)var myLatLng(.+?)<\\\\/div>'\\)", Pattern.MULTILINE);
		String crossroad = "";
		String value = "";
		String ipAddress = "";
		String urlTag = "";
		String latlng = "";
		for (Element script : scripts) {
			Matcher m = p.matcher(script.html());
			while (m.find()) {
				value = m.group(1);
				Pattern p2 = Pattern.compile("(?is)google.maps.LatLng\\((.+?)\\);");
				Matcher m2 = p2.matcher(value);
				while (m2.find()) {
					latlng = m2.group(1);
				}
				p2 = Pattern.compile("(?is)<b>(.+?)<\\\\/b>");
				m2 = p2.matcher(value);
				while (m2.find()) {
					crossroad = m2.group(1);
				}
				p2 = Pattern.compile("(?is)<a href=\"javascript:changeKameru\\((.+?)\\);\">");
				m2 = p2.matcher(value);
				m2 = p2.matcher(value);
				while (m2.find()) {
					urlTag = m2.group(1);
					urlTag = urlTag.replace("\\'", "");
					String[] urlData = urlTag.split(",");
					ipAddress = "http://" + urlData[0] + "/?camera=" + urlData[2].trim() + "&quality=1&type=stream";
					String[] latlngSplit = latlng.split(",");
					// System.out.println("Krizovatka: " + crossroad + " smer: " + urlData[1] + " IP: " + ipAddress);
					Camera camera = new Camera();
					camera.setCrossroadName(crossroad);
					camera.setDirection(urlData[1]);
					camera.setLat(Double.parseDouble(latlngSplit[0]));
					camera.setLng(Double.parseDouble(latlngSplit[1]));
					// camera.setLatLng(latlng);
					camera.setIpaddress(ipAddress);
					cameraService.add(camera);
				}

			}
		}
	}

	public BufferedImage getCapturedImage(String imageURL) {
		HttpURLConnection huc = null;
		DataInputStream dis;
		BufferedImage image = null;
		InputStream is = null;
		URL u;
		try {
			u = new URL(imageURL);
			huc = (HttpURLConnection) u.openConnection();
			is = huc.getInputStream();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		BufferedInputStream bis = new BufferedInputStream(is);
		dis = new DataInputStream(bis);
		readLine(4, dis);
		try {
			image = ImageIO.read(dis);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			dis.close();
			huc.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// System.out.println(image.toString());
		// ImageIO.write(image, "jpg", new File("img2.jpg"));
		return image;
	}

	public static void readLine(DataInputStream dis) {
		try {
			boolean end = false;
			String lineEnd = "\n"; // assumes that the end of the line is marked
									// with this
			byte[] lineEndBytes = lineEnd.getBytes();
			byte[] byteBuf = new byte[lineEndBytes.length];

			while (!end) {
				dis.read(byteBuf, 0, lineEndBytes.length);
				String t = new String(byteBuf);
				// System.out.print(t); // uncomment if you want to see what the
				// lines actually look like
				if (t.equals(lineEnd))
					end = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void readLine(int n, DataInputStream dis) { // used to strip out the
		// header lines
		for (int i = 0; i < n; i++) {
			readLine(dis);
		}
	}

	@RequestMapping(value = "/images/{cameraId}")
	@ResponseBody
	public byte[] getCameraImage(@PathVariable int cameraId) {
		Camera camera = cameraService.getCamera(cameraId);
		return camera.getCapturedImage();
	}
}
