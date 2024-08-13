package com.gluonhq.helloandroid.nfc;

public class ByteArrayRequest extends ARequest
{
	public ByteArrayRequest(String requestString)
	{
		super(requestString);
	}
	
	public byte[] getRequest()
	{
		String rawHexString = this.requestString.replace("0x", "");
		System.out.println("contentOfRequest " + rawHexString);
		byte[] requestArray = new byte[rawHexString.length() / 2];
		System.out.println("Hex String : "+ rawHexString);
		for (int i = 0; i < requestArray.length; i++) {
		
			int index = i * 2;
		    // Using parseInt() method of Integer class
		    int val = Integer.parseInt(rawHexString.substring(index, index + 2), 16);
		    requestArray[i] = (byte)val;
		}
		return requestArray;
	}
	

}
