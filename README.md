# CarbOnBal_Laptop
Java laptop application for the CarbOnBal Arduino Carb and throttle body sync hardware.

This should run on Mac, Windows, Linux, and BSD laptops, anywhere Java runs.

This gives you access to meters, bar charts and raw data graphs for an even more detailed view of what's going on in your engine's throttle bodies or carburetors.

To use this software you will need Java installed on your computer.
https://java.com/en/download/help/download_options.xml
(OpenJDK will work too)

Linux users should probably add their user to whichever group allows access to the serial port. 
Normally one of the following commands will do the trick:

sudo usermod -a -G uucp username
sudo usermod -a -G dialout username
sudo usermod -a -G lock username
sudo usermod -a -G tty username

Username should be replaced by your own user name of course!


Normally a double click on the .jar file will start up the application. If that doesn't work try running:
java -jar carbonbal-laptop-1.0.jar 

In a console or "cmd window".

CarbOnBal is primarily a stand-alone hardware project and does not depend on this software, nor will it ever. 
This software should ultimately become "the icing on the cake" so to speak.

