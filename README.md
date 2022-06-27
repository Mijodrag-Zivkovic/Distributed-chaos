# Distributed-chaos
Distributed system that draws fractals using chaos game. </br>
Principles of game of chaos are described here: [https://www.youtube.com/watch?v=kbKtFN71Lfs]

Distributed system architecture:
 - Bootstrap node - starts up first, all nodes that want to join the system must first communicate with Bootstrap
 - Regular nodes - nodes that do all the calculations for fractal images
 
Nodes that join the system will get assigned their fractal_id, which is a way of telling node which part of the greater picture to draw. 
