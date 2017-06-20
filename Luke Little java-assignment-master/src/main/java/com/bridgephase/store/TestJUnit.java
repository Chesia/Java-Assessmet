package com.bridgephase.store;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;

import com.bridgephase.store.interfaces.IInventory;

/**
 * 
 * @author Luke Little - Tests for the cashRegister and inventory classes
 *
 */
public class TestJUnit {
	@Test
	public void test() throws IOException, regException {
		OutputStream os = new FileOutputStream("testFile.txt");
		//Build inventory
		InputStream input = inputStreamFromString(
				"A123,Apple,0.50,1.00,1\n" +
				"A124,Orange,0.75,1.50,1\n" + 
				"A125,Chrono,15.00,35.00,1");
		IInventory inventory = new Inventory();
		inventory.replenish(input);
		
		//Cash Register Object
		CashRegister cashReg = new CashRegister(inventory);
		//Check beginning transaction throw exception
		try{
			cashReg.beginTransaction();
		}catch(regException regEx){
			fail("Exception caught when trying to start a new transaction");
		}
		//Check scan when inventory runs out on an item
		assertEquals(true, cashReg.scan("A123"));
		assertEquals(false, cashReg.scan("A123"));
		//catch exception when trying to being another transaction
		try{
			cashReg.beginTransaction();
			fail("Exception not caught when trying to start a new transaction");
		}catch (regException regEx){
		}
		//Pay for transaction and print receipt with no issues
		BigDecimal cashAmount = new BigDecimal(1);
		BigDecimal expected = new BigDecimal(0);
		assertEquals(expected,cashReg.pay(cashAmount));
		try{
			cashReg.printReceipt(os);
		}catch(regException regEx){
			fail("Exception occured when printing receipt");
		}
		//Check if inventory has been updated - Quantity should be 0 for A123
		List<Product> invCheck = inventory.list();
		assertEquals(0,invCheck.get(0).getQuantity());
		
		//Replenish inventory and check - UPC A123 quantity will equal 2
		input =  inputStreamFromString( "A123,Apple,0.50,1.00,1");
		inventory.replenish(input);
		invCheck = inventory.list();
		assertEquals(1,invCheck.get(0).getQuantity());
		
		//Start new transaction and try to print receipt, this should throw exception
		try{
		cashReg.beginTransaction();
		}catch(regException regEx){
			fail("Issue with starting transaction");
		}
		try{
			cashReg.printReceipt(os);
			fail("Exception not caught");
		}catch(regException regEx){}
		
		//pay for item with no items scanned - which should throw exception
		BigDecimal pay = new BigDecimal(1);
		try{
			cashReg.pay(pay);
			fail("Exception not caught");
		}catch(regException regEx){}
		
		//scan item and try to print receipt - exception is caught
		cashReg.scan("A123");
		try{
			cashReg.printReceipt(os);
			fail("Exception not caught");
		}catch(regException regEx){}
		
		//scan two more items and pay with no enough money, exception should be thrown
		cashReg.scan("A124");
		cashReg.scan("A125");
		try{
			cashReg.pay(pay);
			fail("Exception not caught");
		}catch(regException regEx){}
		
		//Since payment failed, check inventory to make sure it has not changed
		for (Product product : inventory.list())
			assertEquals(1, product.getQuantity());
		
		//try to print receipt - should throw exception
		try{
			cashReg.printReceipt(os);
			fail("Exception not caught");
		}catch(regException regEx){}
		
		//User begins new transaction - scans items, pays, return total of purchase, and finally print with no errors
		try{
			cashReg.beginTransaction();
		}catch(regException regEx){}
		cashReg.scan("A123");
		cashReg.scan("A124");
		cashReg.scan("A125");
		BigDecimal payment = new BigDecimal(1000);
		pay = pay.add(payment);
		BigDecimal total = cashReg.getTotal();
		BigDecimal test = new BigDecimal(37.5);
		cashReg.pay(payment);
		assertEquals(test,total);
		try{
			cashReg.printReceipt(os);
		}catch(regException regEx){}
		//Check all inventory is now empty
		for (Product product : inventory.list())
			assertEquals(0,product.getQuantity());
		
		//Replenish inventory changing the name, wholesale price, retail price, and quantity
		input = inputStreamFromString(
				"A123,Zero,0.75,1.25,3\n" +
				"A124,Vash,1.15,2.55,4\n" + 
				"A125,Kerrigan,1.65,3.99,70");
		inventory.replenish(input);
		for (Product product : inventory.list()){
			switch(product.getUpc()){
			case "A123":
				assertEquals("Zero",product.getName());
				assertEquals("0.75",String.valueOf(product.getWholesalePrice()));
				assertEquals("1.25",String.valueOf(product.getRetailPrice()));
				assertEquals(3,product.getQuantity());
				break;
			case "A124":
				assertEquals("Vash",product.getName());
				assertEquals("1.15",String.valueOf(product.getWholesalePrice()));
				assertEquals("2.55",String.valueOf(product.getRetailPrice()));
				assertEquals(4,product.getQuantity());
				break;
			case "A125":
				assertEquals("Kerrigan",product.getName());
				assertEquals("1.65",String.valueOf(product.getWholesalePrice()));
				assertEquals("3.99",String.valueOf(product.getRetailPrice()));
				assertEquals(70,product.getQuantity());
				break;
			}
		}
		
		//Start new transaction and try to scan in an item NOT in the inventory, which should return false
		cashReg.beginTransaction();
		assertEquals(false,cashReg.scan("UnicornFarmer"));
		
		//fail("Not yet implemented");
	}
	
	private static InputStream inputStreamFromString(String value) throws UnsupportedEncodingException {
		return new ByteArrayInputStream(value.getBytes("UTF-8"));
	}

}
