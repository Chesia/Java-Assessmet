package com.bridgephase.store;

public class regException extends Exception {
	public String message;

    public regException(String message){
        this.message = message;
    }

    // Overrides Exception's getMessage()
    @Override
    public String getMessage(){
        return message;
    }
}
