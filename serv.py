import flask
import flask_socketio
from flask_socketio import join_room, leave_room
from flask import request
import discoSounds as ds 
import os
import json
import datetime


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
	print('**************************************************client-connected**************************************************************')

#adds a client to the public room
@socket.on('join room')
def on_join_room(data):
	sessionid = request.sid
	print("********************************************************joined room*************************************************************")
	join_room(public_room)

#should be called when connected to get a list of songs for the room
#will take a 'genre' as an arguement, so this needs to be passed in
@socket.on('get songs')
def on_get_songs(data):
	genre = data['genre']
	songs = ds.getSongList(genre)
	print("********************************************SONGLIST triggered******************************************************************")
	socket.emit('song list', songs,room=public_room)

@socket.on('song picked')
def on_song_picked(data):
	start_at = datetime.now().microseconds
	current_song = data['song']
	stream_url_loc = ds.getSongURLLocation(current_song['id'])
	current_song['stream_url'] = stream_url_loc
	socket.emit('song to play', current_song, room=public_room)

@socket.on('register')
def on_register(data):
	print("**************************************triggered register*****************************************************************")
	new_username = data['username']
	new_password = data['password']
	new_email = data['email']
	# new_fn = data['fname']
	# new_ln = data['lname']
	if db.memberExists_by_username(new_username) is None and db.memberExists_by_email(new_email) is None:
		db.registerMember(new_username,new_password,None,None,new_email)
		print((new_username + "just registered!"))
		socket.emit("register response", {'message': "successfully registered!", 'registered':1},room=request.sid)
	else:
		print("registration failure, user must already exist")
		socket.emit("register response", {'message': "registeration failed!",'registered':0},room=request.sid)

@socket.on('login')
def on_login(data):
	print("**************************************triggered login*****************************************************************")
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



@socket.on('new message')
def on_new_message(data):
	print('***********************************triggered new message********************************')
	floor_id = data['floor']
	member_id = data['from']
	text = data['message']
	db.add_message(floor_id,member_id,text)
	socket.emit("message added", {'floor_messages':db.getFloorMessages(floor_id)},room=request.sid)



if __name__ == '__main__':
	socket.run(
		serv,
		port=int(os.getenv('PORT','80')),
		host=os.getenv('IP','0.0.0.0'),
		debug=True)

 ##waht did you do yesterday?
 #what did you do toda?
 #what is the plan for today?
 #roadblocks?