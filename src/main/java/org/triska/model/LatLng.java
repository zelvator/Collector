package org.triska.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class LatLng {
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int ID;
	@Column
	private double lat;
	@Column
	private double lng;
	
	public LatLng(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}
	
	public LatLng() {
	}
	
	public LatLng(int iD, double lat, double lng) {
		super();
		ID = iD;
		this.lat = lat;
		this.lng = lng;
	}


	public int getID() {
		return ID;
	}


	public void setID(int iD) {
		ID = iD;
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
	
	@Override
	public int hashCode() {
		 return (Double.valueOf(this.lat).hashCode() + Double.valueOf(this.lng).hashCode());        
	}
	
	@Override
	public boolean equals(Object obj) {
	    // TODO Auto-generated method stub
	    if(obj instanceof LatLng)
	    {
	    	LatLng temp = (LatLng) obj;
	        if(this.lat == temp.lat && this.lng== temp.lng)
	            return true;
	    }
	    return false;
	}
	
	@Override
	public String toString() {
		return "lat: " + lat + ", lng: " + lng;
	}
}
