ffmpeg\bin\ffmpeg.exe -i %1  -vcodec copy  -af "volume=9dB"    %~n1_.mp4
pause
