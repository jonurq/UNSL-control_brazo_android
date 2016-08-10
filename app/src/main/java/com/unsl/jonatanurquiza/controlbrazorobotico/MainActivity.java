package com.unsl.jonatanurquiza.controlbrazorobotico;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jmedeisis.bugstick.Joystick;
import com.jmedeisis.bugstick.JoystickListener;

public class MainActivity extends Activity {



    private final int init_posA = 90;
    private final int init_posB = 135;
    private final int init_posD = 80;
    private final int init_posE = 75;
    private final int init_posF = 85;
    private final int init_posG = 70;

    private int posA = init_posA; //Servo A1 y A2
    private int posB = init_posB; //Servo B y C
    private int posD = init_posD;
    private int posE = init_posE;
    private int posF = init_posF;
    private int posG = init_posG;

    int fail = 0;
    Button  CenterLeftButton, CenterRightButton,
            DesconectarButton;
    Joystick joystick,joystick1;
    Handler bluetoothIn;
    SeekBar seekBar;
    float j_degrees;
    float j_offset;
    float j1_degrees;
    float j1_offset;

    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;

    private ConnectedThread mConnectedThread;


    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String para MAC address
    private static String address = null;

    private Timer timer1 = new Timer();
    private Timer timer2 = new Timer();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        CenterLeftButton = (Button) findViewById(R.id.CenterLeft);
        CenterRightButton = (Button) findViewById(R.id.CenterRight);
        DesconectarButton = (Button) findViewById(R.id.Desconectar);
        joystick = (Joystick) findViewById(R.id.joystick);
        joystick1 = (Joystick) findViewById(R.id.joystick1);

