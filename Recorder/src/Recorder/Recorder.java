package Recorder;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.*;

public class Recorder {
    public static void listMixers() {
    try{
        Mixer.Info[] mixerInfo =
            AudioSystem.getMixerInfo();
        System.out.println("Available mixers:");
        for(int cnt = 0; cnt < mixerInfo.length; cnt++){
            System.out.println(cnt);
            System.out.println(mixerInfo[cnt].getName());  
            System.out.println(mixerInfo[cnt].getDescription()); 
            Mixer mixer = AudioSystem.getMixer(mixerInfo[cnt]);
            Line.Info[] targetLines = mixer.getTargetLineInfo();
                for (Line.Info t: targetLines) {
                    System.out.println("  Target line: " + t.toString());
                }  
        }//end for loop
     } catch(Exception e) {
     }
  }
  public static void main(String args[]) throws Exception {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    float sampleRate = 44100;
    int sampleSizeInBits = 16;
    int channels = 1;
    boolean bigEndian = true;
    final AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,sampleRate, sampleSizeInBits, channels,2,sampleRate,bigEndian);
    DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
    final TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
    listMixers();
    line.open(format);
    line.start();
    Runnable runner = new Runnable() {
      float busfferSize = format.getFrameSize()*format.getFrameRate();
      int bufferSize = (int)88200/2;
      int length = bufferSize;
            int duration = 13;
      byte[] buffer = new byte[(int)(format.getFrameSize()*format.getFrameRate()*duration)];
      int tot = 20;
      byte[][] bufferlist = new byte[tot][bufferSize]; 
      

      byte dbuffer[] = new byte[bufferSize];
      byte[] tbuffe = new byte[88200]; 

      public void run() {
        try {
                      long t = System.currentTimeMillis();
System.out.println(line.getLongFramePosition());
          line.flush();
          while (line.available()<=0) {
              
          }
          System.out.println(line.getLongFramePosition());
                    System.out.println(line.available());
                    System.out.println(line.getMicrosecondPosition());
                    System.out.println(System.currentTimeMillis()-t);
                    t = System.currentTimeMillis();
                    long t0= t;
                    
                    int count = 0;
          for(int i=0;i!=tot;i++) {
                t = System.nanoTime();
                count = line.read(bufferlist[i], 0, length);
                System.out.println(count);
                System.out.println(System.nanoTime()-t);
          }
          /*try {
            Thread.sleep(1000);
          }
          catch (InterruptedException e) {
              System.out.println(e);
          }*/
                    
                    
          System.out.println("Total recording time:");
          System.out.println(System.currentTimeMillis()-t0);
          System.out.println("Recorder start time:");
          System.out.println(t0);
          int p = 0;
          System.out.println(tot*bufferSize);
          for(int i=0;i!=tot;i++) {
              for(int j=0;j!=bufferSize;j++) {
                  buffer[p] = bufferlist[i][j];
                  p++;
              }
          }
          if (count > 0) {
            out.write(buffer, 0, length*tot);
          }

          out.close();
        } catch (IOException e) {
          System.err.println("I/O problems: " + e);
          System.exit(-1);
        }
    byte audio[] = out.toByteArray();
    System.out.println(audio.length);
    File wavFile = new File("go.wav");
    if(!wavFile.exists()) try {
            wavFile.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(Recorder.class.getName()).log(Level.SEVERE, null, ex);
        }
    // format of audio file
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
    InputStream input = new ByteArrayInputStream(audio);
    try {
    input.read(audio, 1, 1);
    }
    catch (IOException e) {
        System.out.println(e);
    }
        final AudioInputStream ais = new AudioInputStream(input, format, audio.length/ format.getFrameSize());
    try {  
       ais.reset();  
     } catch (Exception e) {   
     } 
        try {
    
        AudioSystem.write(ais, fileType, wavFile);
    }
    catch (IOException ioe) {
        System.out.println(ioe);
    }
      }
    };
    Thread captureThread = new Thread(runner);
    captureThread.setPriority(Thread.MAX_PRIORITY);
    captureThread.start();
    
  }
}