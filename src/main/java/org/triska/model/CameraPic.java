package org.triska.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name = "camera_picture")
public class CameraPic implements java.io.Serializable {

	@Id
	@Column(name = "PIC_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column
	@Lob
	private byte[] capturedImage;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CAMERA_ID", nullable = false)
	@JsonIgnore
	private Camera camera;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date currentTime;
	

	@Column(nullable = true)
	private String direction;

	public CameraPic() {
	}

	public CameraPic(byte[] capturedImage, Camera camera, Date currentTime, String direction) {
		this.capturedImage = capturedImage;
		this.camera = camera;
		this.currentTime = currentTime;
		this.direction = direction;
	}

	public CameraPic(int id, byte[] capturedImage, Camera camera, Date currentTime, String direction) {
		this.id = id;
		this.capturedImage = capturedImage;
		this.camera = camera;
		this.currentTime = currentTime;
		this.direction = direction;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public byte[] getCapturedImage() {
		return capturedImage;
	}


	public void setCapturedImage(byte[] capturedImage) {
		this.capturedImage = capturedImage;
	}


	public Camera getCamera() {
		return camera;
	}


	public void setCamera(Camera camera) {
		this.camera = camera;
	}


	public Date getCurrentTime() {
		return currentTime;
	}


	public void setCurrentTime(Date currentTime) {
		this.currentTime = currentTime;
	}


	public String getDirection() {
		return direction;
	}


	public void setDirection(String direction) {
		this.direction = direction;
	}

	
}
