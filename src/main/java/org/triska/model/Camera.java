package org.triska.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name = "camera")
public class Camera implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -901786073577013575L;
	@Id
	@Column(name = "CAMERA_ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int cameraId;
	@Column
	private String crossroadName;
	@Column
	private String direction;
	@Column
	private String ipaddress;
	@Column
	private double lat;
	@Column
	private double lng;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "camera")
	@Cascade({CascadeType.ALL, CascadeType.DELETE_ORPHAN})
	@Column
	private List<CameraPic> capturedImages = new ArrayList<>();
	

	public Camera() {

	}

	public Camera(int cameraId, String crossroadName, String direction, String ipaddress, double lat, double lng, List<CameraPic> capturedImages) {
		super();
		this.cameraId = cameraId;
		this.crossroadName = crossroadName;
		this.direction = direction;
		this.ipaddress = ipaddress;
		this.lat = lat;
		this.lng = lng;
		this.capturedImages = capturedImages;
	}

	public int getCameraId() {
		return cameraId;
	}

	public void setCameraId(int cameraId) {
		this.cameraId = cameraId;
	}

	public String getCrossroadName() {
		return crossroadName;
	}

	public void setCrossroadName(String crossroadName) {
		this.crossroadName = crossroadName;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public List<CameraPic> getCapturedImages() {
		return capturedImages;
	}

	public void setCapturedImages(List<CameraPic> capturedImages) {
		this.capturedImages = capturedImages;
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
