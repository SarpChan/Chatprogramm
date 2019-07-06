
from PyQt5.QtCore import QObject, pyqtSignal


from socket import *
from pydispatch import dispatcher

import threading 
from pip._internal.cli.cmdoptions import client_cert



#from thread import start_new_thread


#class UpdatedListeEvent(QObject):
#    updatedEvent = pyqtSignal()


serverName = "localhost"
serverPort = 27999


class registOKEvent(QObject):
    registokEvent = pyqtSignal();

class ClientPy:
    def __init__(self):
        self.anfrageliste = []
        self.nutzerliste = []
        self.sendeTreads = []
        self.chatEmpfangenThreads = []
        
        self.nachrichtZuChatListe = []
     
        #Server TCP Verbindung
        self.socket = socket(AF_INET, SOCK_STREAM)
        self.socket.connect((serverName,serverPort))
        self.benutzername = ""
        self.loggedIn = False;
        self.sig = registOKEvent()
        self.logRegErfolgSig = 'logErfolg'
        self.chatAufgebaut = 'chatErfolg'
        self.ausgeloggt = 'logout'
        self.refillNutzer = 'refillNutzer'
        self.neueChatAnfrage = 'neueChatAnfrage'
        self.neueNachricht = "neueNachricht"
        t = threading.Thread(target = self.processReceived)
        t.start()
        
    def sendText(self,text):
        print("Sende an Server " + text)
        text = text + " \n"
        self.socket.send(text.encode())
       
        
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
        print("hallo")
        if(bool):
            self.sendText("8 " + self.anfrageliste[-1]);
        else:
            self.sendText("9 " + self.anfrageliste[-1]);
        
    
    def buildUdpConnection(self, line):
        self.udpIP = chatPort = int(line.split(" ")[3])
        self.udpPort = chatHostAdresse = line.split(" ")[2]
        self.meinPort = int(line.split(" ")[1])
        self.chatPartner = line.split(" ")[4]
        self.ok = ("0000004F004B0000").encode()
        
        self.received = False
        self.setSend = True
        
        #neuer Sochet fuer UDP ?
        self.clientSocket = socket(AF_INET, SOCK_DGRAM)
        self.clientSocket.bind((("127.0.0.1"), int(self.meinPort)))
        
        self.clientSocketSenden = socket(AF_INET, SOCK_DGRAM)
        
        chatEmpfangenThread = threading.Thread(target = self.chatEmpfangen)
        #sendThread = threading.Thread(target = self.chatSenden, args=(chatHostAdresse, chatPort, clientSocket))
        chatEmpfangenThread.start()
        #sendThread.start()
        #self.sendeTreads.append([clientSocket, sendThread])
        self.chatEmpfangenThreads.append([self.clientSocket, chatEmpfangenThread])
        threadsendBestaetigung = threading.Thread(target = self.sendBestaetigung)
        threadsendBestaetigung.start()
    
    def sendBestaetigung(self):
        while True:
            if self.received: 
                    self.send(self.ok)
                    self.received = False
                    print("SENDING OK")
       
    def chatEmpfangen(self):

        while(True):
            modifiedMessage, data = self.clientSocket.recvfrom(2048)
            if(modifiedMessage == self.ok):
                self.setSend = True
                print("RECEIVED OK")
            else:
                self.received = True
                print("FROM CHATPARTNER: " + modifiedMessage.decode())
               
                
            print("message empfangen: " , modifiedMessage , data)
            #self.nachrichtZuChatListe(user + sender, message, sender)
            #self.nachrichtZuChatListe(self.benutzername + modifiedMessage.split("'")[0], modifiedMessage.split("'")[1], modifiedMessage.split("'")[0])
            
    
       
    def send(self, message):
        print("senden:",message, self.udpIP,self.udpPort)
        
        if message != self.ok:
            message  = message + " \n"
  
        for i in range(4):
            self.setSend = False
            if message != self.ok:
                self.clientSocketSenden.sendto(message.encode(), (str(self.udpPort), int(self.udpIP)))
            else:
                self.clientSocketSenden.sendto(message, (str(self.udpPort), int(self.udpIP)))
            if self.setSend:
                break
        
        
        
        
    
    def endUdpConnection(self):
        print("threads beenden")
        #for thread in self.sendeTreads:
        #   if i[0] == clientsocket:
        #        i[1].stop()
        #       self.sendeTreads.remove(i)
            
        for i in self.chatEmpfangenThreads:
            if i[0] == self.clientsocket:
                i[1].stop()
                self.chatEmpfangenThreads.remove(i)
        self.clientSocket.close()
                
                
    def nachrichtZuChatliste( key,  message, sender):

        #TODO ZU Liste hinzu
        dispatcher.send(signal="neueNachricht", sender = dispatcher.Any, user = sender, message = message )

        
        
    def processReceived(self):
        while True:
            
            
            antwort = self.socket.recv(1024).decode("utf-8")
            print("Vom Server empfangen", antwort)
            
            ''' Chatanfrage bekommen'''
            if antwort.split(" ")[0] == "5":
                
                print("Chat anfrage von " + antwort.split(" ")[1])
                self.anfrageliste.append(antwort.split(" ")[1])
                dispatcher.send(signal = self.neueChatAnfrage, sender = dispatcher.Any, anfrager = antwort.split(" ")[1])

            
                ''' erfolgreiches einloggen/ registrieren '''
            elif antwort.split(" ")[0] == "1" or antwort.split(" ")[0] == "0" : 
                if antwort.split(" ")[1] == "200":
                    self.loggedIn = True
                    dispatcher.send(signal = self.logRegErfolgSig, sender = dispatcher.Any)



                      
                ''' Chatanfrage angenommen'''
            elif antwort.split(" ")[0] == "2":
                self.buildUdpConnection(antwort)
                dispatcher.send(signal = self.chatAufgebaut, sender = dispatcher.Any)
                print("Chat anfrage angenommen")
                
                ''' erfolgreich ausgelogged'''
            elif antwort.split(" ")[0] == "3":

                if antwort.split(" ")[1] == '200':
                    self.loggedIn = False

                    dispatcher.send(signal = self.ausgeloggt, sender = dispatcher.Any)

                ''' Aktive Nutzerliste '''
            elif antwort.split(" ")[0] == "7":
                print(antwort)
                if len(antwort.split(" ")) > 1:
                    self.nutzerliste = []
                    self.nutzerliste = antwort.split(" ")[1:-1]
                    dispatcher.send(signal = self.refillNutzer, sender = dispatcher.Any, liste = self.nutzerliste)
                    print(self.nutzerliste)
                
                '''Schliessen'''
            elif antwort.split(" ")[0] == "6":
                 self.loggedIn = False
                 dispatcher.send(signal=self.ausgeloggt, sender=dispatcher.Any)
                 self.socket.close()
                 break


                
    
def main():   
    c = ClientPy()
    
    
    #Registrieren:
   # log = c.login("o", "p", "0")
    #print(log)
    ##while True:
    
        
    #c.requestUdpConnection("i")
    #antwort = c.sendText("0 a b") 
    
     
        #
        #sentence = input("Input lowercase sentence: ")
        #c.sendText(sentence)
      
    #c.closeConnection()
        
#main()




