package org.triska.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.orm.hibernate4.HibernateOptimisticLockingFailureException;
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
		Iterator<Camera> it = list.iterator();
		while(it.hasNext()){
			Camera camera = (Camera) it.next();
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
			camera.setCapturedImages(cameraService.getCameraPics(camera.getCameraId()));
			camera.getCapturedImages().add(camPic);
			try{
				cameraService.edit(camera);
			} catch (HibernateOptimisticLockingFailureException ex){
				ex.printStackTrace();
				continue;
			}
			
		}
		
	}

}
