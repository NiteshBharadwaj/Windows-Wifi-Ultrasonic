function recCallback(obj,event,recObj,fs,t,Hd,m)
result = input('prompt');
x = getAudioData(recObj);
wavwrite(x,fs,'myrecord.wav');
y = filter(Hd,x);
m = m+result;
m = m*fs/1000;
t1 = m-44100;
t2 = m+44100;
x = x(t1+1:t2,:);
y = y(t1+1:t2,:);
[X,F1] = sft_mag(x,fs);

[Y,F2] = sft_mag(y,fs);
x(m-t1+2,:)=x(m-t1+2,:)*12;
y(m-t1+2,:)=y(m-t1+2,:)*12;
f1 = 19070;
f2 = 19030;
[z,p,k] = butter(10,[f1*2/fs f2*2/fs]);
[sos,g] = zp2sos(z,p,k);
Hd = dfilt.df2tsos(sos,g);
y1 = filter(Hd,x);
[Y1,F3] = sft_mag(y1,fs);
subplot(2,3,1); plot(t(t1+1:t2),x); xlabel('time');ylabel('signal')
subplot(2,3,2); plot(t(t1+1:t2),y); xlabel('time');ylabel('signal through filter');
subplot(2,3,5); plot(t(t1+1:t2),y1); xlabel('time');ylabel('signal through filter2');
subplot(2,3,4); plot(F1,X); xlabel('frequency');ylabel('Magnitude');
subplot(2,3,3); plot(F2,Y); xlabel('frequency');ylabel('Magnitude through filter1');
subplot(2,3,6); plot(F3,Y1); xlabel('frequency');ylabel('Magnitude through filter2');
