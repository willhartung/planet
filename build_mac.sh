mvn -DskipTests clean package
rm -rfd target/java-runtime
rm -rfd target/installer
mkdir -p target/installer/input/libs
cp target/libs/* target/installer/input/libs/
cp target/Planet-1.0-SNAPSHOT.jar target/installer/input/libs/
#jdeps --multi-release 17 --ignore-missing-deps --print-module-deps --class-path "target/installer/input/libs/*" target/classes/bit/planet/Launcher.class 
jdeps --multi-release 17 --ignore-missing-deps --print-module-deps --class-path "target/installer/input/libs/*" target/classes
modules="java.base,java.net.http,java.scripting,jdk.jfr,jdk.jsobject,jdk.unsupported,jdk.unsupported.desktop,jdk.xml.dom,java.logging"
jlink --strip-native-commands --no-header-files --no-man-pages --compress=2 --strip-debug --add-modules "$modules" --output target/java-runtime
jpackage --type dmg --dest target/installer --input target/installer/input/libs/ --icon icons/planet.icns --name Planet --main-class app.wnh.planet.Launcher --main-jar Planet-1.0-SNAPSHOT.jar --runtime-image target/java-runtime
