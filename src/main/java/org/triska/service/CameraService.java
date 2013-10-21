package org.triska.service;

import java.util.List;

import org.triska.model.Camera;

public interface CameraService {
	public void add(Camera camera);

	public void edit(Camera camera);

	public void delete(int cameraId);

	public Camera getCamera(int cameraId);

	public List<Camera> getAllCamera();
	
	public void deleteAllCameras();
}
