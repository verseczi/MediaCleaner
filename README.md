# MediaCleaner

First version of this software was written in C#. I've moved to linux, so i've rewritten the damm thing in Kotlin.

The software can be connected to Emby or Plex. It currently supports only one user. For deleting files there is a built in solution, but if you have Sonarr connect that too, its more reliable.

Usage:
After you downloaded the repository run the "gradle jar" command (Note: you need the gradle package for this.)
The runnable .jar file can be found in the build/libs  directory.
For connecting emby etc, use the --settings argument.
e.g.:
> java -jar MediaCleaner.jar --settings

The software has the following options:
- keep the last season you've watched
- keep the last X watched episodes.
- keep episodes for a span of time
- keep favorite episodes (Plex does'nt have this function)

P.S.: I've made this for my personal usage, just to clean up some old tv episodes. I can't give you any support.
