$DebugPreference = "Continue"

# Clean and package using Maven
mvn -DskipTests clean package

# Remove directories with error handling
Remove-Item -Path "target\java-runtime" -Force -Recurse -ErrorAction SilentlyContinue
Remove-Item -Path "target\installer" -Force -Recurse -ErrorAction SilentlyContinue

# Create directory
New-Item -Path "target\installer\input\libs" -ItemType Directory -Force

# Copy files
Copy-Item -Path "target\libs\*" -Destination "target\installer\input\libs\" -Force
Copy-Item -Path "target\Planet-1.0-SNAPSHOT.jar" -Destination "target\installer\input\libs\" -Force

# Define modules
$modules = "java.base,java.net.http,java.scripting,jdk.jfr,jdk.jsobject,jdk.unsupported,jdk.unsupported.desktop,jdk.xml.dom,java.logging"

# Create runtime image
jlink --strip-native-commands --no-header-files --no-man-pages --compress=2 --strip-debug --add-modules $modules --output "target\java-runtime"

# Create installer package
jpackage --dest "target\installer" --input "target\installer\input\libs\" --icon icons/planet.ico --name Planet --main-class app.wnh.planet.Launcher --main-jar Planet-1.0-SNAPSHOT.jar --runtime-image "target\java-runtime"
