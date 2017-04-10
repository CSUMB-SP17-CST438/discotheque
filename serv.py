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
import time

public_room = 912837

app = flask.Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = os.getenv('DATABASE_URL','postgresql://jcrzr:anchor99@localhost/postgres')
# app.config['SQLALCHEMY_TRACK_MODIFICATIONS']= 1

from schema import *
db.init_app(app)

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
	if user exists, function will validate and pass back user information and auth status. else, it will add user reference to schema"""

@socket.on('login')
def on_login(data):
    print("**************************************triggered login*****************************************************************")
    if 'fb_t' not in data:
        print('google_t: ')
        print(data)
        response = requests.get(
            'https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=' + data['google_t'])
        json = response.json()
        print(json)
        fname = json['given_name'] 
        lname = json['family_name']
        link = json['picture']
        email = json['email']
        mem_found = memberExists_by_email(email)
        print(mem_found)
        if mem_found:
            mem = getMember(email)
            socket.emit("login status", {
                        'authorized': 1, 'user': mem}, room=request.sid)
        else:
            new_mem = registerMember("",fname,lname,email,link)
            print(new_mem.to_list())
            socket.emit("login status", {'authorized': 1,'user':new_mem.to_list()}, room=request.sid)

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
        mem_found = memberExists_by_email(email)
        print(mem_found)
        if mem_found:
            mem = getMember(email)
            socket.emit("login status", {
                        'authorized': 1, 'user': mem}, room=request.sid)
        else:
            new_mem = registerMember("",fname,lname,email,link)
            print(new_mem.to_list())
            socket.emit("login status", {'authorized': 1,'user':new_mem.to_list()}, room=request.sid)


@socket.on('new message')
def on_new_message(data):
    print('***********************************triggered new message********************************')
    floor_id = data['floor']
    member_id = data['from']
    text = data['message']
    add_message(floor_id, member_id, text)
    socket.emit("message added", {
                'floor_messages': getFloorMessages(floor_id)}, room=request.sid)



#function will return floor list object after initializing the object
#the function should users associated with it. member_id and floorid 
#floor_name and a floor_genre
# @app.route('/floors')
@socket.on('create floor')
def on_create(data):
    if data['is_public'] == 1:
        public = True
    else:
        public = False
    new_floor = add_floor(data['floor_name'],data['member_id'],public,data['floor_genre'])
    new_floor.add_member(data['member_id'])
    join_room(new_floor.floor_id)
    genre = data['floor_genre']
    songs = ds.getSongList(genre)
    new_floor.set_songlist(songs)
    socket.emit('floor created', {'floor':new_floor.to_list(),'song list':songs},room=new_floor.floor_id)


@socket.on('join floor')
#join room, function expects data to be json array/objects
# expects keys 'floor_id', 'member_id, returns jsonarray to parse
def on_join_floor(data):
    floor_id = data['floor_id']
    join_room(floor_id)
    floor = getFloor(floor_id)
    floor.add_member(data['member_id'])
    socket.emit('member joined', {'floor':floor.to_list()}, room=floor_id) 
    
@socket.on('leave floor')
def on_leave_floor(data):
    current_floor = getFloor(data['floor_id'])
    current_floor.rm_member(data['member_id'])
    current_floor = getFloor(data['floor_id'])
    leave_room(data['floor_id'])
    socket.emit('member left', {'floor':current_floor.to_list()})

    


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


#on login: