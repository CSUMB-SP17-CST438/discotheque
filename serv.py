import flask
import flask_socketio
from flask_socketio import join_room, leave_room
import discoSounds as ds 

serv = flask.Flask(__name__)
socket = flask_socketio.SocketIO(serv)
##default route
@serv.route('/')


@socket.on('connect')
def on_connect():
	print('client-connected')


@socket.on('join room')
def on_join_room(data):
	sessionid = request.sid


@socket.on('get songs')
def on_get_songs(data):
	genre = data['genre']
	songs = ds.getSongList(genre)
	socket.emit('song list', songs)







 ##waht did you do yesterday?
 #what did you do toda?
 #what is the plan for today?
 #roadblocks?