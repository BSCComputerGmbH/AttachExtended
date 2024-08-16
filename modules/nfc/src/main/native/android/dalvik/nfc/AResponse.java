package com.gluonhq.helloandroid.nfc;

public abstract class AResponse<T> 
{

	protected String expectedResponseString;

	
	public AResponse(String expectedResponseString) {
		this.expectedResponseString = expectedResponseString;
	}
	
	public abstract boolean isExpectedResponse(T responseFromSensor);

	/**
	 * if false the response from the sensor is the result and will be given to the application
	 * @return
	 */
	public boolean isExpectedResponseToCheck()
	{
		if(expectedResponseString != null && expectedResponseString.length() > 0)
			return true;
		else
			return false;
	}
}
