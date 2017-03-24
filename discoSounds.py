import soundcloud as sc
# client_secret='67422b6c159a389c9cfed1a9607227ef'



client = sc.Client(client_id="8c1cf28d0d2834808a2eda6645da717b",client_secret='67422b6c159a389c9cfed1a9607227ef')
def getSongList(g):
	##number of results per page
	trackList = []
	scTracks = client.get('/tracks', genres=g, limit=40, streamable='True', track_type='original', order='created_at')
	for t in scTracks:
		#parse track information to what is relevant to us
		username = t.user['username']
		new_track = {'title':t.title, 'creator_user':username,'track_permalink':t.permalink_url,'stream_url':t.stream_url,'artwork':t.artwork_url}
		trackList.append(new_track)
	return trackList

# print(getSongList("punk"))