import flask
import flask_socketio
from flask_socketio import join_room, leave_room
from flask import request
import discoSounds as ds 
import os
import json


public_room = 912837

serv = flask.Flask(__name__)
import schema as db
socket = flask_socketio.SocketIO(serv)
##default route
@serv.route('/')
def start():
	return flask.render_template('index.html')

@socket.on('connect')
def on_connect():
	print('client-connected')

#adds a client to the public room
@socket.on('join room')
def on_join_room(data):
	sessionid = request.sid
	join_room(public_room)

#should be called when connected to get a list of songs for the room
#will take a 'genre' as an arguement, so this needs to be passed in
@socket.on('get songs')
def on_get_songs(data):
	genre = data['genre']
	songs = ds.getSongList(genre)
	# print(songs)
	socket.emit('song list', songs,room=public_room)

@socket.on('now playing')
def on_now_playing(data):
	current_song = data['current_song']
	socket.emit('song to play', current_song, room=public_room)


@socket.on('register')
def on_register(data):
	new_username = data['username']
	new_password = data['password']
	new_email = data['email']
	# new_fn = data['fname']
	# new_ln = data['lname']
	if db.memberExists_by_username(new_username) and db.memberExists_by_email(new_email) is None:
		db.registerMember(new_username,new_password,None,None,new_email,None,None,None)
		print((new_username + "just registered!"))
		socket.emit("registered successfully", {'message': "successfully registered!"})
	else:
		print("registration failure, user must already exist")

@socket.on('login')
def on_login(data):
	loadedData = json.loads(data)
	if 'username' not in loadedData:
		if db.login_attempt_email(loadedData['email'],loadedData['password']):
			socket.emit("login status", {'authorized': 1}, room=request.sid)
		else:
			socket.emit("login status", {'authorized': 0}, room=request.sid)
	if 'email' not in loadedData:
		if db.login_attempt(loadedData['username'],loadedData['password']):
			socket.emit("login status", {'authorized': 1}, room=request.sid)
		else:
			socket.emit("login status", {'authorized': 0}, room=request.sid)





if __name__ == '__main__':
	socket.run(
		serv,
		port=int(os.getenv('PORT')),
		host=os.getenv('IP','0.0.0.0'),
		debug=True)

# if __name__ == '__main__':
# 	socket.run(
# 		serv,
# 		port='8080',
# 		host=os.getenv('IP','0.0.0.0'),
# 		debug=True)


 ##waht did you do yesterday?
 #what did you do toda?
 #what is the plan for today?
 #roadblocks?