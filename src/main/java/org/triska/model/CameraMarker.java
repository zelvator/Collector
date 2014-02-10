package org.triska.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseBody;

public class CameraMarker{

	private String crossroadName;
	private double lat;
	private double lng;
	private List<CameraPic> capturedImages = new ArrayList<>();
	
	
	public CameraMarker() {
	}

	public CameraMarker(String crossroadName, double lat, double lng, List<CameraPic> capturedImages) {
		this.crossroadName = crossroadName;
		this.lat = lat;
		this.lng = lng;
		this.capturedImages = capturedImages;
	}
	
	public String getCrossroadName() {
		return crossroadName;
	}
	public void setCrossroadName(String crossroadName) {
		this.crossroadName = crossroadName;
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
	public List<CameraPic> getCapturedImages() {
		return capturedImages;
	}
	public void setCapturedImages(List<CameraPic> capturedImages) {
		this.capturedImages = capturedImages;
	}
	

}
