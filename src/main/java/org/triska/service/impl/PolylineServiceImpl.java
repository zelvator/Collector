package org.triska.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.triska.dao.PolylineDao;
import org.triska.model.Polyline;
import org.triska.service.PolylineService;

@Service
public class PolylineServiceImpl implements PolylineService {

	@Autowired
	private PolylineDao polylineDao;

	@Transactional
	public void add(Polyline polyline) {
		polylineDao.add(polyline);
	}

	@Transactional
	public void edit(Polyline polyline) {
		polylineDao.edit(polyline);
	}

	@Transactional
	public void delete(int polylineId) {
		polylineDao.delete(polylineId);
	}

	@Transactional
	public Polyline getPolyline(int polylineId) {
		return polylineDao.getPolyline(polylineId);
	}

	@Transactional
	public List<Polyline> getAllPolyline() {
		return polylineDao.getAllPolyline();
	}

	@Transactional
	public void deleteAllPolylines() {
		polylineDao.deleteAllPolylines();
	}
}
