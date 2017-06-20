package com.bridgephase.store;
/**
 * 
 * @author Luke Little - The product class represents what the type of products the inventory class will be using
 *
 */

public class Product {
	private String upc, name;
	private int quantity;
	private double wholesalePrice, retailPrice;
	
	//Setters and getters for product variables
	public String getUpc() {
		return upc;
	}
	public void setUpc(String upc) {
		this.upc = upc;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public double getWholesalePrice() {
		return wholesalePrice;
	}
	public void setWholesalePrice(double wholesalePrice) {
		this.wholesalePrice = wholesalePrice;
	}
	public double getRetailPrice() {
		return retailPrice;
	}
	public void setRetailPrice(double retailPrice) {
		this.retailPrice = retailPrice;
	}
	//Product Constructor
	public Product(String upc, String name,  
			double wholesalePrice, double retailPrice,
			int quantity){
		this.upc = upc;
		this.name = name;
		this.quantity = quantity;
		this.wholesalePrice = wholesalePrice;
		this.retailPrice = retailPrice;
	}
}
