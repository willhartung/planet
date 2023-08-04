# require imagemagick to be installed to create the ico file.
# requires the genIcons() method in the ColorSchemeManagerTest.java class to be run
# (I normally do this by adding the @Test annotation, and "Run Focused Test Method"
# in Netbeans)
# genIcons creates the icons in the /tmp directory

# Make the ico file
convert /tmp/icon32.png /tmp/icon64.png /tmp/icon128.png /tmp/icon256.png /tmp/icon512.png /tmp/icon1024.png planet.ico

# Make the icns file on MacOS, requires iconutil utility
rm -rf planet.iconset
mkdir planet.iconset
cp /tmp/icon16.png planet.iconset/
cp /tmp/icon16.png planet.iconset/icon_16x16.png
cp /tmp/icon32.png planet.iconset/icon_32x32.png
cp /tmp/icon32.png planet.iconset/icon_16x16@2x.png
cp /tmp/icon64.png planet.iconset/icon_64x64.png
cp /tmp/icon64.png planet.iconset/icon_32x32@2x.png
cp /tmp/icon128.png planet.iconset/icon_128x128.png
cp /tmp/icon128.png planet.iconset/icon_64x64@2x.png
cp /tmp/icon256.png planet.iconset/icon_256x256.png
cp /tmp/icon256.png planet.iconset/icon_128x128@2x.png
cp /tmp/icon512.png planet.iconset/icon_512x512.png
cp /tmp/icon512.png planet.iconset/icon_256x256@2x.png
cp /tmp/icon1024.png planet.iconset/icon_512x512@2x.png
iconutil -c icns planet.iconset

# use the 512 icon for Linux
cp /tmp/icon512.png planet_liunx.png

