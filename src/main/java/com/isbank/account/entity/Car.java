package com.isbank.account.entity;

public class Car {
	private String id;
	private int year;
	private String color;
	private String brand;
	private int price;
	private boolean soldState;	
	


	public Car(String id, int year, String color, String brand, int price, boolean soldState) {
		super();
		this.id = id;
		this.year = year;
		this.color = color;
		this.brand = brand;
		this.price = price;
		this.soldState = soldState;
	}

	public String getId() {
		return id;
	}


	public int getYear() {
		return year;
	}


	public String getColor() {
		return color;
	}


	public String getBrand() {
		return brand;
	}


	public int getPrice() {
		return price;
	}


	public boolean isSoldState() {
		return soldState;
	}


	public void setId(String id) {
		this.id = id;
	}


	public void setYear(int year) {
		this.year = year;
	}


	public void setColor(String color) {
		this.color = color;
	}


	public void setBrand(String brand) {
		this.brand = brand;
	}


	public void setPrice(int price) {
		this.price = price;
	}


	public void setSoldState(boolean soldState) {
		this.soldState = soldState;
	}

}
