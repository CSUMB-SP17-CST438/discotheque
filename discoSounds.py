import soundcloud as sc
# client_secret='67422b6c159a389c9cfed1a9607227ef'

client = sc.Client(client_id="8c1cf28d0d2834808a2eda6645da717b",client_secret='67422b6c159a389c9cfed1a9607227ef')
def getSongList(g):
	##number of results per page
	trackList = []
	scTracks = client.get('/tracks', tags=g, limit=40, streamable='True', track_type='original', order='created_at')
	i = 0
	for t in scTracks:
		#parse track information to what is relevant to us
		#returns duration in milliseconds to sync songs. 
		username = t.user['username']
		# stream = client.get(t.stream_url,allow_redirects=False).location
		new_track = {'id':t.id,'title':t.title, 'creator_user':username,'track_permalink':t.permalink_url,'stream_url':"nothing yet",'artwork':t.artwork_url, 'duration':t.duration,'start_time':'0'}
		trackList.append(new_track)
	for x in range(0,4):
		streamLoc = getSongURLLocation(trackList[x]['id'])
		trackList[x]['stream_url'] = streamLoc
	return trackList

def add_stream_urls(songlist):
	for x in range(0,6):
		streamLoc = getSongURLLocation(songlist[x]['id'])
		songlist[x]['stream_url'] = streamLoc
	return songlist



def getSongURLLocation(track_id):
	track = '/tracks/' + str(track_id) 
	tracks = client.get(track)
	track_stream = client.get(tracks.stream_url, allow_redirects=False)
	# print(track_stream.location)
	return track_stream.location

# songlist = getSongList('punk')
# t = songlist[0]
# # print(getSongURLLocation(t['id']))
# # sl = add_stream_urls(songlist)
# print(songlist)

# print(songlist)
