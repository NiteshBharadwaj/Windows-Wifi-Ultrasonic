package send.packet;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;

import java.io.IOException;
import java.net.*;
import send.packet.R;
import android.util.Log;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final int DISCOVERY_PORT = 2561;
	private static final String TAG = "Send failed";
	private static final String TAG1 = "pass:";
	private boolean send = false;
	private DatagramSocket socket = null;
	private DatagramSocket socket1 = null;
	final double duration = 0.1; // seconds
	final int sampleRate = 44100;
	final int numSamples = (int)(duration * sampleRate);
	final double sample[] = new double[numSamples];
	final double freqOfTone = 20100; // Hz
	final byte generatedSnd[] = new byte[2*numSamples];
	Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main); 
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	void genTone(){
		// fill out the array
		for (int i = 0; i < numSamples; ++i) {
			double temp1=0.4*duration*sampleRate;
			double temp2=0.6*duration*sampleRate;
			if (i<temp1)
				sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/(freqOfTone-2000)))*(1-Math.exp(-i/(temp1/5)));
			else if(i>temp1 && i<temp2) {
				sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
			}
			else {
				sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/(freqOfTone-2000)))*Math.exp(-(i-temp2-temp1)/(temp1/5));
			}
		}
		// convert to 16 bit pcm sound array
		// assumes the sample buffer is normalised.
		int idx = 0;
		for (double dVal : sample) {
			short val = (short) (dVal * 32767);
			generatedSnd[idx++] = (byte) (val & 0x00ff);
			generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
		}
	}
	void playSound(){
		AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
		44100, AudioFormat.CHANNEL_CONFIGURATION_MONO,
		AudioFormat.ENCODING_PCM_16BIT, numSamples,
		AudioTrack.MODE_STATIC);
		audioTrack.write(generatedSnd, 0, numSamples);
		audioTrack.play();
	}
	protected void onResume() {
		super.onResume();
		Thread thread = new Thread(new Runnable() {
			public void run() {
				genTone();
				handler.post(new Runnable() {
					public void run() {
						playSound();
					}});
				}  
			});
			thread.start();
	}
	
	public InetAddress getBroadcastAddress() throws IOException {
	    WifiManager wifi = (WifiManager) getBaseContext().getSystemService(Context.WIFI_SERVICE);
	    DhcpInfo dhcp = wifi.getDhcpInfo();
	    if (dhcp == null) {
	        Log.d(TAG, "Could not get dhcp info");
	        return null;
	      }

	    int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
	    byte[] quads = new byte[4];
	    for (int k = 0; k < 4; k++)
	      quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
	    return InetAddress.getByAddress(quads);
	}
	public void startSend() throws IOException {
		byte[] data = new byte[1];
		AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
				44100, AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, numSamples,
				AudioTrack.MODE_STATIC);
		audioTrack.flush();
				audioTrack.write(generatedSnd, 0, numSamples);
		data[0] = 6;
		if (socket==null){
			socket = new DatagramSocket(null);
			socket.setReuseAddress(true);
			socket = new DatagramSocket(DISCOVERY_PORT);
			socket.setBroadcast(true);	
		}
		if (socket1==null){
			socket1 = new DatagramSocket(null);
			socket1.setReuseAddress(true);
			socket1 = new DatagramSocket(DISCOVERY_PORT+1);
			socket1.setBroadcast(true);	
		} 
		//InetAddress address = InetAddress.getByName("172.22.1.92");
		DatagramPacket packet = new DatagramPacket(data, 1,
				getBroadcastAddress(), DISCOVERY_PORT+1);
		Log.d("address", getBroadcastAddress().toString());
		byte[] recBuf = new byte[1024];
		DatagramPacket recPacket = new DatagramPacket(recBuf,recBuf.length);
			long tim = System.currentTimeMillis();
			long tim1 = System.currentTimeMillis();
			//socket.send(packet);
			socket1.send(packet);
			socket.receive(recPacket);
			socket1.send(packet);
			socket.receive(recPacket);
			tim = System.currentTimeMillis();
			socket1.send(packet);
			tim1 = System.currentTimeMillis();
			socket.receive(recPacket);
			tim = System.currentTimeMillis()-tim;
			socket1.send(packet);
			int kj = 0;
			long tj = System.currentTimeMillis();
			audioTrack.play();
			tj = System.currentTimeMillis()-tj;
			tim1 = System.currentTimeMillis()-tim1;
			socket.disconnect();
			socket.close();
			socket = null;
			socket1.disconnect();
			socket1.close();
			socket1 = null;
			String t = Long.toString(tj);
			String t1 = Long.toString(tim1);
			Toast.makeText(MainActivity.this,new String(recPacket.getData()), Toast.LENGTH_SHORT).show();
			Toast.makeText(MainActivity.this,t+" " +t1, Toast.LENGTH_SHORT).show();
	}
	public void start(View view) {
        switch (view.getId()) {
        case R.id.button1:
        	try {
				startSend();
				Log.d(TAG, "Sent");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e(TAG, "Could not send discovery rquest", e);
				e.printStackTrace();
			}
        	break;
        }
     }
	public void stop(View view) {
        switch (view.getId()) {
        case R.id.button2:
        	send = false;
        	break;
        }
     }
	

}
