package de.bsc.nfc_testandroid.android_native;

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

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import de.bsc.nfc_testandroid.R;
//TODO Umbau so, dass alle Informationen wieder zur Applikation gebracht werden und nicht in dieser Ansicht angezeigt werden.
public class NFCActivity extends ComponentActivity
{

    private NfcAdapter nfcAdapter;

    private TextView textView;

    private PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfcreicever);

        textView = findViewById(R.id.tag_viewer_text);

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



        ActivityResultLauncher<Intent> activity01Launcher =
                registerForActivityResult(new
                                ActivityResultContracts.StartActivityForResult(),
                        (result) -> {
                            System.out.println("result incoming " );
                        }
                );
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("AndroidTest onPause");
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
                System.out.println("NFCReceiver#resolveIntent ==> msgs " +  msgs.length);
                //TODO messages
               // onNewIntent(new Intent("test"));
               // activity.setResult(Activity.RESULT_OK, (Intent) message.obj);
               //activity.finish();
                Intent toSendIntent = new Intent("test");
                toSendIntent.putExtra("Parameter1", "Parameter2");

                this.setResult(RESULT_OK, toSendIntent);
                //schliesst die Activity was auch notwendig ist, wenn man Bidirektionalität haben will
                this.finish();
            }


            /* Aufbereitung für direkte Anzeige auf Activity
            if(msgs != null)
                displayMsgs(msgs);
            */
        }
        else
            System.out.println("NFCReceiver#resolveIntent ==> action " +  intent.toString());


    }


    private void displayMsgs(NdefMessage[] message) {
        if (message == null || message.length == 0)
            return;
        StringBuilder sb = new StringBuilder();


        for(int i = 0; i < message.length; i++)
        {
            NdefMessage ndfMessage = message[0];
            sb.append("Describe Content: " + ndfMessage.describeContents() + " record length " + ndfMessage.getRecords().length);
            sb.append('\n');

            NdefRecord[] records = ndfMessage.getRecords();
            //TODO eigentlich müssten hie die Records weitergeben werden

            for(int x = 0; x < records.length; x++)
            {

                String plainTextPayload = ""+new String(records[x].getPayload());
                sb.append("Record==> ");
                sb.append(plainTextPayload);
                sb.append(" <==Record");
                sb.append('\n');

                // GenericPairVO<String, String> weitergabe = new GenericPairVO<String, String>(plainTextPayload, "");
            }

            //textView.setText(message.toString());
        }
        textView.setText(sb.toString());
    }

}
