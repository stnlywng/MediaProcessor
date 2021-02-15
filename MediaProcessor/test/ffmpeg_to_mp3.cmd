ffmpeg\bin\ffmpeg.exe   -i %1   -map 0:a -ac 2 -ar 44100 -ab 128k   %~n1.mp3
pause
