package org.triska.service;

import java.util.List;

import org.triska.model.Camera;
import org.triska.model.CameraPic;

public interface CameraService {
	public void add(Camera camera);

	public void edit(Camera camera);

	public void delete(int cameraId);

	public Camera getCamera(int cameraId);

	public List<Camera> getAllCamera();
	
	public List<CameraPic> getCameraPics(int cameraId);
	
	public void deleteAllCameraPic();
	
	public void deleteAllCameras();
	
	public CameraPic getLastPic(int cameraId);
	
	public List<CameraPic> getCamerasPicsByGps(double lat, double lng);

	public List<Camera> getAllCameraWithPic();
	
	public CameraPic getPicture(int id);
	
	public void deletePic(int picId);
}
