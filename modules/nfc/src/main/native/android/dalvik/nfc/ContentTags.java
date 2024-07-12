package com.gluonhq.helloandroid.nfc;

/**
 * TODO hier und in der Applikation definiert
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

	;
	
	private String startTag, endTag;
	
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
	
	public static String getRawContent(String enclosedContent)
	{
		
		//TODO check ob vorhanden
		
		//danach den Inhalt aus dem string herauslösen
		
		// und zurückgeben
		String cleanContent = "";
	
		return cleanContent;
	}
	
}
