
# When a client opens the socket
EVENT_CONNECT = "connect";

# When the Song List is updated for the Floor
EVENT_SONG_LIST_UPDATE = "song list update";

# When the Message List is updated for the Floor
EVENT_USER_LIST_UPDATE = "member list update";

# When the Message List is updated for the Floor
EVENT_MESSAGE_LIST_UPDATE = "message list update";

# When the UI requests the most recent Song List
EVENT_GET_SONG_LIST = "get song list";

# When the UI requests the most recent Message List
EVENT_GET_MESSAGE_LIST = "get message list";

# When the UI requests the most recent User List
EVENT_GET_USER_LIST = "get member list";

# When a member leaves the Floor that LocalUser is in
EVENT_USER_REMOVE = "remove member";

# When a member joins the Floor that LocalUser is in
EVENT_USER_ADD = "add member";

# When there is a new message from the server to add to the Floor
EVENT_MESSAGE_ADD = "add message";

# When the current LocalUser sends a message
EVENT_MESSAGE_SEND = "new message";

# When the user requests to join the floor
EVENT_JOIN_FLOOR = "join floor";

# When the user requests to leave the floor
EVENT_LEAVE_FLOOR = "leave floor";

# When the server acknowledges that the client has joined the floor
# This event also contains the entire floor object according to Ryan
EVENT_FLOOR_JOINED = "floor joined";
