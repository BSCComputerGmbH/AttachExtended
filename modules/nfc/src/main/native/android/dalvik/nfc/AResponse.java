package com.gluonhq.helloandroid.nfc;

public abstract class AResponse<T> 
{

	protected String expectedResponseString;

	
	public AResponse(String expectedResponseString) {
		this.expectedResponseString = expectedResponseString;
	}
	
	public abstract boolean isExpectedResponse(T responseFromSensor);

}
