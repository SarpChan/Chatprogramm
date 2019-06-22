__author__ = 'schaible'


"""  TCPClient.py
Use the better name for this module:   MakeUpperCaseClientUsingTCP

[STUDENTS FILL IN THE ITEMS BELOW]
  STUDENT NAME
  COURSE NAME and SEMESTER
  DATE
  This module will <blah, blah, blah>
"""

from socket import *

# STUDENTS - replace your server machine's name
'''serverName = "localhost"

# STUDENTS - you should randomize your port number.
# This port number in practice is often a "Well Known Number"
#serverPort = 12000
serverPort = 27999'''

# create TCP socket on client to use for connecting to remote
# server.  Indicate the server's remote listening port
# Error in textbook?   socket(socket.AF_INET, socket.SOCK_STREAM)  Amer 4-2013
'''clientSocket = socket(AF_INET, SOCK_STREAM)

# open the TCP connection
clientSocket.connect((serverName,serverPort))

# interactively get user's line to be converted to upper case
# authors' use of raw_input changed to input for Python 3  Amer 4-2013

#sentence = raw_input("Input lowercase sentence: ")



 
# send the user's line over the TCP connection
# No need to specify server name, port
# sentence casted to bytes for Python 3  Amer 4-2013

#clientSocket.sendall(sentence)
#client_socket.sendall(bytes(sentence , "utf8"))  

clientSocket.sendall("hello/n")
#output to console what is sent to the server
#print ("Sent to Server: ", sentence)

# get user's line back from server having been modified by the server
modifiedSentence = clientSocket.recv(1024)

# output the modified user's line
print ("Received from Make Upper Case Server: ", modifiedSentence)

# close the TCP connection
clientSocket.close()'''



serverName = "localhost"
serverPort = 27999

class ClientPy:
    def __init__(self):
       
        self.clientSocket = socket(AF_INET, SOCK_STREAM)
        self.clientSocket.connect((serverName,serverPort))
        self.benutzername = ""
        
    def sendText(self,text):
        
        print("Sende an Server " + text)
        text = text + " \n"
        print(text.encode())
        
        self.clientSocket.send(text.encode())
        antwort = self.clientSocket.recv(1024)
        print("Vom Server empfangen: " + str(antwort))
            
        return str(antwort)
        
        

    def login(self,username, password, option, client_Socket):
        print("login")
        password = passwort.reverse()
        line = option + " " + username + " " + password;
        
        antwort = sendText(line)
        if antwort.equal("200"):
            self.benutzername = username;
            return True
        return False
        
    def closeConnection(self):
        #if self.benutzername != "":
        text = self.sendText("3 " + self.benutzername)
            
        if text.rfind("200"):
            self.clientSocket.close()
        
        
        
        
    def requestActiveUser(self):
        text = self.sendText("7 " + self.benutzername)
        return text

def main():   
    c = ClientPy()
    #Registrieren:
    
    antwort = c.sendText("0 a b")   
    c.closeConnection()
        
main()




