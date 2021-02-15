ffmpeg\bin\ffmpeg.exe   -i %1  -i 1.mp3  -c copy   -shortest   %~n1_.mp4

pause
