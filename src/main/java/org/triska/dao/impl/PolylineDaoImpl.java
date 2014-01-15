package org.triska.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.triska.dao.PolylineDao;
import org.triska.model.Polyline;

@Repository
public class PolylineDaoImpl implements PolylineDao {

	@Autowired
	private SessionFactory session;

	@Override
	public void add(Polyline polyline) {
		session.getCurrentSession().save(polyline);

	}

	@Override
	public void edit(Polyline polyline) {
		session.getCurrentSession().update(polyline);

	}

	@Override
	public void delete(int polylineid) {
		session.getCurrentSession().delete(getPolyline(polylineid));

	}
	@Override
	public void deleteAllPolylines() {
		session.getCurrentSession().createQuery("delete from Polyline").executeUpdate();
		session.getCurrentSession().createSQLQuery("ALTER TABLE polyline AUTO_INCREMENT = 1").executeUpdate();
		
	}

	@Override
	public Polyline getPolyline(int polylineid) {
		return (Polyline) session.getCurrentSession().get(Polyline.class, polylineid);
	}

	@Override
	public List<Polyline> getAllPolyline() {
		return session.getCurrentSession().createQuery("from Polyline").list();
	}


}
