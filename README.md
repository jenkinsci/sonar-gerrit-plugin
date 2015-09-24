## Sonar Gerrit Plugin
This plugin triggers builds on events from the Gerrit code review system by retrieving events from the Gerrit command "stream-events", so the trigger is pushed from Gerrit instead of pulled as scm-triggers usually are.

Various types of events can trigger a build, multiple builds can be triggered by one event, and one consolidated report is sent back to Gerrit.

Multiple Gerrit server connections can be established per Jenkins instance. Each job can be configured with one Gerrit server.

## Maintainers

Tatiana Didik
aquarellian@gmail.com


## Wiki
* [Wiki](https://wiki.jenkins-ci.org/display/JENKINS/Sonar+Gerrit)
* [Mailing Lists](http://jenkins-ci.org/content/mailing-lists)Mailing Lists

## Build
The plugin depends on a gerrit-trigger plugin.


## License
The MIT License


Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.