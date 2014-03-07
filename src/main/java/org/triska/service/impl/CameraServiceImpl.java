package org.triska.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.triska.dao.CameraDao;
import org.triska.model.Camera;
import org.triska.model.CameraPic;
import org.triska.service.CameraService;

@Service
public class CameraServiceImpl implements CameraService {

	@Autowired
	private CameraDao cameraDao;

	@Transactional
	public void add(Camera camera) {
		cameraDao.add(camera);
	}

	@Transactional
	public void edit(Camera camera) {
		cameraDao.edit(camera);
	}

	@Transactional
	public void delete(int studentId) {
		cameraDao.delete(studentId);
	}

	@Transactional
	public Camera getCamera(int cameraId) {
		return cameraDao.getCamera(cameraId);
	}

	@Transactional
	public List<Camera> getAllCamera() {
		return cameraDao.getAllCamera();
	}
	
	@Transactional
	public void deleteAllCameraPic(){
		cameraDao.deleteAllCameraPic();
	}

	@Transactional
	public void deleteAllCameras() {
		cameraDao.deleteAllCameras();
	}
	
	@Transactional
	public CameraPic getLastPic(int cameraId){
		return cameraDao.getLastPic(cameraId);
	}
	
	@Transactional
	public List<CameraPic> getCamerasPicsByGps(double lat, double lng){
		return cameraDao.getCamerasPicsByGps(lat, lng);
	}

	@Transactional
	public List<CameraPic> getCameraPics(int cameraId) {
		return cameraDao.getCameraPics(cameraId);
	}
	
	@Transactional
	public List<Camera> getAllCameraWithPic(){
		return cameraDao.getAllCameraWithPic();
	}
	@Transactional
	public CameraPic getPicture(int id){
		return cameraDao.getPicture(id);
	}
	@Transactional
	public void deletePic(int picId){
		cameraDao.deletePic(picId);
	}
}
