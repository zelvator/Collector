package org.triska.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.web.bind.annotation.ResponseBody;

@Entity
public class Camera {

	@Id
	@Column
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
	@Column
	@Lob
	private byte[] capturedImage;
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date currentTime;

	public Camera() {

	}

	public Camera(int cameraId, String crossroadName, String direction, String ipaddress, double lat, double lng, byte[] capturedImage, Date currentTime) {
		super();
		this.cameraId = cameraId;
		this.crossroadName = crossroadName;
		this.direction = direction;
		this.ipaddress = ipaddress;
		this.lat = lat;
		this.lng = lng;
		this.capturedImage = capturedImage;
		this.currentTime = currentTime;
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

	public byte[] getCapturedImage() {
		return capturedImage;
	}

	public void setCaptureIdmage(byte[] capturedImage) {
		this.capturedImage = capturedImage;
	}

	public Date getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(Date currentTime) {
		this.currentTime = currentTime;
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
		 return (Double.valueOf(this.lat).hashCode() + Double.valueOf(this.lng).hashCode() + this.crossroadName.hashCode());        
	}
	
	@Override
	public boolean equals(Object obj) {
	    // TODO Auto-generated method stub
	    if(obj instanceof Camera)
	    {
	    	Camera temp = (Camera) obj;
	        if(this.lat == temp.lat && this.lng== temp.lng && this.crossroadName.equals(temp.crossroadName))
	            return true;
	    }
	    return false;
	}
	
}
