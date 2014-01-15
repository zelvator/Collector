package org.triska.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Polyline {
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idPolyline;
	@ManyToOne (cascade=CascadeType.ALL)
	@JoinColumn(name = "pointA")
	private LatLng pointA;
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "pointB")
	private LatLng pointB;

	
	public Polyline(int idPolyline, LatLng pointA, LatLng pointB) {
		this.idPolyline = idPolyline;
		this.pointA = pointA;
		this.pointB = pointB;
	}
	
	public Polyline(){
		
	}

	public Polyline(LatLng pointA, LatLng pointB) {
		this.pointA = pointA;
		this.pointB = pointB;
	}

	
	public int getIdPolyline() {
		return idPolyline;
	}


	public void setIdPolyline(int idPolyline) {
		this.idPolyline = idPolyline;
	}


	public LatLng getPointA() {
		return pointA;
	}

	public void setPointA(LatLng pointA) {
		this.pointA = pointA;
	}

	public LatLng getPointB() {
		return pointB;
	}

	public void setPointB(LatLng pointB) {
		this.pointB = pointB;
	}

	@Override
	public int hashCode() {
		return (this.pointA.hashCode() + this.pointB.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj instanceof Polyline) {
			Polyline temp = (Polyline) obj;
			if (this.pointA.getLat() == temp.pointA.getLat() && this.pointA.getLng() == temp.pointA.getLng() 
					&& this.pointB.getLat() == temp.pointB.getLat() && this.pointB.getLng() == temp.pointB.getLng())
				return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Point A: " + pointA.toString() + " Point B: " + pointB.toString();
	}
}
