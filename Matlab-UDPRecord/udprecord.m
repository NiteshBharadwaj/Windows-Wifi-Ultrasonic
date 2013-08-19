function [varargout] = udprecord(actionStr,varargin)
import java.lang.System
import java.lang.String
fs = 96000;
f1 = 20070;
f2 = 20130;
dt = 1/fs;
T=0.4;
t = 0:dt:T-dt;
[z,p,k] = butter(10,[f1*2/fs f2*2/fs]);
[sos,g] = zp2sos(z,p,k);
Hd = dfilt.df2tsos(sos,g);
recObj = audiorecorder(fs,16,2);


SEND = 1;
RECEIVE = 2;
DEFAULT_TIMEOUT = 5000; %millis

if strcmpi(actionStr,'send')
    action = SEND;
    
    if nargin < 4
        error([mfilename '.m--SEND mode requires 4 input arguments.']);
    end % if
    
    port = varargin{1};
    host = varargin{2};
    mssg = varargin{3};
    
elseif strcmpi(actionStr,'receive')
    action = RECEIVE;
    
    if nargin < 3
        error([mfilename '.m--RECEIVE mode requires 3 input arguments.']);
    end % if
    
    port = varargin{1};
    packetLength = varargin{2};
    
    timeout = DEFAULT_TIMEOUT;
    
    if nargin > 3
        % Override default timeout if specified.
        timeout = varargin{3};
    end % if
    
else
    error([mfilename '.m--Unrecognised actionStr ''' actionStr ''.']);
end % if

% Test validity of input arguments.        
if ~isnumeric(port) || rem(port,1)~=0 || port < 1025 || port > 65535
    error([mfilename '.m--Port number must be an integer between 1025 and 65535.']);
end % if

if action == SEND
    if ~ischar(host)
        error([mfilename '.m--Host name/IP must be a string (e.g., ''www.examplecom'' or ''208.77.188.166''.).']);
    end % if
    
    if ~isa(mssg,'int8')
        error([mfilename '.m--Mssg must be int8 format.']);
    end % if
    
elseif action == RECEIVE    
    
    if ~isnumeric(packetLength) || rem(packetLength,1)~=0 || packetLength < 1
        error([mfilename '.m--packetLength must be a positive integer.']);
    end % if
    
    if ~isnumeric(timeout) || timeout <= 0
        error([mfilename '.m--timeout must be positive.']);
    end % if    
    
end % if

% Code borrowed from O'Reilly Learning Java, edition 2, chapter 12.
import java.io.*
import java.net.DatagramSocket
import java.net.DatagramPacket
import java.net.InetAddress

if action == SEND
    try
        addr = InetAddress.getByName(host);
        packet = DatagramPacket(mssg, length(mssg), addr, port);
        socket = DatagramSocket;
        socket.setReuseAddress(1);
        socket.send(packet);
        socket.close;
    catch sendPacketError
        try
            socket.close;
        catch closeError
            % do nothing.          
        end % try
        
        error('%s.m--Failed to send UDP packet.\nJava error message follows:\n%s',mfilename,sendPacketError.message);
        
    end % try
    
else
    try
        disp('start');
        socket = DatagramSocket(1561);
        socket1 = DatagramSocket(2562);
        socket.setSoTimeout(15000);
        socket.setReuseAddress(1);
        socket.setBroadcast(1);
        socket1.setSoTimeout(10000);
        socket1.setReuseAddress(1);
        socket1.setBroadcast(1);
        packet = DatagramPacket(zeros(1,packetLength,'int8'),packetLength);        
                %p = String.valueOf(System.currentTimeMillis());
        %prto = p.getBytes();
        %varargout{3} = prto;
        mssg = int8(5);
        aadre = InetAddress.getByName('192.168.49.1');
        sendPac = DatagramPacket(mssg, length(mssg),aadre,1561);
        socket1.receive(packet);
        o = System.currentTimeMillis();
        %socket.send(sendPac);
        socket1.receive(packet);
        o = System.currentTimeMillis()-o;
        disp(o);
        %socket.send(sendPac);
        socket1.receive(packet);
        %socket.send(sendPac);
        socket1.receive(packet);
        
        q = System.currentTimeMillis();
        %o =toc(t2);
              %  toc(ticID1);
                disp(String.valueOf(q));
        socket.disconnect;
        socket.close;
        socket1.disconnect;
        socket1.close;
        %disp(o-ticID1);
                varargout{3} =String.valueOf(q-varargin{5});
        o = String.valueOf(o);
        disp(o);
        disp('this is the one');
        varargout{1} = mssg;
        
    catch receiveError

        % Determine whether error occurred because of a timeout.
        if ~isempty(strfind(receiveError.message,'java.net.SocketTimeoutException'))
            errorStr = sprintf('%s.m--Failed to receive UDP packet; connection timed out.\n',mfilename);
        else
            errorStr = sprintf('%s.m--Failed to receive UDP packet.\nJava error message follows:\n%s',mfilename,receiveError.message);
        end % if        

        try
            socket.close;
        catch closeError
            % do nothing.
        end % try

        error(errorStr);
        
    end % try
    
end % if

