ffmpeg.exe -i %1 -filter:v "setpts=0.5*PTS" %~n1_.mp4