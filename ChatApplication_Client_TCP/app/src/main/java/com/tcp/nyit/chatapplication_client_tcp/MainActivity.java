package com.tcp.nyit.chatapplication_client_tcp;

import java.io.DataInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //Declaring the variables
    boolean flag = false;
    Socket s = null;
    String c_msg="",s_msg="";
    LinearLayout lPanel, cPanel;
    EditText port,address;
    Button connection;
    TextView sView,cView;
    EditText input;
    Button send;
    Button disConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.ThreadPolicy tp = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(tp);
        }

        //Assigning the activity_main values
        lPanel = (LinearLayout) findViewById(R.id.lpanel);
        cPanel = (LinearLayout) findViewById(R.id.c_panel);
        port = (EditText) findViewById(R.id.port);
        address = (EditText) findViewById(R.id.address);
        connection = (Button) findViewById(R.id.connect);
        disConnect = (Button) findViewById(R.id.disconnect);
        sView = (TextView) findViewById(R.id.sview);
        cView = (TextView) findViewById(R.id.cview);
        input = (EditText) findViewById(R.id.input);
        send = (Button) findViewById(R.id.send);

        //Declaration of Listeners
        connection.setOnClickListener(connect);
        disConnect.setOnClickListener(dConnect);
        send.setOnClickListener(sendMsg);
    }


    //Disconnection Listener
    OnClickListener dConnect = new OnClickListener() {
        @Override
        public void onClick(View v) {
            flag = true;
            sView.setText("");
            cView.setText("");
            input.setText("");
            lPanel.setVisibility(View.VISIBLE);
            cPanel.setVisibility(View.GONE);
        }
    };

    //Connection Listener
    OnClickListener connect = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(port.getText().toString().equals(""))
            {
                //Null check for port number
                Toast.makeText(MainActivity.this, "Enter Port no.",
                        Toast.LENGTH_LONG).show();
            }
            else if(address.getText().toString().equals(""))
            {
                //Null check for IP address
                Toast.makeText(MainActivity.this, "Enter IP Address",
                        Toast.LENGTH_LONG).show();
            }
            else
            {
                try
                {
                    //Call's the connection thread
                    (new connection_socket()).start();
                    sView.setText("");
                    cView.setText("");
                    lPanel.setVisibility(View.GONE);
                    cPanel.setVisibility(View.VISIBLE);
                }
                catch (Exception e)
                {
                    e.printStackTrace();

                    Toast.makeText(MainActivity.this, "Connection Failed",
                            Toast.LENGTH_LONG).show();

                    lPanel.setVisibility(View.VISIBLE);
                    cPanel.setVisibility(View.GONE);
                }
            }
        }
    };

    //Send message to server Listener
    OnClickListener sendMsg = new OnClickListener() {
        @Override
        public void onClick(View v)
        {
            send();
        }
    };


    public void send()
    {
        if(input.getText().toString().equals(""))
        {
            //Null check for inout message
            Toast.makeText(MainActivity.this, "Type Message",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            try
            {
                String temp;
                //(new connection_socket()).start();
                PrintWriter out = new PrintWriter(s.getOutputStream(),true);
                if(input.getText().toString().contains("\n"))
                {
                    temp = "Client:"+input.getText().toString();
                }
                else
                {
                    temp = "Client:"+input.getText().toString()+"\n";
                }
                c_msg = temp + c_msg;
                //Writes the message to server and displays in c_msg(Client view)
                out.write(temp);
                out.flush();

                    MainActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            cView.setText(c_msg);
                        }
                    });
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        input.setText("");
    }

    //Connection socket class
    public class connection_socket extends Thread
    {
        public void run()
        {
            try {
                //Connects to Server socket with the ip address and port number
                s = new Socket(address.getText().toString(), Integer.parseInt(port.getText().toString()));
                receive_msg r = new receive_msg(s);
                r.start();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    //Receives message from Server class
    public class receive_msg extends Thread
    {
        Socket soc_ket;
        public receive_msg(Socket soc)
        {
            this.soc_ket=soc;
        }
        public void run() {
            try {
                DataInputStream in = new DataInputStream(soc_ket.getInputStream());

                //Reads the socket for input message from server and displays in s_view(Server View)
                while (!flag) {
                    if (in.available() > 0) {

                        s_msg = in.readLine()+"\n"+s_msg;

                        MainActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                sView.setText(s_msg);
                            }
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}