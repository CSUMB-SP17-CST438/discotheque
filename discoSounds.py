import soundcloud as sc
# client_secret='67422b6c159a389c9cfed1a9607227ef'

client = sc.Client(client_id="8c1cf28d0d2834808a2eda6645da717b",client_secret='67422b6c159a389c9cfed1a9607227ef')
def getSongList(g):
	##number of results per page
	trackList = []
	scTracks = client.get('/tracks', genres=g, limit=40, streamable='True', track_type='original', order='created_at')
	i = 0
	for t in scTracks:
		#parse track information to what is relevant to us
		username = t.user['username']
		t_stream = client.get(scTracks[i].stream_url, allow_redirects=False)
		new_track = {'title':t.title, 'creator_user':username,'track_permalink':t.permalink_url,'stream_url':str(t_stream.location),'artwork':t.artwork_url}
		trackList.append(new_track)
		i+1
	return trackList


def getSongURLLocation():
	tracks = client.get('/tracks',genres='punk',limit=1,streamable=True)
	track_stream = client.get(tracks[0].stream_url, allow_redirects=False)
	# print(tracks[0].stream_url)

	# print("stream.loc:")
	# print(track_stream.location)


getSongURLLocation()
print("songlist")
print(getSongList("punk"))