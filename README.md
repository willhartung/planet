# planet
A UI wrapper around a fractal planet generator

The fractal planet generator was created by Torben Mogensen, who's website at
http://hjemmesider.diku.dk/~torbenm/Planet/ .

This project rewrote the original C program into Java, and then created a GUI
wrapper around the generator, exposing most of the model parameters to allow
easy access to the different projections, color schemes, and other aspects 
of the model. It also provides a printable icosahedral version.

There is a more detail manual bundled with the application.


#Build Instructions

Planet is a JavaFX Maven project, and requires JDK 17 or above. It uses JavaFX
from the Maven repositories, so there is no need to bundle FX with the JDK. I 
have not tested it on a JDK that has FX installed.

Building is straightforward using:

    mvn clean install

You can then execute the application using:

    mvn javafx:run

Or, there is a combined jar created in the shade directory:

    java -jar share/Planet.jar

