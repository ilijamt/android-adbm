<a href="https://play.google.com/store/apps/details?id=com.matoski.adbm">
  <img alt="Get it on Google Play"
       src="https://developer.android.com/images/brand/en_generic_rgb_wo_60.png" />
</a>

ADB Manager (adbm)
------------------

ADB Manager, your one stop to developing more easily on Android.

**Warning: REQUIRES ROOT!**

The most automated, easy-to-use and stable ADB management tool with a great support.

Features:
+ Easy control and access details from notification bar
+ Auto connect on saved WiFi networks
+ Auto start on boot, you can select if you want to or not from the preferences screen
+ Automatically switch between USB and NETWORK when you disconnect/connect from/to WiFi
+ Configurable service management
+ Different color coded icons depending on the state of the ADB 
+ Keep screen on while the service is running
+ Wake the screen when new package is installed
+ You can toggle the ADB state by touching the image in the notification bar

It's really more simple to use than others, is always visible in notification bar. It supports advanced features like fully automate adb state control based on the WiFi network, and the state of the network.

ADB manager enables you to automatically start ADB in network mode when you connect to any know configured network from the list in the preferences.

Developing in cafes/bars/trains/toilet/other places over WiFi? ADB Manager will switch ADB into wireless and back automatically.

### Change Log

For a detailed list of the changes, take a look at this [change log](changelog.md)

Released application as open source

### Permissions

* android.permission.ACCESS_SUPERUSER 

Used with SuperSU if available

* android.permission.RECEIVE_BOOT_COMPLETED

Used to automatically start the service on the boot of the device, how long should it wait before starting and how often should the AlarmManager check to see if the service is running is configurable from the Preferences menu.

* android.permission.ACCESS_NETWORK_STATE, android.permission.ACCESS_WIFI_STATE

Used to automate the switching between the ADB states

* android.permission.INTERNET

Used to retrieve the IP, as it crashes when checking for the IP address without this permission.

* android.permission.WAKE_LOCK

Used to wake up the screen on new package install, or to keep the screen on while the ADB service is on

### Future versions:

* Auto connect to ADB without executing adb connect on your pc/laptop
* Delay between switching states

### Known issues

* On API 8, the menu icons look a little weird on LDPI devices.

* Some **Samsung** devices
  
  If you get the following error in the console log: 
  
  **Failed to install apk on device 'ip:port': timeout**
  
  **Launch canceled!**
  
  Eclipse | Window > Preferences > Android > DDMS > ADB connection timeout (ms) Increase the value

### Tested on

|       API      |  8 | 10 | 14 | 15 | 16 | 17 | 18 | 19 | 20 | 21 |
|:--------------:|:--:|:--:|----|:--:|:--:|:--:|:--:|:--:|:--:|:--:|
|   One+ |  |    |    |    |    |    |    | ![OK](Resources/checkmark.jpg) | | | 
|        Nexus 4 |    |    |    | ![OK](Resources/checkmark.jpg) | ![OK](Resources/checkmark.jpg) | ![OK](Resources/checkmark.jpg) | ![OK](Resources/checkmark.jpg) |    | | |
|        Nexus 7 |    |    |    | ![OK](Resources/checkmark.jpg) | ![OK](Resources/checkmark.jpg) | ![OK](Resources/checkmark.jpg) | ![OK](Resources/checkmark.jpg) | ![OK](Resources/checkmark.jpg)   | | |
|      Galaxy S2 |    | ![OK](Resources/checkmark.jpg) | ![OK](Resources/checkmark.jpg) | ![OK](Resources/checkmark.jpg) | ![OK](Resources/checkmark.jpg) |    |    |    |  | |
|      Galaxy S3 |    |    |    | ![OK](Resources/checkmark.jpg) | ![OK](Resources/checkmark.jpg) | ![OK](Resources/checkmark.jpg) | ![OK](Resources/checkmark.jpg) | ![OK](Resources/checkmark.jpg) | | |
|      Galaxy S4 |    |    |    |    |    | ![OK](Resources/checkmark.jpg) | ![OK](Resources/checkmark.jpg) | ![OK](Resources/checkmark.jpg)|  | ![OK](Resources/checkmark.jpg) |
|    Galaxy Note |    |    | ![OK](Resources/checkmark.jpg) | ![OK](Resources/checkmark.jpg) |    |    |    |    |  | |
| Galaxy Note II |    |    |    | ![OK](Resources/checkmark.jpg) | ![OK](Resources/checkmark.jpg) |    |    |    |  | |
| Galaxy Tab     |    |    | ![OK](Resources/checkmark.jpg) | ![OK](Resources/checkmark.jpg) |    |    |    |    |  | |
|       HTC Hero | ![OK](Resources/checkmark.jpg) | ![OK](Resources/checkmark.jpg) |    |    |    |    |    |    |  | |
|   HTC Wildfire | ![OK](Resources/checkmark.jpg) |    |    |    |    |    |    |    |  | |

