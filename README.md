# Fourier
An interactive visualization of the Fourier Transform featured in 3Blue1Brown's video: https://www.youtube.com/watch?v=spUNpyF58BY

It is compiled into a runnable jar file written in Java using libraries from Eclipse, JFree Charts, and a Complex Numbers class. It showcases the ideas outlined by 3Blue1Brown, like wrapping, the displacement in the fourier, and uncertainty principles in the fourier transform. 
I am a programmer, so this is not very optimized, in fact the only optimizations I really did was working with scaling how many points are displayed in the circle, wrapped graph and only using a handful of points to calculate average weights rather than all of them or trying to use integration.
If enough people are interested, I can upload related libraries and the actual code, but I just thought I would put this out there because it is really useful in getting an understanding of how fourier transforms and such work.
Enjoy!


p.s., I also realised the displacement of my original cosine graph is 0, so the resulting circle graph actually goes through the origin (compare to 3Blue1Brown's video for reference). If this really is an issue, I or we can release alternate versions.
