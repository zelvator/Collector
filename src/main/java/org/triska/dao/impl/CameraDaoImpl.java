package org.triska.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.triska.dao.CameraDao;
import org.triska.model.Camera;

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
		return (Camera) session.getCurrentSession().get(Camera.class, cameraid);
	}

	@Override
	public List<Camera> getAllCamera() {
		return session.getCurrentSession().createQuery("from Camera").list();
	}
}
