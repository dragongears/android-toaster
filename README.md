## PROJECT CENTURION

Project Centurion is a project to create a Bluetooth enabled toaster.
The toaster is controlled by commands sent via Bluetooth SPP, typically from
a mobile app.

To get started -- checkout http://dragongears.github.com/projectcenturion


## Versioning

For transparency and insight into our release cycle, and for striving to maintain backward compatibility, Bootstrap will be maintained under the Semantic Versioning guidelines as much as possible.

Releases will be numbered with the follow format:

`<major>.<minor>.<patch>`

And constructed with the following guidelines:

* Breaking backward compatibility bumps the major
* New additions without breaking backward compatibility bumps the minor
* Bug fixes and misc changes bump the patch

For more information on SemVer, please visit http://semver.org/.


## Bug tracker

Have a bug? Please create an issue here on GitHub!

https://github.com/dragongears/android-toaster/issues


## Twitter account

Keep up to date on announcements and more by following Dragongears on Twitter,
<a href="http://twitter.com/Dragongears">@Dragongears</a>.


## Developers

There are several parts to the Project Centurion software, the Arduino sketch for
the toaster itself and the mobile apps to control it.

+ **Arduino** - `Centurion.ino`
The Arduino sketch for the Arduino compatible board installed in the toaster

+ **webOS** - Centurion.ipk
The webOS application for the TouchPad or other webOS device running the Enyo framework

+ **Android** - Centurion.apk
The Android application

## Authors

**Art Dahm**

+ http://twitter.com/dragongears
+ http://github.com/dragongears


## Copyright and License Information

Unless otherwise specified, all content, including all source code files and
documentation files in this repository are:

Copyright (c) 2012-2014 Arthur J. Dahm III

Unless otherwise specified or set forth in the NOTICE file, all content,
including all source code files and documentation files in this repository are:
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this content except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
