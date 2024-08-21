package com.gluonhq.helloandroid.nfc;


public enum TagTechnologyConstants 
{
	Not_Available("not_available"),
	
	NfcA("NfcA"),
	
	NfcB("NfcB"),
	
	NfcF("NfcF"),
	
	NfcV("NfcV"),
	
	IsoDep("IsoDep"),
	
	MifareUltralight("MifareUltralight"),
	
	MifareClassic("MifareClassi"),
	
	//TODO no idea to fill the parameter writeNdefMessage(NdefMessage msg) on Android native
	//Ndef("Ndef"),
	
	;

	private String nfcTag;
	
	TagTechnologyConstants(String nfcTag) 
	{
		this.nfcTag = nfcTag;
	}
	
	public String getNFCTag()
	{
		return nfcTag;
	}
	
	public static TagTechnologyConstants getTagTechnology(String io_operationString)
	{
		int startIndex = io_operationString.indexOf(ContentTags.IO_OPERATIONS.getStartTag());
		int endIndex = io_operationString.indexOf(ContentTags.IO_OPERATIONS.getEndTag());
		String contentOfTag = io_operationString.substring(startIndex+ ContentTags.IO_OPERATIONS.getStartTag().length(), endIndex);
		
		TagTechnologyConstants[] tagTechnology = TagTechnologyConstants.values();
		for(int i = 0; i < tagTechnology.length; i++)
		{
			if(contentOfTag.equals(tagTechnology[i].getNFCTag()))
			{
				return tagTechnology[i];
			}
		}
		return TagTechnologyConstants.Not_Available;
	}
}