Used libraries
---------------

 * [libsuperuser](https://github.com/Chainfire/libsuperuser)

Translations
------------
If you want to help translate this application feel free to send me a pull request, also if you want to help me translate the store description, see the Resources folder

[https://crowdin.net/project/adbm](https://crowdin.net/project/adbm)

Archive versions
-----------------

* [0.8.0] (http://files.matoski.com/adbm/releases/adbm-0.8.0-release.apk)
* [0.8.2] (http://files.matoski.com/adbm/releases/adbm-0.8.2-release.apk)
* [0.8.3] (http://files.matoski.com/adbm/releases/adbm-0.8.3-release.apk)
* [0.8.4] (http://files.matoski.com/adbm/releases/adbm-0.8.4-release.apk)
* [0.9.0] (http://files.matoski.com/adbm/releases/adbm-0.9.0-release.apk)
* [0.9.4] (http://files.matoski.com/adbm/releases/adbm-0.9.4-release.apk)
* [0.9.5] (http://files.matoski.com/adbm/releases/adbm-0.9.5-release.apk)
* [0.9.6] (http://files.matoski.com/adbm/releases/adbm-0.9.6-release.apk)
* [0.9.7] (http://files.matoski.com/adbm/releases/adbm-0.9.7-release.apk)
* [0.9.8] (http://files.matoski.com/adbm/releases/adbm-0.9.8-release.apk)
* [0.9.9] (http://files.matoski.com/adbm/releases/adbm-0.9.9-release.apk)
* [1.0.0] (http://files.matoski.com/adbm/releases/adbm-1.0.0-release.apk)
* [1.0.1] (http://files.matoski.com/adbm/releases/adbm-1.0.1-release.apk)
* [1.0.5] (http://files.matoski.com/adbm/releases/adbm-1.0.5-release.apk)
* [1.0.7] (http://files.matoski.com/adbm/releases/adbm-1.0.7-release.apk)
* [1.0.8] (http://files.matoski.com/adbm/releases/adbm-1.0.8-release.apk)
* [1.0.9] (http://files.matoski.com/adbm/releases/adbm-1.0.9-release.apk)
* [1.1.1] (http://files.matoski.com/adbm/releases/adbm-1.1.1-release.apk)
* [1.1.2] (http://files.matoski.com/adbm/releases/adbm-1.1.2-release.apk)
* [1.2.0] (http://files.matoski.com/adbm/releases/adbm-1.2.0-release.apk)
* [1.2.1] (http://files.matoski.com/adbm/releases/adbm-1.2.1-release.apk)
* [1.2.2] (http://files.matoski.com/adbm/releases/adbm-1.2.2-release.apk)

License
-------

Copyright (C) 2013 Ilija Matoski (ilijamt@gmail.com)
 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0
 
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributors
------------
* **Main Developer** Ilija Matoski (ilijamt@gmail.com)
* **Translator (Japanese)** Mari (https://crowdin.com/profile/marimaari)
* **Translator (Spanish)** Ryo567 (eumase-04@hotmail.com)
* **Translator (Chinese Simplified)** yangfl (https://crowdin.com/profile/yangfl), yap7800 (https://crowdin.com/profile/yap7800)
