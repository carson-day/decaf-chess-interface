# Linux/Generic Installation Instructions #
## Decaf requires java/jre 5 or better ##

  * If you have no Java or a Java version < 1.5 installed, download and install the latest version of java from Sun's website (http://www.java.com)
  * Download the zipped Decaf ([here](http://decaf-chess-interface.googlecode.com/files/DecafGeneric.1.03.zip))
  * Download speech pack if you want Speech enabled ([here](http://decaf-chess-interface.googlecode.com/files/DecafSpeech.zip))
  * Extract these zip files to where you want them. (they both contain a Decaf base directory and have to be installed into the same directory for Decaf Speech to work)
  * To run without speech double click on Decaf.jar or from the command line 'java -jar Decaf.jar'
  * To run with speech double click on Decaf Speech.jar or from the command line 'java -jar Decaf Speech.jar'

### Timeseal Configuration ###
> Generic comes with two timeseals for linux: Resources/timeseal\_x86 and Resources/timeseal\_athlon.
> The timeseal can be configured in properties/Timeseal.properties
> The default is x\_86. You can change this properties file to point to the version you need for linux or other os.


### Sound configuration ###
> If you are having problems playing or mixing sounds there is a property you can set that will invoke a program via command line to play sounds. In Decaf/properties/os.properties add the line sound.externaprocess=SOUND\_PROCESS. Where SOUND\_PROCESS is the name of the process to launch.

> Krell wanted to use aoss to mix sounds and added the following line to his os.properties.

```
sound.externaprocess=playsound
```