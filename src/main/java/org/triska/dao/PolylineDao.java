package org.triska.dao;

import java.util.List;

import org.triska.model.Polyline;

public interface PolylineDao {

	public void add(Polyline polyline);

	public void edit(Polyline polyline);

	public void delete(int polylineId);

	public Polyline getPolyline(int polylineId);

	public List<Polyline> getAllPolyline();

	public void deleteAllPolylines();

}
