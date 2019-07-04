
#from PyQt5.QtCore import QObject, pyqtSignal


from socket import *

import threading 



#from thread import start_new_thread


#class UpdatedListeEvent(QObject):
#    updatedEvent = pyqtSignal()


serverName = "localhost"
serverPort = 27999

class ClientPy:
    def __init__(self):

        self.nutzerliste = []
        #Server TCP Verbindung
        self.socket = socket(AF_INET, SOCK_STREAM)
        self.socket.connect((serverName,serverPort))
        self.benutzername = ""
        self.loggedIn = False;
        
    def sendText(self,text):
        print("Sende an Server " + text)
        text = text + " \n"
      
        self.socket.send(text.encode())
        #antwort = self.socket.recv(1024)
        #print("Vom Server empfangen: " + str(antwort))
            
        #return str(antwort)
        
    # 0 = Registrieren  1 = Login
    def login(self,username, password, option):
        print("login")
        # "sichere" Übertragung des Passworts
        password = password[::-1] 
        line = option + " " + username + " " + password;
        
        self.sendText(line)
        self.benutzername = username;
       
    
        
    def closeConnection(self):
        text = self.sendText("3 " + self.benutzername)
            
       
           
        
    def schliessen(self):
        self.sendText("6 ");    
        
        
    def requestActiveUser(self):
        text = self.sendText("7 " + self.benutzername)
        print(self.benutzername + "name")
        return text



    # bei Server UDP-Connection mit user "name" anfragen
    def requestUdpConnection(self, name):
        self.sendText("2 " + name)
        
    def answerUdpConnection(self, bool):
        if(bool):
            sendText("8 " + chatanfrage.getVon());
        else:
            sendText("9 " + chatanfrage.getVon());
    
    def buildUdpConnection(self, line):
        chatPort = int(line.split(" ")[1])
        chatHostAdresse = line.split(" ")[2]
        meinPort = int(line.split(" ")[3])
        
        #neuer Sochet fuer UDP ?
        clientSocket = socket(AF_INET, SOCK_DGRAM)
        chatEmpfangenThread = threading.Thread(target = self.chatEmpfangen)
        sendThread = threading.Thread(target = self.chatSenden, args=(chatHostAdresse, chatPort))
    
    def chatEmpfangen(self):
        while(true):
            modifiedMessage, serverAddress = socket.recvfrom(2048)
            
    def chatSenden(self, udpIP, udpPort):
        while(true):
            message = bytes(input("Chat sentence you: "), 'utf-8')
            #IP des Chatpartners + port
            socket.sendto(MESSAGE, (udpIP, udpPort))
    
    def endUdpConnection(self, socket):
        print("threads beenden")
        
        
        
    def processReceived(self):
        while True:
            
            print("bin hier")
            antwort = self.socket.recv(1024).decode("utf-8")
            print("Vom Server empfangen", antwort)
            
            ''' Chatanfrage bekommen'''
            if antwort.split(" ")[0] == "5":
                print("Chat anfrage von " + antwort.split(" ")[1])
            
                ''' erfolgreiches einloggen/ registrieren '''
            elif antwort.split(" ")[0] == "1" or antwort.split(" ")[0] == "0" : 
                if antwort.split(" ")[1] == "200":
                    self.loggedIn = True 
                      
                ''' Chatanfrage angenommen'''
            elif antwort.split(" ")[0] == "2":
                self.buildUdpConnection(antwort)
                print("Chat anfrage angenommen")
                
                ''' erfolgreich ausgelogged'''
            elif antwort == "3 200":
                self.loggedIn = False
                
                ''' Aktive Nutzerliste '''
            elif antwort.split(" ")[0] == "7":
                self.nutzerliste = antwort.split(" ")[1:]
            
                '''Schliessen'''
            elif antwort.split(" ")[0] == "6":
                 self.loggedIn = False
                 self.socket.close()
                
    
def main():   
    c = ClientPy()
    
    t = threading.Thread(target = c.processReceived)
    t.start()
    #Registrieren:
    c.login("o0o", "p", "0")
    
    #while True:

    #c.requestUdpConnection("i")
    #antwort = c.sendText("0 a b") 
    
     
        #
        #sentence = input("Input lowercase sentence: ")
        #c.sendText(sentence)
      
    c.closeConnection()
        
main()




