#╔════════════╤══════════════════════════════════════╗
#║Author      │Carsen Yates                          ║
#╟────────────┼──────────────────────────────────────╢
#║Date created│04/23/2017                            ║
#╟────────────┼──────────────────────────────────────╢
#║Description │For making terminal output look pretty║
#╚════════════╧══════════════════════════════════════╝

import sys

HEADER = '\033[95m'
OKBLUE = '\033[94m'
OKGREEN = '\033[92m'
WARNING = '\033[93m'
FAIL = '\033[91m'
ENDC = '\033[0m'
BOLD = '\033[1m'
UNDERLINE = '\033[4m'
CYAN  = "\033[1;36m"
MAGENTA  = "\033[1;35m"

def remove_formats(string):
	for f in [HEADER, OKBLUE, OKGREEN, WARNING, FAIL, ENDC, BOLD, UNDERLINE, CYAN, MAGENTA]:
		string = string.replace(f, '')
	return string

# Formats a string for the terminal, format options are above
# returns a string
def format(string, f):
	return f + string + ENDC

def count_lines(string):
	return len(string.split('\n'))
	

#╔═╤═╗
#║1│2║
#╟─┼─╢
#║3│4║
#╚═╧═╝
# Turns a 2D array of strings into a string that is formatted into a box matrix like above
# This is a FUCKING masterpiece.  I wrote it for the pure fun of it
def get_box(box):
	result = '╔'

	# First we have to get the sizes of each box
	col_widths = []
	row_heights = []
	for i in box[0]:
		col_widths.append(0)
	for i in range(0, len(box)):
		row_heights.append(0)
	for i in range(0, len(box)):
		for j in range(0, len(box[i])):
			rf = remove_formats(str(box[i][j]))
			lines = rf.split('\n')
			length = len(lines)
			if length > row_heights[i]:
				row_heights[i] = length
			for line in lines:
				l = len(line)
				if l > col_widths[j]:
					col_widths[j] =  l
			
	# now lets print that shit
	for i in range(0, len(box)):
		x = box[i]
		# First we print the edge of the table that is above the current row
		if i == 0: # The top edge
			for j in range(0, len(x)):
				for k in range(0, col_widths[j]):
					result += ('═');
				if j == (len(x) - 1):
					result += ('╗\n')
				else:
					result += ('╤')
		else: # row separator
			result += ('╟')
			for j in range(0, len(x)):
				for k in range(0, col_widths[j]):
					result += ('─');
				if j == (len(x) - 1):
					result += ('╢\n')
				else:
					result += ('┼')

		# now we print the current row
		# NEW
		y = []
		for j in range(0, len(x)):
			y.append(str(x[j]).split('\n'))
		for k in range(0, row_heights[i]): # LINE
			result += ('║')
			for j in range(0, len(x)): # COLUMN
				numlines = len(y[j])
				c = 0
				if k < numlines:
					z = str(y[j][k])
					result += z
					c += len(remove_formats(z))
				for q in range(c, col_widths[j]):
					result += ' '

				if j == (len(x) - 1):
					result += ('║\n')
				else:
					result += ('│')			
			
				

		# now print the bottom part of the table
		if i == (len(box) - 1):
			result += ('╚')
			for j in range(0, len(x)):
				for k in range(0, col_widths[j]):
					result += ('═');
				if j == (len(x) - 1):
					result += ('╝\n')
				else:
					result += ('╧')
	return result

