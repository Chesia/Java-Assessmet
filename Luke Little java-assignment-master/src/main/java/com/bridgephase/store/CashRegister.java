package com.bridgephase.store;

import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import com.bridgephase.store.interfaces.IInventory;
/**
 * 
 * @author Luke Little
 * The cashRegister class allows a user to upload an inventory and scan items from that inventory. 
 * The user can then pay for the scanned items and print a receipt
 */
public class CashRegister {
	//Data Variables
	private IInventory inventory;
	private HashMap <String, Product> invMap = new HashMap <String, Product>();
	private HashMap <String, Integer> receiptMap = new HashMap <String, Integer>();
	private double purchTotal=0;
	private int totalProdPurchased=0;
	private int transaction; // 0 = Transaction not in Process 1 = Transaction in process, 2 = Transaction payed for & wait to be printed
	private BigDecimal paid;
	private BigDecimal change;
	
	//Constructor
	public CashRegister (IInventory invt){
		this.inventory = invt;
		createMap();
	}
	//Being cash register transaction
	public void beginTransaction()throws regException{
		if(transactionCheck() != 0)
			throw new regException("Finish current transaction");
		else
			this.transaction = 1;
	}
	
	//returns an int that describes where in the process the current transaction is
	public int transactionCheck(){
		return this.transaction;
	}
	
	//Scans an items UPC label and adds it to the receipt
	//Also check in real time if there is enough inventory
	public boolean scan(String upc){
		//Check if Inventory has UPC Label
		if(this.invMap.containsKey(upc) && transactionCheck() == 1){
			Product p = invMap.get(upc);
			//Build Checkout Receipt
			if(this.receiptMap.containsKey(upc)){
				//Check if there is enough inventory
				if(checkInv(upc)) this.receiptMap.put(upc, this.receiptMap.get(upc) + 1);
				else return false;
			}
			else this.receiptMap.put(upc, 1);
			this.purchTotal += p.getRetailPrice();
			++this.totalProdPurchased;
			return true;
		}
		return false;
	}
	
	//Returns the total cost of items currently scanned in  
	public BigDecimal getTotal(){
		//Return total retail price of customers purchased goods
		BigDecimal b = new BigDecimal(purchTotal, MathContext.DECIMAL64);
		return b;
	}
	
	//Pay for the items scanned in
	public BigDecimal pay(BigDecimal cashAmount) throws regException{
		//Return change/money owed to customer
		if(cashAmount.subtract(getTotal()).compareTo(BigDecimal.ZERO) < 0 
				|| transactionCheck() != 1
				|| receiptMap.isEmpty()){
			//Clear variables since transaction is bad
			//Throw error
			cleanUp();
			int temp = transactionCheck();
			this.transaction = 0; //Transaction must be reset to 0 to cancel current transaction
			switch (temp){
				case 0:
					throw new regException("Another transaction is currently in process.");
				case 2:
					throw new regException("Please print your receipt.");
				default:
					throw new regException("You do not have enough cash for your transaction.\n" + "Transaction has been canceled.\n");
			}
		}
		//Successful - Update inventory
		updateInvMap();
		this.paid = cashAmount;
		this.change = cashAmount.subtract(getTotal());
		this.transaction = 2;
		return this.change;
	}
	
	//Print the receipt of the purchased items
	public void printReceipt(OutputStream os) throws regException{
		if(transactionCheck() != 2){
			if(transactionCheck() == 1)
				throw new regException("Another transaction is currently in process");
			else
				throw new regException("You must begin your transaction first");
		}
		PrintStream printStream = new PrintStream(os);
		//Top section of Receipt
		printStream.println("BridgePhase Convenience Store");
		printStream.println("-----------------------------");
		printStream.println("Total Products Bought: " + this.totalProdPurchased);
		printStream.println();
		//Mid section of receipt
		NumberFormat cf = NumberFormat.getCurrencyInstance();
		for(Map.Entry<String, Integer> rMap : receiptMap.entrySet()){
			Product p = (Product)invMap.get(rMap.getKey());
			printStream.println(rMap.getValue() + " " + p.getName() + " @ " //Quantity and name
			+ cf.format(p.getRetailPrice()) + ": " 							//Retail price
			+ cf.format(p.getRetailPrice()*rMap.getValue()));				//Retail price * the quantity bought
			
		}
		//Lower Section of receipt
		printStream.println("-----------------------------");
		printStream.println("Total: " + cf.format(getTotal()));
		printStream.println("Paid: " + cf.format(this.paid));
		printStream.println("Change: " + cf.format(this.change));
		printStream.println("-----------------------------");
		printStream.close();
		
		//Clean up variables for next customer
		cleanUp();
		this.transaction = 0;
	}
	
	//Creates a hashmap of the inventory
	private void createMap(){
		for(Product product : this.inventory.list())
			this.invMap.put(product.getUpc(), product);
	}
	
	//Updates the inventory
	private void updateInvMap(){
		for(Map.Entry<String, Integer> rMap : receiptMap.entrySet()){
			Product p = (Product)invMap.get(rMap.getKey());
			p.setQuantity(p.getQuantity() - rMap.getValue());
			invMap.put(rMap.getKey(), p);
		}
	}
	
	//Reset variables to inital states
	private void cleanUp(){
		this.receiptMap.clear();
		this.purchTotal=0;
		this.totalProdPurchased=0;
		this.paid = BigDecimal.valueOf(0);
		this.change = BigDecimal.valueOf(0);
	}
	
	//Checks if there is enough inventory(quantity) left
	private boolean checkInv(String upc){
		return (invMap.get(upc).getQuantity() > this.receiptMap.get(upc)) ? true:false;		
	}
}
