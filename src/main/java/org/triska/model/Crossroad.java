package org.triska.model;

public class Crossroad {
	
	private String[] name;
	private double lat;
	private double lng;
	
	public Crossroad(String[] name, double lat, double lng) {
		this.name = name;
		this.lat = lat;
		this.lng = lng;
	}


	public Crossroad() {
	}

	
	public String[] getName() {
		return name;
	}


	public void setName(String[] name) {
		this.name = name;
	}


	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	
	
}
