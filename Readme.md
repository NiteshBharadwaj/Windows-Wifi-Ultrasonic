Folders:

Matlab-UDPRecord  - It directly communicates with mobile app through matlab framework and captures the packets and records the audio signals
recorder.m is the entry-point to the code
*Please note that standard laptop hardware can receive audio signals at high frequencies but the speakers cannot produce the same

The following projects can be used (individually or together) to communicate with hardware layer

Recorder - A sound recorder that communicates directly with audio recorder using audio buffers. 
It can be used to change buffer sizes, number of buffers etc..
Presently it records for 10 seconds and prints the recorder start time

UDPCapture - A wifi capture that uses jnetpcap capture to capture wifi packets. A trivial implementation of the capture mechanism.
The packet header is accessed and the time stamps from the link layer are accessed using methods in jnetpcap library 
* Requires winpcap (dll for windows) or pcap equivalents on other platforms to be installed

SendPacket- The android app which sends signal (wifi+audio) to the laptop. Once a signal is sent it displays the wifi round trip time of previous packets as a message on mobile. 

The two programs Recorder and UDPCapture can be run one after the other and then the signal can be received from the mobile in next 10 seconds 
The recorded data is stored in "go.wav" and 

TODO: Connect the mobile with laptop either by running a hotspot on laptop or by making the mobile itself a softAP(hotspot)

TODO: Check the ip address of the mobile and put in the code (udprecord.m in folder#1) if using Matlab-UDPRcord. 
If using UDPCapture, it sees all the udp traffic through the adapter. So it is not necessary to specify ip of mobile

TODO: Modify the code (comment all socket1.receive in sendPacket ) if UDPCapture is used instead of MATLAB
	                  (uncomment) if MATLAB-UDPRecord is used to capture
					 
Tested last on 19-8-2013 on Galaxy Nexus (JB 4.3) and Windows 7