        seekBar = (SeekBar) findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                posG = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });




        joystick.setJoystickListener(new JoystickListener() {
            @Override
            public void onDown() {
            }

            @Override
            public void onDrag(float degrees, float offset) {
                j_degrees = degrees;
                j_offset = offset;

            }

            @Override
            public void onUp() {
                j_degrees = 0;
                j_offset = 0;
            }
        });


        joystick1.setJoystickListener(new JoystickListener() {
            @Override
            public void onDown() {
            }

            @Override
            public void onDrag(float degrees, float offset) {
                j1_degrees = degrees;
                j1_offset = offset;

            }

            @Override
            public void onUp() {
                j1_degrees = 0;
                j1_offset = 0;
            }
        });

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // obtiene adapatador Bluetooth
        checkBTState();


        timer1 = new Timer();
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }

        }, 0, 30);



        DesconectarButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                timer1.cancel();
                timer2 = new Timer();
                timer2.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        TimerMethod2();
                    }

                }, 0, 30);
            }
        });



    }


    private void TimerMethod()
    {
        this.runOnUiThread(Timer_Tick);
    }

    private Runnable Timer_Tick = new Runnable() {
        @Override
        public void run() {


            if (CenterLeftButton.isPressed()) {
                if (posF - 2 >= 5)
                    posF -= 2;
            }
            if (CenterRightButton.isPressed()) {
                if (posF + 2 <= 165)
                    posF += 2;
            }

            int step=1;

            if(j_offset!=0) {
                //Derecha
                if (-22.5 < j_degrees && j_degrees <= 22.5) {
                    if (posA + step <= 180)
                        posA += step;
                }
                //Derecha Arriba
                else if (22.5 < j_degrees && j_degrees <= 67.5) {
                    if (posA + step <= 180)
                        posA += step;
                    if (posB + step <= 135)
                        posB += step;
                }
                //Arriba
                else if (67.5 < j_degrees && j_degrees <= 112.5) {
                    if (posB + step <= 135)
                        posB += step;
                }
                //Izquierda Arriba
                else if (112.5 < j_degrees && j_degrees <= 157.5) {
                    if (posA - step >= 0)
                        posA -= step;
                    if (posB + step <= 135)
                        posB += step;
                }
                //Izquierda
                else if ((157.5 < j_degrees && j_degrees <= 180) || (-157.5 > j_degrees && j_degrees >= -180)) {
                    if (posA - step >= 0)
                        posA -= step;
                }
                //Izquierda Abajo
                else if ((-112.5 > j_degrees && j_degrees >= -157.5)) {
                    if (posA - step >= 0)
                        posA -= step;
                    if (posB - step >= 0)
                        posB -= step;
                }
                //Abajo
                else if ((-67.5 > j_degrees && j_degrees >= -112.5)) {
                    if (posB - step >= 0)
                        posB -= step;
                }
                //Derecha Abajo
                else if ((-22.5 > j_degrees && j_degrees >= -67.5)) {
                    if (posA + step <= 180)
                        posA += step;
                    if (posB - step >= 0)
                        posB -= step;
                }

            }
            if(j1_offset>0.5)
                step=2;
           if(j1_offset!=0) {
                //Derecha
                if (-22.5 < j1_degrees && j1_degrees <= 22.5) {
                    if (posD + step <= 145)
                        posD += step;
                }
                //Derecha Arriba
                else if (22.5 < j1_degrees && j1_degrees <= 67.5) {
                    if (posD + step <= 145)
                        posD += step;
                    if (posE + step <= 145)
                        posE += step;
                }
                //Arriba
                else if (67.5 < j1_degrees && j1_degrees <= 112.5) {
                    if (posE + step <= 145)
                        posE += step;
                }
                //Izquierda Arriba
                else if (112.5 < j1_degrees && j1_degrees <= 157.5) {
                    if (posE + step <= 145)
                        posE += step;
                    if (posD - step >= 20)
                        posD -= step;
                }
                //Izquierda
                else if ((157.5 < j1_degrees && j1_degrees <= 180) || (-157.5 > j1_degrees && j1_degrees >= -180)) {
                    if (posD - step >= 20)
                        posD -= step;
                }
                //Izquierda Abajo
                else if ((-112.5 > j1_degrees && j1_degrees >= -157.5)) {
                    if (posD - step >= 20)
                        posD -= step;
                    if (posE - step >= 5)
                        posE -= step;
                }
                //Abajo
                else if ((-67.5 > j1_degrees && j1_degrees >= -112.5)) {
                    if (posE - step >= 5)
                        posE -= step;
                }
                //Derecha Abajo
                else if ((-22.5 > j1_degrees && j1_degrees >= -67.5)) {
                    if (posE - step >= 5)
                        posE -= step;
                    if (posD + step <= 145)
                        posD += step;

                }

            }


            byte[] data = new byte[]{(byte) posA, (byte) posB, (byte) posD, (byte) posE, (byte) posF, (byte) posG};
            mConnectedThread.write(data);
        }
    };



    private void TimerMethod2()
    {
        this.runOnUiThread(Timer_Tick2);
    }

    private Runnable Timer_Tick2 = new Runnable() {
        @Override
        public void run() {
            if (posA < init_posA)
            {
                posA++;
            }
            else if (posA > init_posA)
            {
                posA--;
            }
            if (posB < init_posB)
            {
                posB++;
            }
            else if (posB > init_posB)
            {
                posB--;
            }
            if (posD < init_posD)
            {
                posD++;
            }
            else if (posD > init_posD)
            {
                posD--;
            }
            if (posE < init_posE)
            {
                posE++;
            }
            else if (posE > init_posE)
            {
                posE--;
            }
            if (posF < init_posF)
            {
                posF++;
            }
            else if (posF > init_posF)
            {
                posF--;
            }
            if (posG < init_posG)
            {
                posG++;
            }
            else if (posG > init_posG)
            {
                posG--;
            }


            byte[] data = new byte[]{(byte) posA, (byte) posB, (byte) posD, (byte) posE, (byte) posF, (byte) posG};
            mConnectedThread.write(data);

            //
            if (posA == init_posA && posB == init_posB && posD == init_posD && posE == init_posE && posF == init_posF && posG == init_posG)
            {
                timer2.cancel();
                finish();
            }
        }





        };



            private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    public void onResume() {
        super.onResume();

        //Obtener la MAC address desde la DeviceListActivity via intent
        Intent intent = getIntent();

        //Obtiene la MAC address desde la DeviceListActivty via EXTRA
        address = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        //Crea un dispositivo y setea la MAC Address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }
        // Establece la conección del socket Bluetooth.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {

            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            //Cierra los sockets
            btSocket.close();
        } catch (IOException e2) {
        }
    }

    //Checkea que el dispositivo tenga Bluetooth disponible, y si esta apagado, solicita encenderlo
    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    //Crea clase nueva para el hilo que envia los bytes
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creación del hilo conector
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Crea los streams de entrada y salida
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //Metodo para escribir bytes
        public void write(byte[] input) {
            try {
                mmOutStream.write(input);                //escribo los bytes via outstream
                fail=0;
            } catch (IOException e)
            {
                fail++;
                if(fail==100){
                    //Si no puedo escribir en 100 intentos, vuelvo a la pantalla anterior
                    Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                    finish();
                }


            }
        }
    }
}



