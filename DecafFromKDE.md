# Creating a Unix startup script and a KDE desktop icon for Decaf #
The following assumes the Decaf archive was unzipped in your home directory.  If this is not the case, you can either move the Decaf root directory to your home directory - or change the path in the following instructions to that at which Decaf is located.  Keep in mind that if the Decaf root directory is not in your home directory, some of the following steps may need to be done by root.

**A Decaf Startup Script for Unix/Linux:**

**Note:** If speech is not desired, replace "DecafSpeech.jar" with "Decaf.jar".

Using a text editor or word processor, create the following script:

```
#!/bin/bash
# Decaf startup-sript
cd ~/Decaf
java -jar DecafSpeech.jar &
exit 0
```
Save the script as "~/Decaf/Decaf".

Open a shell and issue the following command:

`chmod +x ~/Decaf/Decaf`

**Creating the Decaf Icon:**

  1. Right-click on an empty spot on the desktop and select **Create New->Link to Application...**. A window with several tabs appears.
  1. Click on the icon image in the upper left corner (under the **General** tab). The **Select Icon** window opens.
  1. Select **Other icons**.
  1. Click **Browse** and browse to "~/Decaf/Resources/".
  1. Type "DECAF.BMP" in the **Location:** field.
  1. Click **Open**.
  1. In the **General** tab, replace the text "Link to Application" with "Decaf".
  1. Open the **Application** tab.
  1. In the **Command** field, enter: "sh ~/Decaf/Decaf".
  1. Click **OK**.
**You should now have a Decaf icon on your desktop - click it to start Decaf.**