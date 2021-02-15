rem  quality  18-24,  lower the better   

\VirQ\Tool\ffmpeg\bin\ffmpeg.exe -i %1  -i \VirQ\MediaCenter\virq_logo.png -filter_complex "overlay=100:200"  -vcodec libx264 -crf 22   %~n1_.mp4

pause
