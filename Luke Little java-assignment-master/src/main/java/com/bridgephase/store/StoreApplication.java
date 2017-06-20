package com.bridgephase.store;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

import com.bridgephase.store.interfaces.IInventory;

/**
 * This can be used as an entry point to ensure that you have a working implementation for Part 1.
 * 
 * This class will initially contain compilation errors because the classes in
 * the exercise have not been implemented. Once you've implemented them the
 * compilation errors should disappear.
 * 
 * You are free to modify this class as you deem necessary although it's highly
 * recommended that you produce jUnit tests to verify the logic in your code.
 * 
 * @author Jaime Garcia Ramirez (jramirez@bridgephase.com)
 */
public class StoreApplication {

	/**
	 * This is the main entry point to this application.
	 * 
	 * @param args
	 * @throws regException 
	 * @throws IOException 
	 */
	public static void main(String args[]) throws regException, IOException {
			BigDecimal cashAmount = new BigDecimal(50);
			OutputStream os = new FileOutputStream("testFile2.txt");
			InputStream input = inputStreamFromString(
					"A123,Apple,0.50,1.00,100\n" +
					"A124,Orange,0.75,1.50,100" );
			IInventory inventory = new Inventory();
			inventory.replenish(input);
			
			//Cash Register Part
			CashRegister cashReg = new CashRegister(inventory);
			cashReg.beginTransaction();
			cashReg.scan("A123");
			cashReg.scan("A123");
			cashReg.scan("A124");
			cashReg.scan("A124");
			try{
				cashReg.pay(cashAmount);
				cashReg.printReceipt(os);
				//Check if inventory updates
				input = inputStreamFromString("A123,Apple,0.50,1.00,100\n" + "A124,Orange,0.75,1.50,200" );
				inventory.replenish(input);
				for (Product product : inventory.list())
					System.out.println("Found a product: " + product.getName() + " Quantity: " + product.getQuantity());
			}catch(regException regEx){
				System.out.println(regEx.getMessage());
			}
	}

	/**
	 * This is a simple way to convert a string to an input stream.
	 * 
	 * @param value
	 *            the String value to convert
	 * @return an InputStream that can read the values from the
	 *         <code>value</code> parameter
	 * @throws UnsupportedEncodingException
	 */
	private static InputStream inputStreamFromString(String value) throws UnsupportedEncodingException {
		return new ByteArrayInputStream(value.getBytes("UTF-8"));
	}
}