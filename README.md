RSM-Client
==========

Really Simple Multiplayer Client Library

**Whats RSM?**

RSM stands for Really Simple Multiplayer [Library], a library for built Ludum Dare games. RSM's a Java based library that when finished will allow anyone to quickly and easily add multiplayer support to a Java game. RSM's not meant to be the be all and end all of multiplayer libraries and in fact will support only barebones feautres. However, RSM will be super light weight, easy to use and deployable in minutes, not hours (crucial for Ludum Dare).

Server Side: https://github.com/nosedive25/RSM-Server

**Documentation**

You can find some basic documentation for the ~~current~~ (now outdated) (client) build here: https://dl.dropboxusercontent.com/u/53944475/RSMClient.html

**Client Setup**

- Grab the source for RSM-Client
- (Optional)Compile the source into a jar **or** copy the source into your project
- Review RSMClientDriver.java for a basic game structure and setup your game in a similar manner
- Run genkeys.sh (**Only** if you haven't already) and copy the serverkeystore file to the location your game's jar file (or Eclipse project) resides in
- Build and test your connection to your sever

**Project Status**

RSM is far from completed and has only the base system setup. It should be ready for the upcoming Ludum Dare in December for any brave souls who'd like to try it.

- [x] SSL
- [ ] More efficient client search algorithim
- [x] Better server responce handeling
- [x] Basic server properties file (server.props)
- [ ] Better server properties (not just java strings)
- [ ] Server properties error handeling
- [x] Client side game creation
- [x] Clients can safely disconnect
- [x] Keystore generation script
