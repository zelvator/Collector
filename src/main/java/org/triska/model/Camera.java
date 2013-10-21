package org.triska.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

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
	@Lob
	private byte[] capturedImage;
	@Column
	private Date currentTime;

	public Camera() {

	}

	public Camera(int cameraId, String crossroadName, String direction, String ipaddress, byte[] capturedImage, Date currentTime) {
		super();
		this.cameraId = cameraId;
		this.crossroadName = crossroadName;
		this.direction = direction;
		this.ipaddress = ipaddress;
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
	
	public String getByteArrayString()
	{
	   return new String(this.capturedImage);
	}

}
