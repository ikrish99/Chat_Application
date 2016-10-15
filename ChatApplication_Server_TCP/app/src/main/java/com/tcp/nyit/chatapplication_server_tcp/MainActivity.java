package com.tcp.nyit.chatapplication_server_tcp;

import java.io.DataInputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
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

    //Declares the variables
    boolean flag = false;
    Socket s;
    ServerSocket ss;
    String s_msg="",c_msg="";
    LinearLayout cPanel;
    Button connection;
    TextView sView,cView;
    EditText input,PORT;
    Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.ThreadPolicy tp = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(tp);
        }

        //Assigns the values from the activity_main panel
        cPanel = (LinearLayout) findViewById(R.id.c_panel);
        connection = (Button) findViewById(R.id.connect);
        sView = (TextView) findViewById(R.id.sview);
        cView = (TextView) findViewById(R.id.cview);
        input = (EditText) findViewById(R.id.input);
        PORT = (EditText) findViewById(R.id.port);
        send = (Button) findViewById(R.id.send);

        //On click listener decleration
        connection.setOnClickListener(connect);
        send.setOnClickListener(sendMsg);
    }

    //Call's the Socket class and declares the Server socket
    OnClickListener connect = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(PORT.getText().toString().equals(""))
            {
                Toast.makeText(MainActivity.this, "Enter the Port no.",
                        Toast.LENGTH_LONG).show();
            }
            else
            {
                try
                {
                    //Server socket is declared
                    ss= new ServerSocket();
                    ss.setReuseAddress(true);
                    ss.bind(new InetSocketAddress(Integer.parseInt(PORT.getText().toString())));

                    //Starts the socket class in Thread
                    (new connetion_socket()).start();

                    PORT.setEnabled(false);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Connection failed. Try with different Port no.",
                            Toast.LENGTH_LONG).show();
                }
            }
            sView.setText("");
        }
    };

    //Socket is declared and send Listener is used to send the message to Client
    OnClickListener sendMsg = new OnClickListener() {
        @Override
        public void onClick(View v) {
            send(s);
            input.setText("");
        }
    };

    public void send(Socket socket)
    {
        if (!input.getText().toString().equals(""))
        {
            try
            {
                String temp = "";

                //(new connetion_socket()).start();
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                if(input.getText().toString().contains("\n"))
                {
                    temp = "Server:" + input.getText().toString();
                }
                else
                {
                    temp = "Server:" + input.getText().toString()+"\n";
                }

                s_msg = temp + s_msg;
                //Sends the message to Client and displays the sent message in s_msg(Server View)
                out.write(temp);
                out.flush();

                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        sView.setText(s_msg);
                    }
                });
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Toast.makeText(MainActivity.this, "Type Message",
                    Toast.LENGTH_LONG).show();
        }
    }

    //Socket connection class
    public class connetion_socket extends Thread
    {
        public void run()
        {
            try
            {
                //Server socket is available to accept any client
                s=ss.accept();
                received_msg r = new received_msg(s);
                r.start();
            }catch (Exception e){e.printStackTrace();}
        }
    }

    //Receives message from Client.
    public class received_msg extends Thread
    {
        Socket soc;

        public received_msg(Socket socket)
        {
            this.soc = socket;
        }

        public void run() {
            try {
                DataInputStream in = new DataInputStream(soc.getInputStream());
                //Loops runs to read the message in socket, If it available
                while (!flag) {
                    if (in.available() > 0)
                    {
                        //If the message is available in Socket, It reads and displays in C_msg(Client view)
                        c_msg = in.readLine()+"\n"+c_msg;

                        MainActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                cView.setText(c_msg);
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