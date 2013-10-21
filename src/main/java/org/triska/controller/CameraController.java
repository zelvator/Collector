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
import java.util.Date;
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
import org.triska.service.CameraService;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;

@Controller
public class CameraController {
	@Autowired
	private CameraService cameraService;

	@Scheduled(cron = "0 0 0 * * ?")
	public void onSchedule() {
		cameraService.deleteAllCameras();
		createStructure();
	}

	@PostConstruct
	public void onStartup() {
		cameraService.deleteAllCameras();
		createStructure();
	}

	@Scheduled(fixedDelay = 5000)
	public void repeated() {
		List<Camera> cameras = cameraService.getAllCamera();
		for (Camera camera : cameras) {
			BufferedImage img = getCapturedImage(camera.getIpaddress());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] imageInByte = null ;
			try {
				ImageIO.write( img, "jpg", baos );
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
		return "camera";
	}

	@RequestMapping(value = "/camera.do", method = RequestMethod.POST)
	public String doActions(@ModelAttribute Camera camera, BindingResult result, @RequestParam String action, Map<String, Object> map) {
		Camera cameraResult = new Camera();
		switch (action.toLowerCase()) {
		case "add":
			cameraService.add(camera);
			cameraResult = camera;
			break;
		case "edit":
			cameraService.edit(camera);
			cameraResult = camera;
			break;
		case "delete":
			cameraService.delete(camera.getCameraId());
			cameraResult = new Camera();
			break;
		case "search":
			Camera searchedCamera = cameraService.getCamera(camera.getCameraId());
			cameraResult = searchedCamera != null ? searchedCamera : new Camera();
			break;

		}
		map.put("camera", cameraResult);
		map.put("cameraList", cameraService.getAllCamera());
		return "camera";
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
		Pattern p = Pattern.compile("(?is)krizovatka.push\\('(.+?)'\\);"); // krizovatka.push('??');
		String crossroad = "";
		String value = "";
		String oldvalue = "";
		for (Element script : scripts) {
			Matcher m = p.matcher(script.html());
			boolean crossroadComplete = false;
			while (m.find()) {
				value = m.group(1);
				if (!oldvalue.isEmpty()) {
					if (value.contains("http") && !oldvalue.contains("http")) {
						Camera camera = new Camera();
						camera.setCrossroadName(crossroad);
						camera.setDirection(oldvalue);
						camera.setIpaddress(value);
						cameraService.add(camera);
						System.out.println("Krizovatka: " + crossroad + " smer: " + oldvalue + " IP: " + value);
						crossroadComplete = true;
					} else {
						crossroad = oldvalue;
					}
				}
				if (crossroadComplete) {
					oldvalue = "";
					crossroadComplete = false;
				} else {
					oldvalue = value;
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
		JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(dis);
		try {
			image = decoder.decodeAsBufferedImage();
		} catch (ImageFormatException | IOException e) {
			e.printStackTrace();
		}
		try {
			dis.close();
			huc.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		System.out.println(image.toString());
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
//				System.out.print(t); // uncomment if you want to see what the
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
	public byte[] getCameraImage(@PathVariable int cameraId)  {
	  Camera camera = cameraService.getCamera(cameraId);
	  return camera.getCapturedImage();
	}
}
