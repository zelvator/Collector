package org.triska.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.triska.dao.CameraDao;
import org.triska.model.Camera;
import org.triska.model.CameraPic;

@Repository
public class CameraDaoImpl implements CameraDao {

	@Autowired
	private SessionFactory session;

	@Override
	public void add(Camera camera) {
		session.getCurrentSession().save(camera);

	}

	@Override
	public void edit(Camera camera) {
		session.getCurrentSession().update(camera);

	}

	@Override
	public void delete(int cameraid) {
		session.getCurrentSession().delete(getCamera(cameraid));

	}

	@Override
	public void deleteAllCameras() {
		session.getCurrentSession().createQuery("delete from Camera").executeUpdate();
		session.getCurrentSession().createSQLQuery("ALTER TABLE camera AUTO_INCREMENT = 1").executeUpdate();

	}

	@Override
	public Camera getCamera(int cameraid) {
		Camera cam = (Camera) session.getCurrentSession().get(Camera.class, cameraid);
		return cam;
	}

	@Override
	public List<Camera> getAllCamera() {
		return session.getCurrentSession().createQuery("from Camera").list();
	}

	@Override
	public List<Camera> getAllCameraWithPic() {
		return session.getCurrentSession().createQuery("select distinct c from Camera c left join fetch c.capturedImages").list();
	}

	@Override
	public CameraPic getLastPic(int cameraId) {
		List<CameraPic> cameraPic = session.getCurrentSession()
				.createQuery("select c.capturedImages from Camera c where c.cameraId = :id order by c.capturedImages.currentTime DESC limit 1")
				.setParameter("id", cameraId).list();
		if (cameraPic.size() > 0) {
			return cameraPic.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<CameraPic> geCamerasPicsByCrossroad(double lat, double lng) {
		List<CameraPic> camPics = new ArrayList<>();
		List<Camera> cameras = session.getCurrentSession().createQuery("from Camera c where c.lat = :lat and c.lng = :lng")
				.setParameter("lat", lat)
				.setParameter("lng", lng).list();
		for (Camera camera : cameras) {
			CameraPic pic = getLastPic(camera.getCameraId());
			pic.setDirection(camera.getDirection());
			camPics.add(pic);
		}
		for (CameraPic cameraPic : camPics) {
			System.out.println("Mamm tohle: " + cameraPic.getId());
		}
		return camPics;
	}

	@Override
	public CameraPic getPicture(int id) {
		return (CameraPic) session.getCurrentSession().get(CameraPic.class, id);
	}

	@Override
	public void deletePic(int picId) {
		session.getCurrentSession().delete(getPicture(picId));

	}

	@Override
	public List<CameraPic> getCameraPics(int cameraid) {
		return session.getCurrentSession().createQuery("select c.capturedImages from Camera c where c.cameraId = :id").setParameter("id", cameraid)
				.list();
	}
}
