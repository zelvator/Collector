package org.triska.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.triska.model.Camera;
import org.triska.model.CameraPic;
import org.triska.service.CameraService;

public class ShotCollector implements Runnable{

	private List<Camera> list = new ArrayList<>();
	private CameraService cameraService;
	
	public ShotCollector(List<Camera> list, CameraService cameraService){
		this.list = list;
		this.cameraService = cameraService;
	}
	
	@Override
	public void run() {

		BufferedImage img = null;
		for (Camera camera : this.list) {
			img = CameraController.getCapturedImage(camera.getIpaddress());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] imageInByte = null;
			if(img == null){
				//nejaky nahradni obrazek...
				img = new BufferedImage(1, 1,BufferedImage.TYPE_INT_ARGB);
			}
			try {
				ImageIO.write(img, "jpg", baos);
				baos.flush();
				imageInByte = baos.toByteArray();
				
			} catch (IOException e) {
				e.printStackTrace();
			} finally{
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			CameraPic camPic = new CameraPic();
			camPic.setCapturedImage(imageInByte);
			camPic.setCurrentTime(new Date());
			camPic.setCamera(camera);
//			Hibernate.initialize(camWithPics.getCapturedImages());
			camera.setCapturedImages(cameraService.getCameraPics(camera.getCameraId()));
			camera.getCapturedImages().add(camPic);
			cameraService.edit(camera);
		}
		
	}

}
