package org.triska.service;

import java.util.List;

import org.triska.model.Polyline;

public interface PolylineService {
	public void add(Polyline polyline);

	public void edit(Polyline polyline);

	public void delete(int polylineId);

	public Polyline getPolyline(int polylineId);

	public List<Polyline> getAllPolyline();

	public void deleteAllPolylines();
}
