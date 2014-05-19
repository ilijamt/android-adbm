Change Log
==========
#### 1.1.2 

###### Features:
* Added Spanish translation by Ryo567 (eumase-04@hotmail.com)

#### 1.1.1

###### Features:
* Added an option to change languages
* Upgraded SU library 

#### 1.0.9

###### Bugfixes:

* Fixed an issue where all the wifi networks auto connect 

#### 1.0.8

###### Bugfixes:

* Fixed an issue in preference where an user set the port to an absurdly big number for ADB port

#### 1.0.7

###### Bugfixes:

* Fixed an issue that affects Android 4.0.3 and 4.0.4 of a crash in get java.net.NetworkInterface.getNetworkInterfacesList

#### 1.0.6

###### Bugfixes:

* Fixed an issue introduced in an earlier version where it doesn't remove the double qoutes from the getSSID function, and therefore the autoconnect didn't work

###### Features:

* Added a button in preferences to clear the wifi list collected by the application, this is used to refresh the wifi list, and in some cases is usefull, if the autoconnect doesn't work, or after an upgrade of the android system.

#### 1.0.5

###### Features:

* Some fixes for activity and service interaction
* Handler fixes, now it doesn't crash the service

#### 1.0.2

###### Features:

* Upgraded SuperSU library

###### Bugfixes:

* Very rarely the service got called with null action, and because of it crashed the service

#### 1.0.1

###### Bugfixes:

* Fixed a crash that happens if you haven't saved any WiFi networks in the system, when you open the configuration screen for WiFi networks in settings

#### 1.0.0

Released as open source

#### 0.9.9

You should reset your preferences for this update from the preferences screen.

###### Features:

* Added a status to show if we have wakelock or not

###### Bugfixes:

* Tidy up for the strings, also added new time intervals for the preferences screen
* Hopefully fixed the issue with AlarmManager holding the CPU awake, even if we don't have a wake lock acquired


#### 0.9.8

###### Bugfixes:

* Fixed the wake lock issues on some occasions

#### 0.9.7

###### Bugfixes: 

* ADB state by touching the image in the notification bar

#### 0.9.5

###### Bugfixes:

* Doesn't release wakelock always

#### 0.9.4

###### Features:
* Added an option to keep screen on while the service is running
* Added an option to wake the screen when new package is installed if the keep screen functionality is not on

###### Permission:
* android.permission.WAKE_LOCK
  Used to wake up the screen on new package install, or to keep the screen on while the ADB service is on

#### 0.9.1

###### Bugfixes:
* Fixed a crash that occurs sometimes when starting the service

#### 0.9

###### Features: 
* Added a Widget

###### Bugfixes:
* Various optimizations and bugfixes

#### 0.8.4

###### Features: 
* Added About menu
* Added Change Log menu

###### Bugfixes:
* Various optimizations and bugfixes

#### 0.8.3

###### Features:
* Added the ability to make the notification permanent or be able to clear it

###### Bugfixes:
* ADB Network status not updating properly

#### 0.8.2

###### Features:
* Added new icon for the states, now it has different icons for various states.
  White overlay: No WiFi connections
  Sky blue overlay: WiFi connection available but no connections
  Yellow overlay: WiFi connected, and ADB is running in network state

###### Bugfixes:
* Sometimes the DISCONNECT event didn't update the notification bar

#### 0.8.0 - Initial release

###### Features:
* Easy control and access details from notification bar
* Auto connect on saved WiFi networks
* Auto start on boot, you can select if you want to or not from the preferences screen
* Automatically switch between USB and NETWORK when you disconnect/connect from/to WiFi
* Configurable service management
