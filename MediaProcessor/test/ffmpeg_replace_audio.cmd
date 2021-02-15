ffmpeg\bin\ffmpeg.exe   -i %1  -i 1.mp3    -map 0:0 -map 1  -vcodec copy -acodec copy   %~n1_.mp4

pause
