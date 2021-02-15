rem  quality  18-24,  lower the better  


.\ffmpeg\bin\ffmpeg.exe -i %1  -ss 00:01:01  -to 00:01:56   -c copy    %~n1_.mp4

pause

rem DG1080122548
