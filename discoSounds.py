import soundcloud as sc
import json
import requests
# client_secret='67422b6c159a389c9cfed1a9607227ef'
client = sc.Client(client_id='VouLZKnkCQYDVMu86Y8dJ1Nt7h8DMeT0',
	client_secret='gRIEjS8jCajOpJ3Gkcgs3e1wg8vf39KA')
# client.authorize_url()


def getSongList(g):
	##number of results per page
	trackList = []
	scTracks = client.get('/tracks', tags=g, limit=50, streamable='True', track_type='original',sharing='public',)
	i = 0
	for t in scTracks:
		#parse track information to what is relevant to us
		#returns duration in milliseconds to sync songs. 
		if t.duration <= 240000:
			username = t.user['username']
			new_track = {'id':t.id,'title':t.title, 'creator_user':username,'track_permalink':t.permalink_url,'stream_url':"",'artwork':t.artwork_url, 'duration':t.duration,'start_time':''}
			trackList.append(new_track)
	# for x in range(0,4):
	# 	trackList[x]['stream_url'] = getSongURLLocation(trackList[x]['id'])
	return trackList


def getSongURLLocation(track_id):
	track = '/tracks/' + str(track_id) 
	tracks = client.get(track)
	track_stream = client.get(tracks.stream_url, allow_redirects=False)
	# print(track_stream.location)
	return track_stream.location

def refresh_streams(songList):
	for x in range(0,4):
		songList[x]['stream_url'] = getSongURLLocation(songList[x]['id'])
	return songList

def refresh_song(song,start_time):
	# print("****INSIDE REFRESH SONG****",song)
	song['stream_url'] = getSongURLLocation(song['id'])
	song['start_time'] = start_time
	return song


def try_i1(track_id):
	track = 'i1/tracks/'+str(track_id)
	# resolved = client.get(track)
	resolved = requests.get(('https://api.soundcloud.com/i1/tracks/' + str(track_id) + '/streams?client_id=VouLZKnkCQYDVMu86Y8dJ1Nt7h8DMeT0'))
	json = resolved.json()
	# print(json)
	if json['http_mp3_128_url'] is not None:
		return json['http_mp3_128_url']
	else:
		return False	
	

# print("********songlist*******")
# songs = getSongList("rock")
# print(songs[0])
# url = getSongURLLocation(songs[0]['id'])
# il1 = try_i1(songs[0]['id'])

# print("getsongURL",url)

# print("ul:1", il1)
# if url == il1:
# 	print("yes muthafuckaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
# print(try_i1(319172154))
# print(json.dumps(songs,indent=4))

# print("****refreshed*****")
# print(json.dumps(refresh_streams(songs),indent=4))