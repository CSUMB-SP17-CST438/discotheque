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
			rf = remove_formats(box[i][j])
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
			y.append(x[j].split('\n'))
		for k in range(0, row_heights[i]): # LINE
			result += ('║')
			for j in range(0, len(x)): # COLUMN
				numlines = len(y[j])
				c = 0
				if k < numlines:
					result += y[j][k]
					c += len(remove_formats(y[j][k]))
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

# prints a string without a newline
def s_print(string):
	sys.stdout.write(string)
	sys.stdout.flush()
	
# takes a 2D array of strings and prints it in the console magically
def print_box(box):
	s_print(get_box(box))

# Formats a string and prints it inside a box
def print_rect(string , f):
	return rect(format(string, f))

# Prints a string inside a box
def print_rect(string):
	print_box([[string]])


# For printing verbose info
def logbox_i(event):
	s_print(format(get_box([[ENDC + format("INFO", HEADER) + OKGREEN],[event]]), OKGREEN))

# For printing debug info
# I suppose we can define debug info as anything that doesn't need to be logged after it's done being tested
def logbox_d(event):
	s_print(format(get_box([[ENDC + format("DEBUG", HEADER) + OKBLUE],[event]]), OKBLUE))

# For printing warnings
def logbox_w(event):
	s_print(format(get_box([[ENDC + format("WARNING", HEADER) + WARNING],[event]]), WARNING))

# For printing failures/errors
def logbox_e(event):
	s_print(format(get_box([[ENDC + format("FAIL", HEADER) + FAIL],[event]]), FAIL))

# For printing a socket EVENT
def logbox_sock(event):
	s_print(format(get_box([[ENDC + format("SOCKET EVENT RECIEVED", HEADER) + CYAN],[ENDC + format(event, OKGREEN) + CYAN]]), CYAN))

# For printing a socket EMIT
def logbox_emit(event):
	s_print(format(get_box([[ENDC + format("SOCKET EMIT", HEADER) + MAGENTA],[ENDC + format(event, OKGREEN) + MAGENTA]]), MAGENTA))

		

# test area
logbox_i("test info")
logbox_d("test debug message")
logbox_w("test warning")
logbox_e("test error or failure")
logbox_sock("test socket event")
logbox_emit("test socket emit")
print_box([
	['Author', 'Carsen Yates'],
	['Date created', '04/23/2017'],
	['Description', 'For making terminal\noutput look pretty']
])
print_box([
	['IM A BOX\n IMA BOX\n\n\n\n\n\n\n yur mum', 'One fish\ntwo fish\nred fish\nblueeeeeeeeeeeeeeeeeeeeeeeeee fish'],
	['breh', 'smoke weed all of the days'],
])
