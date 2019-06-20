__author__ = 'schaible'

"""  UDPClient.py
Use the better name for this module: MakeUpperCaseClientUsingUDP

[STUDENTS FILL IN THE ITEMS BELOW]
  STUDENT NAME
  COURSE NAME and SEMESTER
  DATE
  This module will <blah, blah, blah>
"""

from socket import *

# STUDENTS - replace with your server machine's name
serverName = "localhost"

# STUDENTS - randomize your port number (use the same one in the server)
# This port number in practice is often a "Well Known Number"
serverPort = 12000
#serverPort = 27999

# create UDP socket
# Error in book? clientSocket = socket(socket.AF_INET, socket.SOCK_DGRAM)
#    corrected by  Amer 4-2013
clientSocket = socket(AF_INET, SOCK_DGRAM)


# get user's input from keyboard
# raw_input changed to input for Python 3  Amer 4-2013
message = bytes(input("Input lowercase sentence: "), 'utf-8')

# send user's sentence out socket; destination host and port number req'd
# need to cast message to bytes for Python 3   Amer 4-2013
clientSocket.sendto((message), (serverName, serverPort))

print ("Sent to Make Upper Case Server running over UDP: ", message)

# receive modified sentence in all upper case letters from server
modifiedMessage, serverAddress = clientSocket.recvfrom(2048)

# output modified sentence and close the socket
print ("Received back from Server: ", modifiedMessage)

# close the UDP socket
clientSocket.close()