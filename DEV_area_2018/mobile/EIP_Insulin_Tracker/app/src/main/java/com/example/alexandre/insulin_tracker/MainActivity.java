package com.example.alexandre.insulin_tracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    String EntryFileName = "DataEntries.csv";
    String EmailSendTo = "alexandre.debeaumont@epitech.eu";
//    File InternalFile = new File(getFilesDir(), EntryFileName);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
//                        TODO: Mail sent > Delete
                        File InternalFile = new File(getFilesDir(), EntryFileName);
                        boolean deleted = InternalFile.delete();
                        if (!deleted) {
                            Toast tst = Toast.makeText(getApplicationContext(), "File failed to delete :[", Toast.LENGTH_LONG);
                            tst.show();
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
//                        TODO: Mail not sent > DO NOT DELETE
                        Toast tst = Toast.makeText(getApplicationContext(), "Data not deleted", Toast.LENGTH_LONG);
                        tst.show();
                        break;
                }
            }
        };
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Did you send the mail?\n(Yes will delete previous data)").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener);


//        SUBSTRACT INSULIN BUTTON
        FloatingActionButton substractInsulin = (FloatingActionButton) findViewById(R.id.SubstractInsulinButton);
        substractInsulin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView insulinInput = (TextView) findViewById(R.id.InsulinTakenInput);

                CharSequence value = insulinInput.getText();
                int intValue = Integer.parseInt(value.toString());
                if (intValue > 0) {
                    intValue--;
                    insulinInput.setText(Integer.toString(intValue));
                }
            }
        });

//        ADD INSULIN BUTTON
        FloatingActionButton addInsulin = (FloatingActionButton) findViewById(R.id.AddInsulinButton);
        addInsulin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView insulinInput = (TextView) findViewById(R.id.InsulinTakenInput);

                CharSequence value = insulinInput.getText();
                int intValue = Integer.parseInt(value.toString());
                intValue++;
                insulinInput.setText(Integer.toString(intValue));
            }
        });

//        AUTO GENERATE DATE BUTTON
        Button dateButton = (Button) findViewById(R.id.AutoDateButton);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView dateInput = (TextView) findViewById(R.id.InsulinDateInput);

                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd-HH:mm", Locale.FRANCE);
//                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy-HH:mm", Locale.FRANCE);
                String formattedDate = df.format(c);

                dateInput.setText(formattedDate);
            }
        });
//        SAVE ENTRY BUTTON
        Button saveEntry = (Button) findViewById(R.id.SaveEntryButton);
        saveEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File InternalFile = new File(getFilesDir(), EntryFileName);
                TextView InsulinUnitsTV = findViewById(R.id.InsulinTakenInput);
                TextView DateTV = findViewById(R.id.InsulinDateInput);
                TextView DescriptionTV = findViewById(R.id.DescriptionInputTextView);
                Toast tst = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);

//                Data Verification
                int insulinValue = Integer.parseInt(InsulinUnitsTV.getText().toString());
                if (insulinValue < 0) {
                    tst.setText("Insulin value cannot be negative !");
                    tst.show();
                    return;
                }
                if (DateTV.getText().toString().equals("")) {
                    tst.setText("Date cannot be empty, please use \"SET DATE TO NOW\" button");
                    tst.show();
                    return;
                }

                String data = InsulinUnitsTV.getText() + ";";
                data += DateTV.getText() + ";";
                data += DescriptionTV.getText() + "\n";


                try {
                    boolean stateInfo = InternalFile.exists();

                    if( !stateInfo ){
                        //stateInfo = fileStruct.mkdirs();
                        //textBox.append("\nmake dirs: " + stateInfo);
                        stateInfo = InternalFile.createNewFile();
                    }

                    stateInfo = InternalFile.setReadable( true , false);
                    stateInfo = InternalFile.setWritable( true, false );

                    FileOutputStream fOut = new FileOutputStream(InternalFile, true);
                    fOut.write(data.getBytes());
                    fOut.flush();
                    fOut.close();
                    tst.setText("Entry successfully saved !");
                    tst.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Snackbar.make(view, "ERROR : " + e.toString(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                }
//                File file = new File(getFilesDir(), "tmp_entries.csv");
//                Snackbar.make(view, "Current folder : " + file.getAbsolutePath(), Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

//        GENERATE CSV BUTTON
        Button sendCSVButton = (Button) findViewById(R.id.GenerateCSVButton);
        sendCSVButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File InternalFile = new File(getFilesDir(), EntryFileName);
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {EmailSendTo});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Insulin Data Entries");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Salut voila les données");

                if (!InternalFile.exists() || !InternalFile.canRead()) {
                    Toast tst = Toast.makeText(getApplicationContext(), "There is no data to send", Toast.LENGTH_SHORT);
                    tst.show();
                    return;
                }
                Uri uri = FileProvider.getUriForFile(getApplicationContext(), "com.example.alexandre.eip_insulin_tracker.fileprovider", InternalFile);
                emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
//                TODO: ask if he wants to delete
                builder.show();

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
