import flask
import flask_socketio
from flask_socketio import join_room, leave_room
import discoSounds as ds 
import os

public_room = 912837

serv = flask.Flask(__name__)
socket = flask_socketio.SocketIO(serv)
##default route
@serv.route('/')
def start():
	return flask.render_template('index.html')

@socket.on('connect')
def on_connect():
	print('client-connected')


@socket.on('join room')
def on_join_room(data):
	sessionid = request.sid
	join_room(public_room)

@socket.on('get songs')
def on_get_songs(data):
	genre = data['genre']
	songs = ds.getSongList(genre)
	socket.emit('song list', songs,room='public')

@socket.on('now playing')
def on_now_playing(data):
	current_song = data['current_song']
	socket.emit('song to play', current_song, room=public_room)

if __name__ == '__main__':
	socket.run(
		serv,
		port=int(os.getenv('PORT')),
		host=os.getenv('IP','0.0.0.0'),
		debug=True)




 ##waht did you do yesterday?
 #what did you do toda?
 #what is the plan for today?
 #roadblocks?