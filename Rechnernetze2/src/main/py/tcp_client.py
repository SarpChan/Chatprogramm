
from PyQt5.QtCore import QObject, pyqtSignal

import time
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
        self.chatListe = {}
        
        
     
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
        
        '''UDP Verbindung'''
        self.received = False
        self.setSend = True
        self.ok = ("0000004F004B0000").encode()
        self.chatPartner = ""
        self.chatten = False
        
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
            self.sendText("8 " + self.anfrageliste[-1])
            self.chatten = True
        else:
            self.sendText("9 " + self.anfrageliste[-1])
        
    
    def buildUdpConnection(self, line):
        ''' Erstellt eine neue UDP connection zu einem Client'''
        self.udpIP = chatPort = int(line.split(" ")[3])
        self.udpPort = chatHostAdresse = line.split(" ")[2]
        self.meinPort = int(line.split(" ")[1])
        self.chatPartner = line.split(" ")[4]
       
        
        self.received = False
        self.setSend = True
        
       
        self.clientSocket = socket(AF_INET, SOCK_DGRAM)
        self.clientSocket.bind((("127.0.0.1"), int(self.meinPort)))
        
        self.clientSocketSenden = socket(AF_INET, SOCK_DGRAM)
        
        self.chatEmpfangenThread = threading.Thread(target = self.chatEmpfangen)
        self.chatEmpfangenThread.start()
        
        threadsendBestaetigung = threading.Thread(target = self.sendBestaetigung)
        threadsendBestaetigung.start()
    
    def sendBestaetigung(self):
        ''' Thread der bei eingehender nachricht eine Sende Bestaetigung zurueck sendet'''
        while True:
            if self.received:
                    self.clientSocketSenden.sendto(self.ok, (str(self.udpPort), int(self.udpIP)))
                    self.received = False
                    print("SENDING OK")
       
    def chatEmpfangen(self):
        ''' Thread der eingehende nachrichten empfaengt'''
        while(True):
            modifiedMessage, data = self.clientSocket.recvfrom(2048)
            if(modifiedMessage == self.ok):
                self.setSend = True
                print("RECEIVED OK")   
            else:
                self.received = True
                print("FROM CHATPARTNER: " + modifiedMessage.decode())
                  
            if(modifiedMessage != self.ok):
                print(type(modifiedMessage.decode())    )
                print(type(self.benutzername)  )
                print(type(self.chatPartner)   )
                self.nachrichtZuChatListe(self.benutzername + self.chatPartner, modifiedMessage.decode().rstrip(), self.chatPartner)
           
       
    def send(self, message):
        '''Sendet Nachrichten an Chatpartner'''
        self.nachrichtZuChatListe(self.benutzername + self.chatPartner, message, self.benutzername)
        message  = message + " \n"

        for i in range(4):
            print("senden:",message, self.udpIP,self.udpPort)
            self.setSend = False
            if message != self.ok:
                self.clientSocketSenden.sendto(message.encode(), (str(self.udpPort), int(self.udpIP)))
                print("habe nachricht nochmal gesendet:",message, self.udpIP,self.udpPort)  
                time.sleep(2)
            if self.setSend:
                break
        
        
    
    def endUdpConnection(self):
        print("threads beenden")
        self.clientSocketSenden.close()
        self.clientSocket.close()
        self.chatEmpfangenThread._stop()
        self.threadsendBestaetigung._stop()
        
     
                
    def nachrichtZuChatListe(self, key,  message, sender):

        if key in self.chatListe:
            self.chatListe[key].append(sender + " : " + message)
        else:
            self.chatListe[key] = [sender + " : " + message]
        print(self.chatListe[key])
        dispatcher.send(signal="neueNachricht", sender = dispatcher.Any, liste = self.chatListe[key])

    
    def sendEndUdpConnection(self,name):
        self.send("10 " + name)
        self.chatten = False
        
        
    def processReceived(self):
        '''Verarbeitet alle eingehenden Nachriten des Servers'''
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
                if  self.chatten == True:
                    self.sendEndUdpConnection()
                    
                self.loggedIn = False
                dispatcher.send(signal=self.ausgeloggt, sender=dispatcher.Any)
                self.socket.close()
                break
                '''Chat von Chatpartner beendet'''
            elif antwort.split(" ")[0] == "10":
                self.endUdpConnection()
                self.chatten = False


                
    
def main():   
    c = ClientPy()
    

