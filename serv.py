import flask
import flask_socketio
from flask_socketio import join_room, leave_room
from flask import request
import discoSounds as ds
import os
import json
import datetime
import requests
import facebook

public_room = 912837

app = flask.Flask(__name__)



import schema as db
socket = flask_socketio.SocketIO(app)
# default route


@app.route('/')
def start():
    return flask.render_template('index.html')


@socket.on('connect')
def on_connect():
    print('**************************************************client-connected**************************************************************')

# adds a client to the public room


@socket.on('join room')
def on_join_room(data):
    sessionid = request.sid
    print("********************************************************joined room*************************************************************")
    join_room(public_room)

# should be called when connected to get a list of songs for the room
# will take a 'genre' as an arguement, so this needs to be passed in


@socket.on('get songs')
def on_get_songs(data):
    genre = data['genre']
    songs = ds.getSongList(genre)
    print("********************************************SONGLIST triggered******************************************************************")
    socket.emit('song list', songs, room=public_room)


@socket.on('song picked')
def on_song_picked(data):
    current_song = data['song']
    stream_url_loc = ds.getSongURLLocation(current_song['id'])
    current_song['stream_url'] = stream_url_loc
    socket.emit('song to play', current_song, room=public_room)


"""this listener is expecting key:pair list (i.e json) with either fb_t for facebook token, or google_t for google token
	if user exists, function will validate and pass back user information and auth status. else, it will add user reference to db"""

@socket.on('login')
def on_login(data):
    print("**************************************triggered login*****************************************************************")
    if 'fb_t' not in data:
        response = requests.get(
            'https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=' + data['google_t'])
        json = response.json()
        print(json)
        fname = json['given_name'] 
        lname = json['family_name']
        link = json['picture']
        email = json['email']
        mem_found = db.memberExists_by_email(email)
        if mem_found:
            mem = db.getMember(email)
            socket.emit("login status", {
                        'authorized': 1, 'user': mem}, room=request.sid)
        else:
            new_mem = db.registerMember("",fname,lname,email,link)
            socket.emit("login status", {'authorized': 1,'user':new_mem}, room=request.sid)

#
    if 'google_t' not in data:
        graph = facebook.GraphAPI(access_token=data['fb_t'],version='2.7')
        js = graph.get_object(id='me', fields='first_name,last_name,email,picture')
        print("js:",js)
        fname = js['first_name']
        lname = js['last_name']
        email = js['email']
        print("***************************EMAIL************************")
        print(email)
        link = js['picture']['data']['url']
        mem_found = db.memberExists_by_email(email)
        if mem_found:
            mem = db.getMember(email)
            socket.emit("login status", {
                        'authorized': 1, 'user': mem}, room=request.sid)
        else:
            new_mem = db.registerMember("",fname,lname,email,link)
            socket.emit("login status", {'authorized': 1,'user':new_mem}, room=request.sid)


@socket.on('new message')
def on_new_message(data):
    print('***********************************triggered new message********************************')
    floor_id = data['floor']
    member_id = data['from']
    text = data['message']
    db.add_message(floor_id, member_id, text)
    socket.emit("message added", {
                'floor_messages': db.getFloorMessages(floor_id)}, room=request.sid)


@app.route('/floors')
@socket.on('create')
def on_create(data):
    new_floor = db.floor(data['floor_name'],data['m_id'],data['isPublic'])
    socket.emit('floor created', {'floor_id':new_floor.floor_id})



# def get_dt_ms():
# 	epoch = datetime.datetime.utcfromtimestamp(0)

#     return (dt - epoch).total_seconds() * 1000.0

if __name__ == '__main__':
    socket.run(
        app,
        port=int(os.getenv('PORT', '80')),
        host=os.getenv('IP', '0.0.0.0'),
        debug=True)

 # waht did you do yesterday?
 # what did you do toda?
 # what is the plan for today?
 # roadblocks?
