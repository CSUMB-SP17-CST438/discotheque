# project3-jocruz-tha-pking-cyates
Silent disco android app.

# Discotheque #
### Current State ###
The app streams music to a room.  When a song is picked by a user, it is streamed to all users that are connected to the room.

### Work Distrubution ###
_Who worked on what_

* Joshua 'Ryan' Cruz: Python/Flask server development, intitial soundcloud intergration, database creation, CircleCI and Heroku integration.
* Tommy Ha: Android socket integration(Song List), Android UI development, server debugging.
* Peter King: UI skeleton, READMEs, rewrote user storys. 
* Kara Spencer: Android MediaPlayer streaming functionality.
* Carsen Yates: Android socket integration(Auth/Register/Song List), Android UI development, server debugging. 

### What was completed ###
* The application in its current form allows users to login as a guest and join the first chat room.
* Currently all users join the same room. In the room, users can select from a list of songs, which is then streamed to all users connected to the room. 
* The databases and backend framework are deployed on a heroku app that all the users interact with.

### Known Problems ###
* [server] Registration/Authentication still does not work, these are problems that need to be worked out in the database.
* [android] MediaPlayer does not always stop playing the song when the user leaves the chat room

### Future Improvments ###
* Song synchronization.
* Chat functinality will be fully added when auth is available.  Chat UI is still in development.
* Users be able to create, join, and leave different rooms.
* Users will only be able to choose songs only when it is their turn.
* Users will have profiles.
