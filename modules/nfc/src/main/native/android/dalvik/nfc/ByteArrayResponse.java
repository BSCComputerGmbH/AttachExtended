package com.gluonhq.helloandroid.nfc;

public class ByteArrayResponse extends AResponse {

	public ByteArrayResponse(String expectedResponseString)
	{
		super(expectedResponseString);
	}
	
	
	public byte[] getResponse()
	{
		String rawHexString = this.expectedResponseString.replace("0x", "");
		System.out.println("contentOfResponse " + rawHexString);
		byte[] responseArray = new byte[rawHexString.length() / 2];
		System.out.println("Hex String : "+ rawHexString);
		for (int i = 0; i < responseArray.length; i++) {
		
			int index = i * 2;
		    // Using parseInt() method of Integer class
		    int val = Integer.parseInt(rawHexString.substring(index, index + 2), 16);
		    responseArray[i] = (byte)val;
		}
		return responseArray;
	}
	
	
}
