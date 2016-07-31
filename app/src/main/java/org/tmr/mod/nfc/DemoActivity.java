package org.tmr.mod.nfc;

import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.widget.TextView;

import org.tmr.mod.R;


public class DemoActivity extends AppCompatActivity {
	
	private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private AlertDialog mDialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_demo);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.nfc_icon);
        //getSupportActionBar().setTitle(heading);
        getSupportActionBar().show();
		
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            showMessage(R.string.nfc_error, R.string.no_nfc);
            finish();
            return;
        } 
        //else {
        //	mAdapter.enableReaderMode(this, NfcAdapter.ReaderCallback, NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS, null);
        //}
        
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

	}
	
    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            if (!mAdapter.isEnabled()) {
                showWirelessSettingsDialog();
            }
            mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
        }
    }
    
    private void showWirelessSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.nfc_disabled);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.create().show();
    }

    private void showMessage(int title, int message) {
        mDialog.setTitle(title);
        mDialog.setMessage(getText(message));
        mDialog.show();
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
    }
    
    private void resolveIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {

            // Read raw id data
            byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);

            String nfcIdString = "empty";
            if (id != null && id.length > 0) {
                nfcIdString = Base64.encodeToString(id, Base64.DEFAULT).replace("\n", "").replace("\r", "");
            }

            // Read tag data
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            String nfcTagString = "empty";
            if (tag != null) nfcTagString = tag.toString();

            //DEBUG
            System.out.println("*** id : "+nfcIdString+" :***");
            System.out.println("*** tag: "+nfcTagString+" :***");

            TextView nfcRes = (TextView) findViewById(R.id.nfc_results);

            nfcRes.append("Tag ID: "+nfcIdString +"\n" + "Tag Data: "+nfcTagString + "\n");
            nfcRes.invalidate();
        }

    }

}
