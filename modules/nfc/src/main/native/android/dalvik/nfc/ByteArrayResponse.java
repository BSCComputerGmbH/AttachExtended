package com.gluonhq.helloandroid.nfc;

public class ByteArrayResponse extends AResponse<Byte[]> {

	public ByteArrayResponse(String expectedResponseString)
	{
		super(expectedResponseString);
	}
	
	public byte[] getResponse()
	{
		String rawHexString = this.expectedResponseString.replace("0x", "");
		byte[] responseArray = new byte[rawHexString.length() / 2];
		for (int i = 0; i < responseArray.length; i++) {
		
			int index = i * 2;
		    // Using parseInt() method of Integer class
		    int val = Integer.parseInt(rawHexString.substring(index, index + 2), 16);
		    responseArray[i] = (byte)val;
		}
		return responseArray;
	}

	@Override
	public boolean isExpectedResponse(Byte[] responseFromSensor) 
	{
		byte[] expectedResponse = getResponse();
		if(responseFromSensor.length == expectedResponse.length)
		{
			for(int i = 0; i < responseFromSensor.length; i++)
			{
				if(responseFromSensor[i].byteValue() !=  expectedResponse[i])
					return false;
			}
			return true;
		}
		return false;
	}
	
	public static Byte[] toObjectArray(byte[] incomingByteArray)
	{
		Byte[] returnValue = new Byte[incomingByteArray.length];
		for(int x = 0; x < incomingByteArray.length; x++)
			returnValue[x] = incomingByteArray[x];
		return returnValue;
	}
	
	
}
