package c2.android_socket_client;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends Activity {

    public EditText et;
    public TextView tv;
    public String host = "192.168.168.15";
    public int port = 20001;
    public String tmp;
    public Socket socket_client;
    private Thread thread;
    private BufferedReader br;
    private BufferedWriter bw;
    public Handler handler = new Handler();
    public Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et = findViewById(R.id.editText);
        tv = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        StrictMode.ThreadPolicy tp = StrictMode.ThreadPolicy.LAX;
        StrictMode.setThreadPolicy(tp);
        thread = new Thread(readData);
        thread.start();
    }

    private View.OnClickListener btnlistener = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(socket_client.isConnected()){
                Log.e("text","write");
                try {
                    // 取得網路輸出串流
                    bw = new BufferedWriter( new OutputStreamWriter(socket_client.getOutputStream()));

                    // 寫入訊息
                    bw.write(et.getText()+"\n");

                    // 立即發送
                    bw.flush();
                } catch (IOException e) {
                    Log.e("text", e.toString());
                }
                // 將文字方塊清空
                et.setText("");
            }
        }

    };

    private Runnable readData = new Runnable() {
        public void run() {
            button.setOnClickListener(btnlistener);
            // server端的IP
            InetAddress serverIp;
            try {
                serverIp = InetAddress.getByName(host);
                socket_client = new Socket(serverIp, port);

                Log.e("text","connect");
                // 取得網路輸入串流
                br = new BufferedReader(new InputStreamReader(
                        socket_client.getInputStream()));

                // 當連線後
                while (socket_client.isConnected()) {
                    // 取得網路訊息
                    tmp = br.readLine();

                    // 如果不是空訊息則
                    if(tmp!=null)
                        // 顯示新的訊息
                        handler.post(updateText);
                }

            } catch (IOException e) {
                Log.e("text", e.toString());
            }
        }
    };

    private Runnable updateText = new Runnable() {
        public void run() {
            // 加入新訊息並換行
            tv.append(tmp + "\n");
        }
    };


}
