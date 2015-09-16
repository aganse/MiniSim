# Makefile for MiniSim
# I know, I know, but let's not start a "make" vs. "ant" argument here...
#
# Written by Andy Ganse, Applied Physics Laboratory, University of Washington
# copyright 1999 by UW
#
# aganse@apl.washington.edu

all: jar doc

classes: MiniSim.java GUIFrame.java NavalObject.java SimInterface.java \
     Sonobuoy.java Submarine.java 
	javac MiniSim.java GUIFrame.java NavalObject.java SimInterface.java \
           Sonobuoy.java Submarine.java 

jar: classes MiniSim.class
	jar cf MiniSimDemo.jar *.class
	jar umvf mainClass MiniSimDemo.jar

srcjar: MiniSim.java GUIFrame.java NavalObject.java SimInterface.java \
     Sonobuoy.java Submarine.java buoy1.gif sub3.gif water.gif mainClass \
     README.txt Makefile MiniSim.html classDiagram.gif screenshot.png
	jar cf MiniSim.src.jar *.java buoy1.gif sub3.gif water.gif \
	   mainClass README.txt Makefile MiniSim.html classDiagram.gif screenshot.png

doc: MiniSim.java
	javadoc -d doc/javadoc -author -version *.java 

clean:
	\rm *.class *~

