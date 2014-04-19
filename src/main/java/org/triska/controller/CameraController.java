package org.triska.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.triska.model.Camera;
import org.triska.model.CameraPic;
import org.triska.service.CameraService;

@Controller
public class CameraController {
	@Autowired
	private CameraService cameraService;

	@Scheduled(cron = "0 0 0 * * ?")
	public void onSchedule() {
	}

	@PostConstruct
	public void onStartup() {
		List<Camera> cameras = cameraService.getAllCamera();
		if (cameras.isEmpty()) {
			cameraService.deleteAllCameras();
			createStructure();
		} else {
			cameraService.deleteAllCameraPic();
		}
	}

	@Async
	@Scheduled(fixedRate = 120000)
	// 2min
	public void repeatedDelete() {
		List<Camera> cameras = Collections.unmodifiableList(cameraService.getAllCamera());
		long now = System.currentTimeMillis();
		for (Camera camera : cameras) {
			camera.setCapturedImages(cameraService.getCameraPics(camera.getCameraId()));
			for (CameraPic pic : camera.getCapturedImages()) {
				if (pic.getCurrentTime().getTime() < now - 240000) {
					System.out.println("Deleting this one");
					cameraService.deletePic(pic.getId());
				}
			}
		}
	}

	@Scheduled(fixedDelay = 10000)
	// 10sec
	public void repeatedCollecter() {
		int splitArrayBy = 10;
		List<Camera> cameras = Collections.unmodifiableList(cameraService.getAllCamera());
		System.out.println("Big NOT splitted " + cameras.size());

		List<List<Camera>> parts = getChoppedList(cameras, splitArrayBy);
		System.out.println("Big splitted " + parts.size());

		Thread[] threads = new Thread[parts.size()];
		for (int i = 0; i < parts.size(); i++) {
			List<Camera> part = parts.get(i);
			System.out.println("Array " + i + " " + part.size());
			threads[i] = new Thread(new ShotCollector(part, cameraService));
			threads[i].start();
		}

		for (int i = 0; i < parts.size(); i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public static <T> List<List<T>> getChoppedList(List<T> list, final int choppBy) {
		List<List<T>> parts = new ArrayList<List<T>>();
		final int N = list.size();
		for (int i = 0; i < N; i += choppBy) {
			parts.add(new ArrayList<T>(list.subList(i, Math.min(N, i + choppBy))));
		}
		return parts;
	}

	@RequestMapping(value = { "/", "/camera" }, method = RequestMethod.GET)
	public String setupMainPage(Map<String, Object> map) {
		map.put("cameraList", cameraService.getAllCamera());
		return "camera";
	}

	@RequestMapping(value = { "/cameradata" }, method = RequestMethod.GET)
	public String setupTablePage(Map<String, Object> map) {
		map.put("cameraList", cameraService.getAllCameraWithPic());
		return "cameradata";
	}

	@RequestMapping(value = { "/creator" }, method = RequestMethod.GET)
	public String setupCreator(Map<String, Object> map) {
		map.put("cameraList", cameraService.getAllCamera());
		return "creator";
	}

	@RequestMapping(value = { "/old" }, method = RequestMethod.GET)
	public String setupOldCreator(Map<String, Object> map) {
		map.put("cameraList", cameraService.getAllCamera());
		return "old";
	}

	@RequestMapping(value = "/crossroad/{lat}/{lng}")
	public @ResponseBody
	List<CameraPic> getCameraPicsForMarkers(@PathVariable String lat, @PathVariable String lng) {
		lat = lat.replace('x', '.');
		lng = lng.replace('x', '.');
		List<CameraPic> p = cameraService.getCamerasPicsByGps(Double.parseDouble(lat), Double.parseDouble(lng));
		return p;
	}

	public void createStructure() {
		String address = "http://mapy.ovanet.cz/krizovatky/";
		URL url = null;
		Document doc = null;
		boolean connected = false;
		long start = System.currentTimeMillis();
		long later = System.currentTimeMillis();
		while (!connected && ((later - start) / 1000) < 20 ) {
			try {
				later = System.currentTimeMillis();
				url = new URL(address);
				doc = Jsoup.parse(url, 5000);
			} catch (MalformedURLException e) {
				e.printStackTrace();
				continue;
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			connected = true;
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

	public static BufferedImage getCapturedImage(String imageURL) {
		// long start = System.nanoTime();
		HttpURLConnection huc = null;
		DataInputStream dis;
		BufferedImage image = null;
		InputStream is = null;
		URL u;
		try {
			u = new URL(imageURL);
			huc = (HttpURLConnection) u.openConnection();
			huc.setConnectTimeout(10000);
			is = huc.getInputStream();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
			
		} catch(SocketTimeoutException e){
			e.printStackTrace();
			return null;
		}catch(NoRouteToHostException e){
			e.printStackTrace();
			return null;
		} 
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		catch (Exception e){
			e.printStackTrace();
			return null;
		}

		BufferedInputStream bis = new BufferedInputStream(is);
		dis = new DataInputStream(bis);

		readLine(4, dis);

		// long elapsed = System.nanoTime() - start;
		// System.out.println("Elapsed (ms): " + elapsed / 1000000);
		try {
			image = ImageIO.read(dis);
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		try {
			bis.close();
			is.close();
			dis.close();
			huc.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		// System.out.println(image.toString());
		// ImageIO.write(image, "jpg", new File("img2.jpg"));
		return image;
	}

	public static void readLine(DataInputStream dis) {
		long start = System.nanoTime();
		try {
			boolean end = false;
			String lineEnd = "\n"; // assumes that the end of the line is marked
									// with this
			byte[] lineEndBytes = lineEnd.getBytes();
			byte[] byteBuf = new byte[lineEndBytes.length];
			long elapsed = System.nanoTime() - start;
			while (!end) {
				elapsed = System.nanoTime() - start;

				dis.read(byteBuf, 0, lineEndBytes.length);
				String t = new String(byteBuf);
				// System.out.print(t); // uncomment if you want to see what the
				// lines actually look like
				if (elapsed / 1000000 > 3000) {
					end = true;
				}
				if (t.equals(lineEnd))
					end = true;
			}
			// System.out.println("Elapsed (ms): " + elapsed / 1000000);
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

	@RequestMapping(value = { "/images/{cameraId}/{id}" })
	@ResponseBody
	public byte[] getCameraImage(@PathVariable int cameraId, @PathVariable int id) {
		CameraPic camera = cameraService.getPicture(id);
		return camera.getCapturedImage();
	}

	@RequestMapping(value = { "/images/{id}" })
	@ResponseBody
	public byte[] getCameraLastImage(@PathVariable int id) {
		CameraPic camera = cameraService.getPicture(id);
		return camera.getCapturedImage();
	}

	// public List<CameraPic> getcapturedImages(int cameraId) {
	// List<CameraPic> pics= cameraService.getCameraPics(cameraId);
	// System.out.println(pics.size());
	// return pics;
	// }
}
