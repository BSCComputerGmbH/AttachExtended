package com.gluonhq.helloandroid.nfc;

public abstract class AResponse 
{

	protected String expectedResponseString;

	
	public AResponse(String expectedResponseString) {
		this.expectedResponseString = expectedResponseString;
	}

}
