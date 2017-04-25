
# When a client opens the socket
CONNECT = "connect";

# When a client attempts login
LOGIN = "login";

# When the server replies to login
LOGIN_STATUS = "login status";

# When the client asks for the floor list
GET_FLOOR_LIST = "get floor list";

# When the client asks for the floor list
FLOOR_LIST_UPDATE = "floor list update";

# When the Song List is updated for the Floor
SONG_LIST_UPDATE = "song list update";

# When the Message List is updated for the Floor
USER_LIST_UPDATE = "member list update";

# When the Message List is updated for the Floor
MESSAGE_LIST_UPDATE = "message list update";

# When the UI requests the most recent Song List
GET_SONG_LIST = "get song list";

# When the UI requests the most recent Message List
GET_MESSAGE_LIST = "get message list";

# When the UI requests the most recent User List
GET_USER_LIST = "get member list";

# When a member leaves the Floor that LocalUser is in
USER_REMOVE = "remove member";

# When a member joins the Floor that LocalUser is in
USER_ADD = "add member";

# When there is a new message from the server to add to the Floor
MESSAGE_ADD = "add message";

# When the current LocalUser sends a message
MESSAGE_SEND = "new message";

# When the user requests to join the floor
JOIN_FLOOR = "join floor";

# When the user requests to leave the floor
LEAVE_FLOOR = "leave floor";

# When the server acknowledges that the client has joined the floor
# This event also contains the entire floor object period.
FLOOR_JOINED = "floor joined";

#When profile has been updated it will emit this message 
#The event contains the member object with updates
PROFILE_UPDATE = "profile updated"
