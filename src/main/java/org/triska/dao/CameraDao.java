package org.triska.dao;

import java.util.List;

import org.triska.model.Camera;
import org.triska.model.CameraPic;

public interface CameraDao {

	public void add(Camera camera);

	public void edit(Camera camera);

	public void delete(int cameraId);

	public Camera getCamera(int cameraId);

	public List<Camera> getAllCamera();
	
	public List<CameraPic> getCameraPics(int cameraId);

	public void deleteAllCameras();
	
	public CameraPic getLastPic(int cameraId);
	
	public List<CameraPic> geCamerasPicsByCrossroad(double lat, double lng);
	
	public List<Camera> getAllCameraWithPic();
	
	public CameraPic getPicture(int id);

	public void deletePic(int picId);

}
