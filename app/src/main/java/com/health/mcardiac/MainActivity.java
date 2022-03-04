package com.health.mcardiac;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity {
    ProgressBar progBar;
    private Sensor   senAcc;
    private Button btnStart, btnStop;
    private TextView AccX, AccY, AccZ, AccTime;
    private Spinner activityValue, positionValue;
    DBHandler DBase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBase = new DBHandler(this);
        AccX = (TextView) findViewById(R.id.txtX);
        AccY = (TextView) findViewById(R.id.txtY);
        AccZ = (TextView) findViewById(R.id.txtZ);
        AccTime = (TextView) findViewById(R.id.txttime);
        activityValue = (Spinner) findViewById(R.id.spnactivity);
        positionValue = (Spinner) findViewById(R.id.spnposition);
        progBar = (ProgressBar) findViewById(R.id.progressBar);
        progBar.setVisibility(View.INVISIBLE);

        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*if (positionValue.getSelectedItem().equals("Select Position")) {
                    Toast.makeText(getApplicationContext(),"Please Select Phone Position",Toast.LENGTH_LONG).show();
                    return;
                }
                if (activityValue.getSelectedItem().equals("Select Activity")) {
                    Toast.makeText(getApplicationContext(),"Please Select Activity",Toast.LENGTH_LONG).show();
                    return;
                }*/

                startCollection();
                progBar.setVisibility(View.VISIBLE);
            }
        });
        btnStop = (Button) findViewById(R.id.btnStop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopCollection();
                progBar.setVisibility(View.GONE);

            }
        });
        checkStoragePermissions(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_file, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Handle item selection
        switch (item.getItemId()) {
            case R.id.exportdata:
                exportDB();
                Toast.makeText(getApplicationContext(),"Data Exported Successfully",Toast.LENGTH_LONG).show();
                return true;
            case R.id.closeApp:
                finish();
                return true;
            case R.id.deleteData:
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Deleting Activity")
                        .setMessage("Are you sure you want to delete records?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DBase.deleteData();
                                Toast.makeText(getApplicationContext(),"Records Deleted Successfully",Toast.LENGTH_LONG).show();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to close this app?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    public void startCollection() {
        String valActivity = activityValue.getSelectedItem().toString();
        String valPosition = positionValue.getSelectedItem().toString();
        Intent intent = new Intent(this, ActivityService.class);
        intent.putExtra("activity", valActivity);
        intent.putExtra("position", valPosition);
        startService(intent);
          }
    public void stopCollection() {
        Intent intent = new Intent(this, ActivityService.class);
        stopService(intent);
        }

        private void exportDB() {
        String valActivity = activityValue.getSelectedItem().toString();
        //File exportDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
         File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!exportDir.exists())
        {
            exportDir.mkdirs();
        }
        //File file = new File(exportDir, valActivity +".csv");
        File file = new File(exportDir, "Activity_tb.csv");
        try
        {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = DBase.getReadableDatabase();
            Cursor curCSV = db.rawQuery("SELECT Activity, Position, X, Y, Z, curtime, curdate FROM activity_tb",null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while(curCSV.moveToNext())
            {
                // columns to export
                String arrStr[] ={curCSV.getString(0), curCSV.getString(1), curCSV.getString(2),curCSV.getString(3),curCSV.getString(4),curCSV.getString(5),curCSV.getString(6) };
               csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
        }
        catch(Exception sqlEx)
        {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
        }    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void checkStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so promp the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
