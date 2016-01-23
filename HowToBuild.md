# How To Build Decaf #

**1) Download and install apache ant.**

**2) Download and install a subversion client. You can click the source tab for more information.**

**3) Download code**

**trunk** in subversion is used for beta development. It is the current state of code development and might be unstable. The preferred way of obtaining source is to grab the latest release in tags. You can follow the directions on the Source tab using tags/release-LABEL instead of trunk.

**4) Run an ant task to build.**

  * ant dist (Builds Decaf without debug and all of the zip files. Requires the OSX app to be present see below)
  * ant runosx  (Builds Decaf with debug and runs decaf on osx.)
  * ant runwin  (Builds Decaf with debug and runs decaf in generic mode (see build.xml for enabling speech))
  * ant rungeneric  (Builds Decaf with debug and runs decaf on osx (see build.xml for enabling speech and changing   timeseals)
  * ant clean (Cleans up the build directory and properties/preferences.object)


## Why cant it find Decaf.app? ##
If you want to build an app for osx keep reading, otherwise just comment out the offending  include in the build.xml file.

In OSX to build the application as an OSX app you need XCode installed.

First do an ant dist

In finder click on your harddrive.
Run Developer/Applications/Java Tools/Jar Bundler.app

### Build Information: ###
Main class: decaf.Decaf
Java version: 1.5+

### Classpath And Files: ###
Add DecafOSX.jar,lib/**.jar,env/osx/lib/**.jar

### Properties: ###
in VM Options add -Dapple.laf.useScreenMenuBar=true
Bundle Name: Name of bundle
Short Version: Decaf

Save the app as Decaf.app in the directory you are using for source.

Run ant dist again (to zip it up into DecafOSX.zip).