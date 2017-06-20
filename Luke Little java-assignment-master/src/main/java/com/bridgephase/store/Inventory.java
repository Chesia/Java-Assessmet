package com.bridgephase.store;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.bridgephase.store.interfaces.IInventory;
/**
 * 
 * @author Luke Little - This class will be the inventory that is used to run the cash register
 * 						 This class mimics an inventory system that would be used at a real store
 *
 */
public class Inventory implements IInventory {
	List<Product> prodList = new ArrayList<Product>();
	private HashMap<String, Integer> currInv = new HashMap<String,Integer>();
	private int index=0;
	@Override
	public void replenish(InputStream inputStream) throws IOException {
		// Add each input stream to the Product List inventory
		BufferedReader bfReader = null;
		bfReader = new BufferedReader(new InputStreamReader(inputStream));
		String invLine = null;
		while((invLine = bfReader.readLine()) != null){
			String[] aList = invLine.split(",");
			Product p = new Product(aList[0], aList[1], 							//UPC, name
						Double.parseDouble(aList[2]), Double.parseDouble(aList[3]), //wholesalePrice, retailPrice
						Integer.parseInt(aList[4]));		  						//Quantity
			//Check if product is in inventory - if so, update listing
			if(currInv.containsKey(aList[0])) 
				updateInv(p, this.currInv.get(aList[0]));
			//Add upc to inventory
			else{
				prodList.add(p);
				currInv.put(aList[0], index++);
			}
		}
		bfReader.close();
		//Testing purposes
		/*
		Iterator itr=prodList.iterator();
		NumberFormat cf = NumberFormat.getCurrencyInstance();
		while(itr.hasNext()){
			Product p=(Product)itr.next();
			System.out.println(p.getUpc() + " " + p.getName() + " " + cf.format(p.getWholesalePrice()) + " " + cf.format(p.getRetailPrice()) + " " + p.getQuantity());
		}*/
	} 

	@Override
	public List<Product> list() {
		// Return an unmodifiableList
		return Collections.unmodifiableList(prodList);
	}
	//Updates the inventory
	//Updates are based on the product passed in, and updates a product currently in the list/inventory 
	// based on that products index in the list
	public void updateInv(Product prod, int item){
		Product p = prodList.get(item);
		p.setName(prod.getName());
		p.setWholesalePrice(prod.getWholesalePrice());
		p.setRetailPrice(prod.getRetailPrice());
		p.setQuantity((p.getQuantity() + prod.getQuantity()));
		prodList.set(item, p);
	}
}
