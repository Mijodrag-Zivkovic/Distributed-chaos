# Distributed-chaos
Distributed system that draws fractals using chaos game. </br>
Principles of game of chaos are described here: [https://www.youtube.com/watch?v=kbKtFN71Lfs]

Distributed system architecture:
 - Bootstrap node - starts up first, all nodes that want to join the system must first communicate with Bootstrap
 - Regular nodes - nodes that do all the calculations for fractal images
 
Nodes that join the system will get assigned their fractal_id, which is a way of telling node which part of the greater picture to draw. The first node that joins will start drawing the whole picture, with the fractal_id of 0. Whenever a new node joins, it will recieve fractal_id and will check it's place in it's subfractal. If it's not the last node for that subfractal, it will be idle. If it's the last node for that subfractal, it will signal the first node for that subfractal ("parent" node) to give other idle nodes of that subfractal their own pieces of image to work on. After they are done, they will each create image file titled with their port 
(which acts as node Id), so user can see which piece of image said node has drawn. Final image is titled "final", and is result of all node's work. </br>

