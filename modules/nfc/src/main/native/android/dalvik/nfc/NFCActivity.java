package com.gluonhq.helloandroid.nfc;



import com.gluonhq.helloandroid.nfc.Intent.NFC;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;
import com.gluonhq.helloandroid.nfc.ContentTags;
import android.nfc.Tag;
import java.util.List;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.nfc.tech.TagTechnology;


//TODO Umbau so, dass alle Informationen wieder zur Applikation gebracht werden und nicht in dieser Ansicht angezeigt werden.
public class NFCActivity extends Activity
{

    private NfcAdapter nfcAdapter;

    private String optionalData;

	private Tag receivedTag;
    
    private PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        optionalData = intent.getStringExtra(ContentTags.OPTIONAL_DATA_KEY);
        
        System.out.println("NFCReceiver#optionaleData " + optionalData);
        
        // setContentView(R.layout.nfcreicever);

       // textView = findViewById(R.id.tag_viewer_text);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //TODO Methode die das null nach "oben" befördert bzw. die Fehlermeldung
        System.out.println("NFCReceiver ==> Adapter1: " + nfcAdapter);
        if (nfcAdapter == null) {
            Toast.makeText(this, "No NFC", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        System.out.println("NFCReceiver ==> Adapter2: " + nfcAdapter);
        pendingIntent = PendingIntent.getActivity(this, 0, new android.content.Intent(this, this.getClass()).addFlags(
                android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);

        /*
       ActivityResultLauncher<Intent> activity01Launcher =
                registerForActivityResult(new
                                ActivityResultContracts.StartActivityForResult(),
                        (result) -> {
                            System.out.println("result incoming " );
                        }
                );*/
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(nfcAdapter != null)
        {
            System.out.println("NFCReceiver#nfcAdapter isEnabled? " + nfcAdapter.isEnabled());
            if(!nfcAdapter.isEnabled())
            {

                showWirelessSettings();
            }

            System.out.println("AndroidTest onResume enableForegroundDispatch " + nfcAdapter);
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    private void showWirelessSettings()
    {
        android.content.Intent intent = new android.content.Intent(Settings.ACTION_WIRELESS_SETTINGS);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        resolveIntent(intent);
    }
    private void resolveIntent(Intent intent) {

        String action = intent.getAction();
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
        {
        	
        	
            System.out.println("NFCReceiver#resolveIntent intentAction " + getIntent().toString());
            System.out.println("NFCReceiver#resolveIntent if Abfrage " );
            Parcelable[] parceableMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            System.out.println("NFCReceiver#resolveIntent ==> parceableMessages " +  parceableMessages.length);
            receivedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            System.out.println("NFCReceiver#receivedTag ==> tag " +  receivedTag);
          
            NdefMessage[] msgs = null;

            if(parceableMessages != null)
            {
                msgs = new NdefMessage[parceableMessages.length];
                for(int i = 0; i < parceableMessages.length; i++)
                {
                    msgs[i] = (NdefMessage)parceableMessages[i];
                }
            }

            if(msgs != null)
            {
                Intent toSendIntent = new Intent(NFC.ACTION);
                toSendIntent.putExtra("requestCode", 10002);   
                
                if(optionalData.contains(ContentTags.SimpleRequestCall.getStartTag()))
                {
                	toSendIntent.putExtra("Nfc_Content", getTaggedMessageString(msgs));
                }
                else
                {
                	//way with request messages
                	String receivedMessageString = getResultFromNFCCommunication(msgs);
                	System.out.println("NFCReceiver#buildedString " + receivedMessageString);
                	toSendIntent.putExtra("Nfc_Content", receivedMessageString);
                }
              
                this.setResult(RESULT_OK, toSendIntent);
                //schliesst die Activity was auch notwendig ist, wenn man Bidirektionalität haben will
                this.finish();
            }
        }
        else
            System.out.println("NFCReceiver#resolveIntent ==> action " +  intent.toString());
    }
    
    
    private String getResultFromNFCCommunication(NdefMessage[] message)
    {
    	if (message == null || message.length == 0)
  	  		return ContentTags.Notification.getStartTag() + "No message received." + ContentTags.Notification.getEndTag();
    	
    	StringBuilder sb = new StringBuilder();
    	for(int i = 0; i < message.length; i++)
	  	{
    		  NdefMessage ndfMessage = message[0];
	  		  sb.append(ContentTags.NdefMessage_Description.getStartTag());
	  		  sb.append(ndfMessage.describeContents());
	  		  sb.append(ContentTags.NdefMessage_Description.getEndTag());
	  		  
	  		  sb.append(ContentTags.NdefMessage_RecordLength.getStartTag());
	 		  sb.append(""+ndfMessage.getRecords().length);
	 		  sb.append(ContentTags.NdefMessage_RecordLength.getEndTag());
	  		  
	 		  NdefRecord[] records = ndfMessage.getRecords();
	 		  for(int x = 0; x < records.length; x++)
	 		  {
	 			 //start record
	 			  sb.append(ContentTags.NdefMessage_Record.getStartTag());
	 			  //id hex string
	 			  sb.append(ContentTags.NdefRecord_id.getStartTag());
	 			  sb.append(ContentTags.bytesToString(records[x].getId()));
	 			  sb.append(ContentTags.NdefRecord_id.getEndTag());
	 			  
	 			  //tnf as short value
	 			  sb.append(ContentTags.NdefRecord_tnf.getStartTag());
	 			  sb.append(records[x].getTnf());
	 			  sb.append(ContentTags.NdefRecord_tnf.getEndTag());
	 			  
	 			  //typed hex string
	 			  sb.append(ContentTags.NdefRecord_type.getStartTag());
	 			  sb.append(ContentTags.bytesToString(records[x].getType()));
	 			  sb.append(ContentTags.NdefRecord_type.getEndTag());
	 			  
	 			  //payload as hexstring
	 			  sb.append(ContentTags.NdefRecord_payload.getStartTag());
	 			  sb.append(ContentTags.bytesToString(records[x].getPayload()));
	 			  sb.append(ContentTags.NdefRecord_payload.getEndTag());
	 			  
	 			  //mimetype as "clear" string
	 			  sb.append(ContentTags.NdefRecord_mimeType.getStartTag());
	 			  sb.append(records[x].toMimeType());
	 			  sb.append(ContentTags.NdefRecord_mimeType.getEndTag());
	 			
	 			 
	 			  
	 			  //send request sequence to nfc and add the response to the record field
	 			  //TODO?
	 			  if(records[x].toMimeType().contains("text/plain"))
	 			  {
	 				  TagTechnology tagTechnologyToUse = null;
	 				  
	 				  try
	 				  {
	 					  	TagTechnologyConstants constant = TagTechnologyConstants.getTagTechnology(optionalData);
	    					System.out.println("NFCReceiver#TagTechnology " + constant);
	    					
	    					switch(constant)
	    					{ 
		    					case NfcA:
		                            tagTechnologyToUse = NfcA.get(receivedTag);
		                            break;
		                        case NfcB:
		                            tagTechnologyToUse = NfcB.get(receivedTag);
		                            break;
		                        case NfcF:
		                            tagTechnologyToUse = NfcF.get(receivedTag);
		                            break;
		                        case NfcV:
		                            tagTechnologyToUse = NfcV.get(receivedTag);
		                            break;
		                        case IsoDep:
		                            tagTechnologyToUse = IsoDep.get(receivedTag);
		                            break;
		                        case MifareUltralight:
		                            tagTechnologyToUse = MifareUltralight.get(receivedTag);
		                            break;
		                        case MifareClassic:
		                            tagTechnologyToUse = MifareClassic.get(receivedTag);
		                            break;
	    					}
	    					
	    					if(tagTechnologyToUse != null)
	                        {
	    						System.out.println("NFCReceiver#tagTechnologyToUse " + tagTechnologyToUse);
	    						if(!tagTechnologyToUse.isConnected())
	                            {
	                                tagTechnologyToUse.connect();
	                            }
	    						System.out.println("NFCReceiver#tagTechnologyToUse.isConnected " + tagTechnologyToUse.isConnected());
	                            
	    						byte[] response = new byte[256];
	                            

	            				List<GenericPairVO<? extends ARequest, ? extends AResponse>> genericPairList = 
	            						RequestResponseDivier.getGenericPairList(optionalData);
	            				
	            				for(int z = 0; z < genericPairList.size(); z++)
	            				{
	            					ByteArrayRequest byteArrayRequest = (ByteArrayRequest)genericPairList.get(z).getLeft();
	            					
	                			    response = sendRequest(constant, tagTechnologyToUse, byteArrayRequest.getRequest());
	                			  
	                			    AResponse aResponse = genericPairList.get(z).getRight();
	            					if(aResponse.isExpectedResponseToCheck())
	            					{
	            						boolean isEquals = ((ByteArrayResponse)genericPairList.get(z).getRight()).isExpectedResponse(ByteArrayResponse.toObjectArray(response));
	                					System.out.println("NFCReceiver#isEquals " + isEquals);
	                					if(isEquals)
	                					{
	                						//nothing to do if equals
	                						System.out.println("NFCReceiver#IstGleich");
	                					}
	                					else
	                					{
	                						sb.append(ContentTags.NdefRecord_respone_error.getStartTag());
		            						sb.append("error expected response was different: ");
	                						sb.append(ContentTags.bytesToString(response).toString());
		            						sb.append(ContentTags.NdefRecord_respone_error.getEndTag());
	                					}
	            					}
	            					else
	            					{
	            						sb.append(ContentTags.NdefRecord_response_content.getStartTag());
	            						sb.append(ContentTags.bytesToString(response).toString());
	            						sb.append(ContentTags.NdefRecord_response_content.getEndTag());
	            					}
	            				}
	            				
	            				tagTechnologyToUse.close();
	                        }
	 					 
	 				  }
	 				  catch(Exception e)
	 				  {
		 					sb.append(ContentTags.NdefRecord_respone_error.getStartTag());
	 						sb.append("error stacktrace: ");
	 						sb.append(e.getMessage());
	 						sb.append(ContentTags.NdefRecord_respone_error.getEndTag());
	 				  }
	 				 
	 		
	 			  }
	 			  //end record
	 			 sb.append(ContentTags.NdefMessage_Record.getEndTag());
	 			  
	 		  }
    		
	  	}
    	
    	
    	//Complete return value
    	return sb.toString();
    }
    
    
    
    /**
     * test nfc communication 
     * @param message
     * @return
     */
    private String getResultFromNFCCommunicationOld(NdefMessage[] message)
    {
    	if (message == null || message.length == 0)
  	  		return ContentTags.Notification.getStartTag() + "No message received." + ContentTags.Notification.getEndTag();
    	
    	StringBuilder sb = new StringBuilder();
    	for(int i = 0; i < message.length; i++)
	  	{
    		NdefRecord[] records = message[i].getRecords();
    		for(int x = 0; x < records.length; x++)
	        {
    			//TODO?
    			if(records[x].toMimeType().contains("text/plain"))
	            {
    				TagTechnology tagTechnologyToUse = null;
    				
    				try
	            	{
    					TagTechnologyConstants constant = TagTechnologyConstants.getTagTechnology(optionalData);
    					System.out.println("NFCReceiver#TagTechnology " + constant);
    					
    					switch(constant)
    					{ 
	    					case NfcA:
	                            tagTechnologyToUse = NfcA.get(receivedTag);
	                            break;
	                        case NfcB:
	                            tagTechnologyToUse = NfcB.get(receivedTag);
	                            break;
	                        case NfcF:
	                            tagTechnologyToUse = NfcF.get(receivedTag);
	                            break;
	                        case NfcV:
	                            tagTechnologyToUse = NfcV.get(receivedTag);
	                            break;
	                        case IsoDep:
	                            tagTechnologyToUse = IsoDep.get(receivedTag);
	                            break;
	                        case MifareUltralight:
	                            tagTechnologyToUse = MifareUltralight.get(receivedTag);
	                            break;
	                        case MifareClassic:
	                            tagTechnologyToUse = MifareClassic.get(receivedTag);
	                            break;
    					}
    					
    					
    					if(tagTechnologyToUse != null)
                        {
    						System.out.println("NFCReceiver#tagTechnologyToUse " + tagTechnologyToUse);
    						if(!tagTechnologyToUse.isConnected())
                            {
                                tagTechnologyToUse.connect();
                            }
    						System.out.println("NFCReceiver#tagTechnologyToUse.isConnected " + tagTechnologyToUse.isConnected());
                            
    						byte[] response = new byte[256];
                            

            				List<GenericPairVO<? extends ARequest, ? extends AResponse>> genericPairList = 
            						RequestResponseDivier.getGenericPairList(optionalData);
            				
            				for(int z = 0; z < genericPairList.size(); z++)
            				{
            					ByteArrayRequest byteArrayRequest = (ByteArrayRequest)genericPairList.get(z).getLeft();
            					
                			    response = sendRequest(constant, tagTechnologyToUse, byteArrayRequest.getRequest());
                			  
                			    AResponse aResponse = genericPairList.get(z).getRight();
            					if(aResponse.isExpectedResponseToCheck())
            					{
            						boolean isEquals = ((ByteArrayResponse)genericPairList.get(z).getRight()).isExpectedResponse(ByteArrayResponse.toObjectArray(response));
                					System.out.println("NFCReceiver#isEquals " + isEquals);
                					if(isEquals)
                					{
                						//nothing to do if equals
                						System.out.println("NFCReceiver#IstGleich");
                					}
                					else
                					{
                						//TODO error or whate?
                					}
            					}
            					else
            					{
            						return ContentTags.bytesToString(response).toString();
            					}
            				}
            				
            				tagTechnologyToUse.close();
                        }
    					
	            	}
    				catch(Exception e)
    				{
    					e.printStackTrace();
    					
    				}
    			}
    		}
    		
	  	}
    	return "TODO";
    	
    }
    
    private String getTaggedMessageString(NdefMessage[] message)
    {
    	
  	  	if (message == null || message.length == 0)
  	  		return ContentTags.Notification.getStartTag() + "No message received." + ContentTags.Notification.getEndTag();
  	  	
	  	StringBuilder sb = new StringBuilder();
	  	for(int i = 0; i < message.length; i++)
	  	{
	  		  NdefMessage ndfMessage = message[0];
	  		  sb.append(ContentTags.NdefMessage_Description.getStartTag());
	  		  sb.append(ndfMessage.describeContents());
	  		  sb.append(ContentTags.NdefMessage_Description.getEndTag());
	  		  
	  		  sb.append(ContentTags.NdefMessage_RecordLength.getStartTag());
	 		  sb.append(""+ndfMessage.getRecords().length);
	 		  sb.append(ContentTags.NdefMessage_RecordLength.getEndTag());
	  		  
	 		  NdefRecord[] records = ndfMessage.getRecords();
	 		  for(int x = 0; x < records.length; x++)
	 		  {
	 			  //start record
	 			  sb.append(ContentTags.NdefMessage_Record.getStartTag());
	 			  //id hex string
	 			  sb.append(ContentTags.NdefRecord_id.getStartTag());
	 			  sb.append(ContentTags.bytesToString(records[x].getId()));
	 			  sb.append(ContentTags.NdefRecord_id.getEndTag());
	 			  
	 			  //tnf as short value
	 			  sb.append(ContentTags.NdefRecord_tnf.getStartTag());
	 			  sb.append(records[x].getTnf());
	 			  sb.append(ContentTags.NdefRecord_tnf.getEndTag());
	 			  
	 			  //typed hex string
	 			  sb.append(ContentTags.NdefRecord_type.getStartTag());
	 			  sb.append(ContentTags.bytesToString(records[x].getType()));
	 			  sb.append(ContentTags.NdefRecord_type.getEndTag());
	 			  
	 			  //payload as hexstring
	 			  sb.append(ContentTags.NdefRecord_payload.getStartTag());
	 			  sb.append(ContentTags.bytesToString(records[x].getPayload()));
	 			  sb.append(ContentTags.NdefRecord_payload.getEndTag());
	 			  
	 			  //mimetype as "clear" string
	 			  sb.append(ContentTags.NdefRecord_mimeType.getStartTag());
	 			  sb.append(records[x].toMimeType());
	 			  sb.append(ContentTags.NdefRecord_mimeType.getEndTag());
	 			  //end record
	 			  sb.append(ContentTags.NdefMessage_Record.getEndTag());
	 		  }
	  		  
	  	}
	  	return sb.toString();

    }
    
    
    
    
    
    
    //TODO how to build the message for transfer to the application
    private String getStringMessage(NdefMessage[] message)
    {
    	  if (message == null || message.length == 0)
              return "no Message received";
          StringBuilder sb = new StringBuilder();
          
          for(int i = 0; i < message.length; i++)
          {
        	  NdefMessage ndfMessage = message[0];
              sb.append("Describe Content: " + ndfMessage.describeContents() + " record length " + ndfMessage.getRecords().length);
              sb.append(" ");
             
              
              NdefRecord[] records = ndfMessage.getRecords();
              //TODO eigentlich müssten hie die Records weitergeben werden
              
              System.out.println("getStringMessage > " + records.length);
              for(int x = 0; x < records.length; x++)
              {
                  String plainTextPayload = ""+new String(records[x].getPayload());
                  sb.append("Record==> ");
                  sb.append(plainTextPayload);
                  sb.append(" <==Record");
                  sb.append(" ");
              }
        	  
          }
          return sb.toString();
    }
    
    /**
     * creepy method to send the bytes to the sensor; 
     * <br>Problem is that the abstract BasicTagTechnology class with the transceive method  not public is
     * @param constant
     * @param tagTechnologyToUse
     * @param bytesToSend
     * @return
     */
    private byte[] sendRequest(TagTechnologyConstants constant, TagTechnology tagTechnologyToUse, byte[] bytesToSend)
    {
        byte[] responseValue = new byte[256];
        try
        {
            switch(constant)
            {
                case NfcA:
                    responseValue = ((NfcA)tagTechnologyToUse).transceive(bytesToSend);
                    break;
                case NfcB:
                    responseValue = ((NfcB)tagTechnologyToUse).transceive(bytesToSend);
                    break;
                case NfcF:
                    responseValue = ((NfcF)tagTechnologyToUse).transceive(bytesToSend);
                    break;
                case NfcV:
                    responseValue = ((NfcV)tagTechnologyToUse).transceive(bytesToSend);
                    break;
                case IsoDep:
                    responseValue = ((IsoDep)tagTechnologyToUse).transceive(bytesToSend);
                    break;
                case MifareUltralight:
                    responseValue = ((MifareUltralight)tagTechnologyToUse).transceive(bytesToSend);
                    break;
                case MifareClassic:
                    responseValue = ((MifareClassic)tagTechnologyToUse).transceive(bytesToSend);
                    break;

            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return responseValue;
    }
}
