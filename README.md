#RIOdroid by Spectrum3847
####What is RIOdroid?
RIOdroid is a package that allows an NI RoboRIO to communicate with an Android Device that is connected to it's USB port.

####Why Android?

Android phones are cheap and powerful. Lots of code already exists for the platform. Students already know Java.

####How do they communicate?

ADB is available to allow for debug and control of the Android device from the RoboRIO.
Android Accessory Protocol over USB. works on any android device running 3.1 or greater (basically any prepaid phone today or tablet)

####What should we able to use this for?

* Send video to the Driver station from an android device (this currently works with the IP Webcam app)
* Use Android as the coprocessor and send image data and object recognition data to the RoboRIO
* Use Android as a display for the roboRIO. Display values, colors, videos, etc.
* Use Android as speakers for the RoboRIO Output sounds, music, noises, etc
* Get sensor data from the Android Device (accelerometer, compass, gyro, GPS, etc)
* Potential/Unsure - Use android device as wifi adapter in place for router at demonstrations/practice

###Install Instructions
####Quick Instructions
1. Download RIOdroid.zip from https://github.com/Spectrum3847/RIOdroid/tree/master/release
2. Extract RIOdroid.zip
3. copy RIOdroid.tar.gz and RIOdroid.sh to /home/admin on the RoboRIO
4. Run the RIOdroid.sh script from that directory
5. Use the RIOdroid.jar file in your java code to access Android Debug Bridge and usb4java code.

####More Detailed Instructions
1. Extract the RIOdroid.zip file to a folder on your computer (RIOdroid on your desktop for example)
2.  Copy the script and RIOdroid.tar.gz into the RoboRIO (instructions below)
	- Instructions for Windows
	- Install WinSCP from (https://winscp.net/eng/docs/guide_install)
	- Plug in USB A-B cable to roboRIO USB port and to the computer
	- open WinSCP and connect to either roborio-XXXX.local or 172.22.11.2 (XXXX is your team number)
	- login as Admin and leave the password blank and press enter
	- Click accept or yes to any pop windows
	- Using the left pane of the window navigate to the folder you extracted the RIOdroid.zip into.
	- Drag the RIOdroid.tar.gz and RIOdroid.sh files over to the right window pane.

3. SSH into the RoboRIO
	- Install Putty from (www.chiark.greenend.org.uk/~sgtatham/putty/download.html)
	- open putty and connect to either roborio-XXXX.local or 172.22.11.2 (XXXX is your team number)
	- login as Admin and leave the password blank and press enter
	- Click accept or yes to any pop windows
	- Type the following two commands and press enter after each
		chmod 777 RIOdroid.sh
		./RIOdroid.sh
	- This should have run the RIOdroid script and copied over all the needed files to your roborio.
	- type the following command to see if things are working
		adb devices
	- That should have started the adb services and displayed that in the PuTTY window

4. Now you can use the library .jar file in your project. You will need to add this to your build script. This will be easy in 2016.

5. The example file provided forwards a port from an android device running the IP Webcam app to the external ports of the roborio.
So that you can connect to it at roborio-XXXX.local:8080 and view the camera image.

###Liscense Stuff
This code is heavily based on code provided by
Android Open Source Project (https://source.android.com/)
usb4java (http://usb4java.org/)
libusb (http://www.libusb.org/)
jadb (https://github.com/vidstige/jadb)
Apache Commons (https://commons.apache.org/proper/commons-lang/)
and android.serverbox.ch

Thank you to everyone that worked on these projects and provided open source software for the community to use.

These files were released under various liscenses Apache, GPL2, etc.
I've included the liscense files everywhere, we think. If we missed something please let us know.

Any of the modifications and code that we wrote is fair game to be used by anyone.
