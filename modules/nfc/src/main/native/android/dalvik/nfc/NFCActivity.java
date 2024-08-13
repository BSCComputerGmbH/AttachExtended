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


//TODO Umbau so, dass alle Informationen wieder zur Applikation gebracht werden und nicht in dieser Ansicht angezeigt werden.
public class NFCActivity extends Activity
{

    private NfcAdapter nfcAdapter;

    private String optionalData;

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
                //toSendIntent.putExtra("Nfc_Content", getStringMessage(msgs));
                
                if(optionalData.contains(ContentTags.SimpleRequestCall.getStartTag()))
                {
                	System.out.println("NFCReceiver#SimpleRequestCall");
                	toSendIntent.putExtra("Nfc_Content", getTaggedMessageString(msgs));
                }
                else
                {
                	System.out.println("NFCReceiver#SequenceRequestCall " + optionalData);
                	
                	
                	
                }
              
                this.setResult(RESULT_OK, toSendIntent);
                //schliesst die Activity was auch notwendig ist, wenn man Bidirektionalität haben will
                this.finish();
            }
        }
        else
            System.out.println("NFCReceiver#resolveIntent ==> action " +  intent.toString());
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
}
