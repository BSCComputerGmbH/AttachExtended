package com.gluonhq.helloandroid.nfc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

//import com.bscgmbh.logic.tools.GenericPairVO;
//import com.gluonhq.charm.down.plugins.nfc.INFCListener;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
//import javafxports.android.FXActivity;

public class NFCReceiver extends Activity
{
	
	//public static List<INFCListener> obsList = new ArrayList<INFCListener>();
	
	private NfcAdapter nfcAdapter;
	private PendingIntent pendingIntent;
	
	private String optionalerPinCode;
	
	private Tag receivedTag;

	private TextView contentView;

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        Intent intet = getIntent();
	        optionalerPinCode = intet.getStringExtra("PIN");
	        
	        
	        //TODO aufhübschen
	        LinearLayout.LayoutParams layoutParamsItem = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT); 
			layoutParamsItem.rightMargin = 6;
			layoutParamsItem.leftMargin = 6;
	        
			LinearLayout ersteZeile = new LinearLayout(this);
			ersteZeile.setBackgroundColor(Color.BLACK);
			ersteZeile.setId(100);
	        
			contentView = new TextView(this);
			contentView.setGravity(Gravity.CENTER_VERTICAL);
			contentView.setTextColor(Color.WHITE);
			//TODO language
			contentView.setText("...warte auf NFC Daten...");
			ersteZeile.addView(contentView);
			
			this.setContentView(ersteZeile);
			
	        
	        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
	       
	        if(nfcAdapter == null)
	        {
	        	System.out.println("AndroidTest nfc ist null");
	        	return;
	        }
	        System.out.println("AndroidTest nfcEnabled?  " + nfcAdapter.isEnabled());
	        
	        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(
	                Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
	    }

	    @Override
	    protected void onPause()
	    {
	        System.out.println("AndroidTest onPause");
	        super.onPause();
	        if(nfcAdapter != null)
	        {
	            System.out.println("AndroidTest onPause disableForegroundDispatch");
	            nfcAdapter.disableForegroundDispatch(this);
	        }
	    }


	    @Override
	    protected void onResume()
	    {
	        System.out.println("AndroidTest onResume");
	        super.onResume();
	        if(nfcAdapter != null)
	        {
		        System.out.println("AndroidTest onResume Abfrage nicht enabled " + nfcAdapter.isEnabled());
		        if(!nfcAdapter.isEnabled())
		            showWirelessSettings();

		        System.out.println("AndroidTest onResume enableForegroundDispatch " + nfcAdapter);
		        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
	        }
	    }

	    private void showWirelessSettings()
	    {
	    	Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
			/*FXActivity.getInstance().*/startActivity(intent);
	    }


	    @Override
	    protected void onNewIntent(Intent intent)
	    {
	        setIntent(intent);
	        resolveIntent(intent);
	        super.onNewIntent(intent);
	    }

	    private void resolveIntent(Intent intent)
	    {
	        String action = intent.getAction();
	        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
	            || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
	            || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
	        {
	            Parcelable[] rawMsg = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
	             NdefMessage[] msgs = null;
	            if(rawMsg != null)
	            {
	                msgs = new NdefMessage[rawMsg.length];
	                for(int i = 0; i < rawMsg.length; i++)
	                {
	                    msgs[i] = (NdefMessage)rawMsg[i];

	                }
	            }
	            else
	            {
	              //  textView.setText("else zweig ");
	                byte[] empty = new byte[0];
	                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
	                Tag tag = (Tag)intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
	                byte[] payload = dumpTagData(tag).getBytes();
	                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
	                NdefMessage msg = new NdefMessage(new NdefRecord[] {record});
	                msgs = new NdefMessage[]{msg};
	            }
	            Tag tagZwei = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
	            receivedTag = tagZwei;
	            StringBuilder sb = new StringBuilder();
	            for(int z = 0; z < tagZwei.getTechList().length; z++)
	            {
	            	//sb.append(""+ tagZwei.getTechList()[z] + " ID " + Tools.toHexString(tagZwei.getId()) + " content " +   tagZwei.describeContents());
	                //sb.append("\n");
	            }
	           // textViewTop.setText(sb.toString());
	            displayMsgs(msgs);

	        }
	    }

	    private void displayMsgs(NdefMessage[] msgs)
	    {
	        if(msgs == null || msgs.length == 0)
	            return;
	        //textView.setText("displayMsgs " + msgs.length);


	        StringBuilder build = new StringBuilder();
	       // List<ParsedNdefRecord> records;
	        StringBuilder sb = new StringBuilder();
	        NdefMessage message = msgs[0];

	        sb.append("Describe Content: " + message.describeContents());
	        sb.append('\n');


	        NdefRecord[] records = message.getRecords();
	        sb.append("NdefRecord Length : " + records.length);
	        sb.append('\n');
	        for(int x = 0; x < records.length; x++)
	        {
	            //alle Payloads die plain/text sind werden weitergeleitet
	            
	            if(records[x].toMimeType().contains("text/plain"))
	            {
	            	String plainTextPayload = ""+new String(records[x].getPayload());
	            	
	            	
	            	//GenericPairVO<String, String> weitergabe = new GenericPairVO<String, String>(plainTextPayload, "");
	            	//Standardpincode für das STM550
	            	byte[] pinCode = new byte[] {0x00, 0x00, (byte) 0xE5, 0x00};
	            	
	            	if(optionalerPinCode != null && optionalerPinCode.length() > 0)
	            	{
	            		//TODO Umwandlung des String in ein byte[]
	            		//Vorsicht wegen LSB und MSB!
	            		//LSB = index 3
	            	}
	            	
	            	try
	            	{
	            		if(receivedTag != null)
	            		{
	            			MifareUltralight mu = MifareUltralight.get(receivedTag);
	            			
	            			if(!mu.isConnected())
	            			{
	            				mu.connect();
	            			}
	            			byte[] response = new byte[250];
	            		    //TODO Eingabefeld für Veränderungen muss noch auf die View
	            			//Authentifikation
	            		    response  = mu.transceive(new byte[] {
	            	                (byte)0x1B,  // CMD = AUTHENTICATE 1B 
	            	                (byte)pinCode[0],
	            	                (byte)pinCode[1],
	            	                (byte)pinCode[2],
	            	                (byte)pinCode[3],

	            	        });
	            		    //Zugriff auf die entsprechende Page
	            		    response = mu.transceive(new byte[] {
	            	                (byte)0x30,
	            	                //Die Adresszahl funktioniert nur bei dem ersten Produkt von EnOcean, bei
	            	                //anderen Produkten kann es anders sein.
	            	                (byte)0x49
	            	        });
	            		    //wird in HexString gewandelt, damit man es auf der Serverseite leichter verarbeiten kann.
	            		    String hexString = toHexString(response);
	            		    //weitergabe.setRight(hexString);
	            			
	            		}
	            	}
	            	catch(Exception e)
	            	{
	            		e.printStackTrace();
	            		
	            	}
	            	
	            	/*
	            	Iterator<INFCListener> iterator = obsList.iterator();
	            	while(iterator.hasNext())
	            	{
	            		iterator.next().fireNewMessage(weitergabe);
	            	}
	            	*/
	            }
	        }
	        contentView.setText(sb.toString());
	    }

	    private String dumpTagData(Tag tag)
	    {
	        StringBuilder sb = new StringBuilder();
	        byte[] id = tag.getId();

	        return sb.toString();
	    }
/*
		public static void addStringListener(INFCListener iStringListener) 
		{
			obsList.add(iStringListener);
		}

		public static void removeStringListener(INFCListener iStringListener) 
		{
			obsList.remove(iStringListener);
		}
		*/
		public static String toHexString(byte[] data)
	    {
	        if (data == null)
	            return new String();

	        StringBuffer buffer = new StringBuffer();

	        for (int i = 0; i < data.length; i++)
	        {
	            buffer.append(toHexString(data[i]));
	         
	        }

	        return buffer.toString();
	    }
		
		public static String toHexString(byte value)
	    {
	        char[] chars = new char[2];
	        chars[0] = digits[(value >>> 4) & 0xF];
	        chars[1] = digits[value & 0x0F];
	        return new String(chars);
	    }
		
		private final static char[] digits =
		{
            '0' , '1' , '2' , '3' , '4' , '5' ,
            '6' , '7' , '8' , '9' , 'A' , 'B' ,
            'C' , 'D' , 'E' , 'F'
		};
}
