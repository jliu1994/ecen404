package com.example.jason.tamusmartinsole;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SyncStateContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    WebView myBrowser;

    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    ArrayList<Integer> bluetoothdata = new ArrayList<>();
    StringBuilder conclusion = new StringBuilder();
    ArrayList<Integer> graph;
    ArrayList<String> C = new ArrayList<>(Arrays.asList("5", "15","35","55","75","95"));
    String weightmapfilename;


    void findBT()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            //myLabel.setText("No bluetooth adapter available");
        }

        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                if(device.getName().equals("HC-06"))
                {
                    mmDevice = device;
                    //Toast.makeText(getApplicationContext(), "Found HC-06 in paired devices!!", Toast.LENGTH_LONG).show();
                    break;
                }
            }
        }
        //myLabel.setText("Bluetooth Device Found");
    }

    void openBT() throws IOException
    {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        beginListenForData();

        //myLabel.setText("Bluetooth Opened");
    }

    void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            if(data.length() == 31) {
                                                bluetoothdata.clear();
                                                for (int j = 0; j < 6; j++) {
                                                    int thousand = data.charAt(0 + j * 5) - '0';
                                                    int hundred = data.charAt(1 + j * 5) - '0';
                                                    int ten = data.charAt(2 + j * 5) - '0';
                                                    int single = data.charAt(3 + j * 5) - '0';
                                                    int input_bluetooth_int = 1000 * thousand + 100 * hundred + 10 * ten + single;
                                                    bluetoothdata.add(input_bluetooth_int);
                                                }

                                                //System.out.println(bluetoothdata.get(0) + " " + bluetoothdata.get(1)+" "+bluetoothdata.get(2)
                                                //        +" "+bluetoothdata.get(3)+" "+bluetoothdata.get(4)+" "+bluetoothdata.get(5));
                                                //转char到int
                                                C = ColorMatrix.colorMatrix(bluetoothdata);
                                                //System.out.println("我日");
                                                int avgWeight = Weightonfoot.weightonfoot(bluetoothdata);
                                                int walk = Heel_toe.heel_toe(bluetoothdata);
                                                int state = StateDecider.stateDecider(avgWeight, walk, 1);
                                                int posture = StateAction.StateAction(state, bluetoothdata, conclusion);
                                                graph = Grapher.grapher(posture);
                                            }
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    void closeBT() throws IOException
    {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
        //myLabel.setText("Bluetooth Closed");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        weightmapfilename = new File(getApplicationContext().getFilesDir(), "weightmap.html").getAbsolutePath();
        System.out.println(weightmapfilename);

        try {
            findBT();
            openBT();
        }catch(IOException ex){}

        setContentView(R.layout.activity_main);
        myBrowser=(WebView)findViewById(R.id.mybrowser);
        myBrowser.setClickable(true);
        myBrowser.getSettings().setJavaScriptEnabled(true);
        myBrowser.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // change path to phone storage
        //myBrowser.setWebViewClient(new WebViewClient());
        myBrowser.loadUrl( "file:///android_asset/mypage.html");
        myBrowser.setWebViewClient(new WebViewClient(){

            public boolean shouldOverrideUrlLoading(WebView view, String url){
                String string2="file:///android_asset/weight_map.html";
                String string3="file:///android_asset/correctness.html";
                if (url.equals(string2)){
                    view.loadUrl("file://"+weightmapfilename);//     change on april 15th
                    return true;
                }else if (url.equals(string3)){Toast.makeText(getApplicationContext(),conclusion.toString(),Toast.LENGTH_LONG).show();return true;}
                else{
                    return false;
                }

            }
        });






        Thread writedataT = new Thread(new Runnable() {
            @Override
            public void run() {
                //try{
                //Toast.makeText(getApplicationContext(),"thread running",Toast.LENGTH_LONG).show();}
                //catch (Exception e){}
                while (true) {
                    String one = C.get(0);
                    String two = C.get(1);
                    String three = C.get(2);
                    String four = C.get(3);
                    String five = C.get(4);
                    String six = C.get(5);
                    //Toast.makeText(getApplicationContext(),"thread running",Toast.LENGTH_LONG).show();
                    // write analyzed results into html file
                    // reload myBrowser
                    // Sleep 500 ms



                    //String weightmap="weightmap.html";
                    //new File(getFilesDir()+weightmap);
                   // Path currentRelativePath = Paths.get("");
                   // String s=currentRelativePath.toAbsolutePath().toString();
                   // System.out.println("current relative path is"+s);
                    //String strParentDirectory = weightmap.getParent();
                    //System.out.println("Parent directory is : " + strParentDirectory); //show the directory of new weightmap file
                    try {
                        BufferedWriter out = new BufferedWriter(new FileWriter(weightmapfilename));       //change on april 15th
                        out.write(" <html> <head> Weight Map </head> <body> <meta http-equiv=\"refresh\" content=\"1\"> <svg width=\"500\" height=\"800\"> <circle cx=\"200\" cy=\"100\" r=\"60\" stroke=\"green\" stroke-width=\"4\" fill=\"");
                        out.write(one);//correct
                        out.write("\" />");
                        out.write("<circle cx=\"195\" cy=\"450\" r=\"60\" stroke=\"green\" stroke-width=\"4\" fill=\"");
                        out.write(three);
                        out.write("\" />");
                        out.write("<circle cx=\"150\" cy=\"260\" r=\"60\" stroke=\"green\" stroke-width=\"4\" fill=\"");
                        out.write(two);
                        out.write("\" />");
                        out.write("<circle cx=\"250\" cy=\"700\" r=\"80\" stroke=\"green\" stroke-width=\"4\" fill=\"");
                        out.write(six); //correct
                        out.write("\" />");
                        out.write("<circle cx=\"400\" cy=\"300\" r=\"60\" stroke=\"green\" stroke-width=\"4\" fill=\"");
                        out.write(five);
                        out.write("\" />");
                        out.write("<circle cx=\"350\" cy=\"550\" r=\"60\" stroke=\"green\" stroke-width=\"4\" fill=\"");
                        out.write(four);//correct
                        out.write("\" />");
                        out.write("</svg> </body> </html>");
                        out.close();
                        //File wmf = new File(weightmapfilename);
                        //System.out.println("write weight map html"+ wmf.length());
                    } catch (Exception e) {
                        System.out.println("In writedata: "+e.getMessage());
                    }

                    try{Thread.sleep(1000);}catch(Exception e){}
                }
            }
        }
        );

        writedataT.start();

        //while (true){
        //   myBrowser.reload();
        //   try{
        //    Thread.sleep(1000);
        //    }
        //    catch(InterruptedException ie){}
        //}


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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myBrowser.canGoBack()){
            myBrowser.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event){
        if ((keyCode == KeyEvent.KEYCODE_FORWARD) && myBrowser.canGoForward()){
            myBrowser.goForward();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}