# Turns a 2D array of strings into a string that is formatted into a box matrix like above
# This is a FUCKING masterpiece.  I wrote it for the pure fun of it
def get_boxf(box, text_format, box_format):
	result = format('╔',box_format)

	# First we have to get the sizes of each box
	col_widths = []
	row_heights = []
	for i in box[0]:
		col_widths.append(0)
	for i in range(0, len(box)):
		row_heights.append(0)
	for i in range(0, len(box)):
		for j in range(0, len(box[i])):
			rf = remove_formats(str(box[i][j]))
			lines = rf.split('\n')
			length = len(lines)
			if length > row_heights[i]:
				row_heights[i] = length
			for line in lines:
				l = len(line)
				if l > col_widths[j]:
					col_widths[j] =  l
			
	# now lets print that shit
	for i in range(0, len(box)):
		x = box[i]
		# First we print the edge of the table that is above the current row
		if i == 0: # The top edge
			for j in range(0, len(x)):
				result += box_format
				for k in range(0, col_widths[j]):
					result += ('═');
				if j == (len(x) - 1):
					result += ('╗'+ENDC+'\n')
				else:
					result += ('╤')
		else: # row separator
			result += (box_format + '╟')
			for j in range(0, len(x)):
				for k in range(0, col_widths[j]):
					result += ('─');
				if j == (len(x) - 1):
					result += ('╢'+ENDC+'\n')
				else:
					result += ('┼')

		# now we print the current row
		# NEW
		y = []
		for j in range(0, len(x)):
			y.append(str(x[j]).split('\n'))
		for k in range(0, row_heights[i]): # LINE
			result += format('║', box_format)
			for j in range(0, len(x)): # COLUMN
				numlines = len(y[j])
				c = 0
				if k < numlines:
					z = str(y[j][k])
					result += format(z, text_format)
					c += len(remove_formats(z))
				for q in range(c, col_widths[j]):
					result += ' '

				if j == (len(x) - 1):
					result += (format('║', box_format)+'\n')
				else:
					result += format('│', box_format)			
			
				

		# now print the bottom part of the table
		if i == (len(box) - 1):
			result += (box_format+'╚')
			for j in range(0, len(x)):
				for k in range(0, col_widths[j]):
					result += ('═');
				if j == (len(x) - 1):
					result += ('╝'+ENDC+'\n')
				else:
					result += ('╧')
	return result

# prints a string without a newline
def s_print(string):
	sys.stdout.write(string)
	sys.stdout.flush()
	
# takes a 2D array of strings and prints it in the console magically
def box(box):
	s_print(get_box(box))

# takes a 2D array of strings and prints it in the console magically
def boxf(box, text_format, box_format):
	s_print(get_boxf(box, text_format, box_format))

# Prints a string inside a box
def rect(string):
	box([[string]])

# Formats a string and prints it inside a box
def rectf(string , f):
	return rect(format(string, f))


# For printing verbose info
def i(event):
	s_print(get_boxf([["INFO"],[event]], BOLD, OKBLUE))

# For printing debug info
# I suppose we can define debug info as anything that doesn't need to be logged after it's done being tested
def d(event):
	s_print(get_boxf([["DEBUG"],[event]], BOLD, OKGREEN))

# For printing warnings
def w(event):
	s_print(get_boxf([["WARNING"],[event]], FAIL + BOLD, WARNING))

# For printing failures/errors
def e(event):
	s_print(get_boxf([[format("FAIL", MAGENTA)],[event]], FAIL + UNDERLINE + BOLD, FAIL))

# For printing a socket EVENT
def sock(event):
	s_print(
		get_boxf([["SOCKET EVENT RECIEVED"],[event]], BOLD + OKGREEN, CYAN)
	)

# For printing a socket EMIT
def emit(event):
	s_print(
		get_boxf([["SOCKET EMIT"],[event]], BOLD + OKGREEN, MAGENTA)
	)

# same as map but with specific formatting
def get_json(json):
	return (get_mapf(json, BOLD + OKGREEN, ''))

# same as map but with specific formatting
def json(json):
	s_print(get_json(json))

# Takes a dict object(like json) and prints it out in a table
def get_map(map):
	box = []
	for key, value in map.items():
		box.append([key, value])

# Takes a dict object(like json) and prints it out in a table
def get_mapf(map, text_format, box_format):
	b = []
	for key, value in map.items():
		b.append([key, value])
	return get_boxf(b, text_format, box_format)

# prints a dictionary in a table
def map(map):
	s_print(get_map(map))

# prints a dictionary in a table
def mapf(map, text_format, box_format):
	s_print(get_mapf(map, text_format, box_format))

# test area
def test():
	i("test info")
	d("test debug message")
	w("test warning")
	e("test error or failure")
	sock("test socket event")
	emit("test socket emit")
	box([
		['Author', 'Carsen Yates'],
		['Date created', '04/23/2017'],
		['Description', 'For making terminal\noutput look pretty']
	])
	boxf([
		['IM A BOX\n IMA BOX\n\n\n\n\n\n\n yur mum', 'One fish\ntwo fish\nred fish\nblueeeeeeeeeeeeeeeeeeeeeeeeee fish'],
		[get_mapf({
			'poops': 'farts',
			'dicks': 'buttholes',
			'boobs': 'vagina',
		}, UNDERLINE, CYAN), 'smoke weed all of the days'],
	], BOLD, MAGENTA)
	
