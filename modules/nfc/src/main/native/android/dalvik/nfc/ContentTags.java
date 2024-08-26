package com.gluonhq.helloandroid.nfc;

/**
 * the received nfc content is enclosed with tags for the different types
 * @author mg
 *
 */
public enum ContentTags 
{
	//E.g. to send a error message to the application 
	Notification("<notification>",  "</notification>"),
	
	//Tags in Reihenfolge der Zeichenkette hier definieren
	NdefMessage_Description("<ndefmessage#description>", "</ndefmessage#description>"),
	NdefMessage_RecordLength("<ndefmessage#recordlength>", "</ndefmessage#recordlength>"),
	//es wird mehrere geben, Unterscheidung?
	NdefMessage_Record("<ndefmessage#record>", "</ndefmessage#record>"),
	
	//inner tags from ndefMessage_Record
	NdefRecord_id("<ndefrecord#id>", "</ndefrecord#id>"),
	NdefRecord_tnf("<ndefrecord#tnf>", "</ndefrecord#tnf>"),
	NdefRecord_type("<ndefrecord#type>", "</ndefrecord#type>"),
	NdefRecord_payload("<ndefrecord#payload>", "</ndefrecord#payload>"),
	NdefRecord_mimeType("<ndefrecord#mimeType>", "</ndefrecord#mimeType>"),
	//optional tags for the response, if user send request to nfc
	NdefRecord_response_content("<ndefrecord#response_content>", "</ndefrecord#response_content>"),
	NdefRecord_respone_error("<ndefrecord#response_error>", "</ndefrecord#response_error>"),

	//request with no bidirectional messaging with the nfc
	SimpleRequestCall("<simpleRequestCall>", "</simpleRequestCall>"),
	//request with call a sequence of request to the nfc
	SequenceRequestCall("<sequenceRequestCall>", "</sequenceRequestCall>"),
	//use, if sequence request calls
	Request("<request>", "</request>"),
	Response("<response>", "</response>"),
	
	/**
	 * the connection to the sensor or whatever
	 */
	IO_OPERATIONS("<io_operations>", "</io_operations>"),
	
	;
	
	private String startTag, endTag;
	
	public static final String OPTIONAL_DATA_KEY = "OPTIONAL_DATA";
	
	private ContentTags(String startTag, String endTag)
	{
		this.startTag = startTag;
		this.endTag = endTag;
	}

	public String getStartTag() {
		return startTag;
	}


	public String getEndTag() {
		return endTag;
	}
	
	public static StringBuilder bytesToString(byte[] bs) {
        StringBuilder s = new StringBuilder();
        for (byte b : bs) {
            s.append(String.format("%02X", b));
        }
        return s;
    }
	
	public static String hexStringToString(String hexStr)
	{
		StringBuilder output = new StringBuilder("");
	    for (int i = 0; i < hexStr.length(); i += 2) {
	        String str = hexStr.substring(i, i + 2);
	        output.append((char) Integer.parseInt(str, 16));
	    }
		return output.toString();
	}
	
	public static String getSequenceStartTag(int valueFromSequence)
	{
		return String.format("<sequence:%d>", valueFromSequence);
	}
	
	public static String getSequenceEndTag(int valueFromSequence)
	{
		return String.format("</sequence:%d>", valueFromSequence);
	}
	
	public static String getRawContent(String enclosedContent)
	{
		
		//TODO check ob vorhanden
		
		//danach den Inhalt aus dem string herauslösen
		
		// und zurückgeben
		String cleanContent = "";
	
		return cleanContent;
	}
	
}
