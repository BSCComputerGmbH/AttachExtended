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
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //TODO Methode die das null nach "oben" befördert bzw. die Fehlermeldung
        System.out.println("NFCReceiver#Adapter: " + nfcAdapter);
        if (nfcAdapter == null) {
            Toast.makeText(this, "No NFC", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        pendingIntent = PendingIntent.getActivity(this, 0, new android.content.Intent(this, this.getClass()).addFlags(
                android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);

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
            Parcelable[] parceableMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            receivedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            System.out.println("NFCReceiver#receivedTag " +  receivedTag);
          
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
                //sequence request
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
    		  //content of ndfmessage
    		  sb.append(getNdfMessageContent(ndfMessage));
    		  
	 		  NdefRecord[] records = ndfMessage.getRecords();
	 		  for(int x = 0; x < records.length; x++)
	 		  {
	 			  //start record
	 			  sb.append(ContentTags.NdefMessage_Record.getStartTag());
	 			  //content of ndfrecord
	 			  sb.append(getNdfRecordContent(records[x]));
	 			 
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
     * from NdefMessage object to String
     * @param ndfMessage
     * @return
     */
    private String getNdfMessageContent(NdefMessage ndfMessage)
    {
    	StringBuilder builder = new StringBuilder();
    	 
    	builder.append(ContentTags.NdefMessage_Description.getStartTag());
    	builder.append(ndfMessage.describeContents());
    	builder.append(ContentTags.NdefMessage_Description.getEndTag());
		  
    	builder.append(ContentTags.NdefMessage_RecordLength.getStartTag());
    	builder.append(""+ndfMessage.getRecords().length);
    	builder.append(ContentTags.NdefMessage_RecordLength.getEndTag());
    	
    	return builder.toString();
    }
    
    /**
     * from NdefRecord object to String
     * @param ndefRecord
     * @return
     */
    private String getNdfRecordContent(NdefRecord ndefRecord)
    {
    	StringBuilder builder = new StringBuilder();
    	
    	
		  //id hex string
    	builder.append(ContentTags.NdefRecord_id.getStartTag());
    	builder.append(ContentTags.bytesToString(ndefRecord.getId()));
    	builder.append(ContentTags.NdefRecord_id.getEndTag());
		  
		  //tnf as short value
    	builder.append(ContentTags.NdefRecord_tnf.getStartTag());
    	builder.append(ndefRecord.getTnf());
    	builder.append(ContentTags.NdefRecord_tnf.getEndTag());
		  
		  //typed hex string
    	builder.append(ContentTags.NdefRecord_type.getStartTag());
    	builder.append(ContentTags.bytesToString(ndefRecord.getType()));
    	builder.append(ContentTags.NdefRecord_type.getEndTag());
		  
		  //payload as hexstring
    	builder.append(ContentTags.NdefRecord_payload.getStartTag());
    	builder.append(ContentTags.bytesToString(ndefRecord.getPayload()));
    	builder.append(ContentTags.NdefRecord_payload.getEndTag());
		  
		  //mimetype as "clear" string
    	builder.append(ContentTags.NdefRecord_mimeType.getStartTag());
    	builder.append(ndefRecord.toMimeType());
    	builder.append(ContentTags.NdefRecord_mimeType.getEndTag());
    	
    	return builder.toString();
    	
    }
    
    
    
    private String getTaggedMessageString(NdefMessage[] message)
    {
    	
  	  	if (message == null || message.length == 0)
  	  		return ContentTags.Notification.getStartTag() + "No message received." + ContentTags.Notification.getEndTag();
  	  	
	  	StringBuilder sb = new StringBuilder();
	  	for(int i = 0; i < message.length; i++)
	  	{
	  		  NdefMessage ndfMessage = message[0];
	  		  
	  		  sb.append(getNdfMessageContent(ndfMessage));
	  		  
	 		  NdefRecord[] records = ndfMessage.getRecords();
	 		  for(int x = 0; x < records.length; x++)
	 		  {
	 			 //start record
	 			  sb.append(ContentTags.NdefMessage_Record.getStartTag());
	 			  sb.append(getNdfRecordContent(records[x]));
	 			
	 			  //end record
	 			  sb.append(ContentTags.NdefMessage_Record.getEndTag());
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
