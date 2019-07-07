
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
        self.logRegErfolgSig = 'logErfolg'
        self.chatAufgebaut = 'chatErfolg'
        self.ausgeloggt = 'logout'
        self.refillNutzer = 'refillNutzer'
        self.neueChatAnfrage = 'neueChatAnfrage'
        self.neueNachricht = "neueNachricht"
        self.chatBeendet = "chatEnde"
        t = threading.Thread(target = self.processReceived)
        t.start()
        
        '''UDP Verbindung'''
        self.received = False
        self.setSend = True
        self.ok = ("0000004F004B0000").encode()
        self.chatPartner = ""
        self.chatten = False
        self.threadStop = False
        
        self.clientSocketbind = False
        
        
    def sendText(self,text):
        """Sendet Nachricht text an Server"""
        print("Sende an Server " + text)
        text = text + " \n"
        self.socket.send(text.encode())
       
        

    def login(self,username, password, option):
        """Loggt oder Registriert den Client beim Server. 0 = Login, 1 = Registrieren"""
        print("login")
        # "sichere" Übertragung des Passworts
        password = password[::-1] 
        line = option + " " + username + " " + password;
        
        self.sendText(line)
        self.benutzername = username;

    def closeConnection(self):
        """Leitet Abmelden mit Server ein. Server bleibt aktiv für einen login"""
        text = self.sendText("3 " + self.benutzername)

    def schliessen(self):
        """Leitet Abmelden mit Server ein. Socket wird geschlossen"""
        self.sendText("6 "+ self.benutzername);    

    def requestActiveUser(self):
        """Fragt den Server nach einer aktuellen Nutzerliste der aktiven Nutzer"""
        text = self.sendText("7 " + self.benutzername)
        print(self.benutzername + "name")
        return text

    # bei Server UDP-Connection mit user "name" anfragen
    def requestUdpConnection(self, name):
        """Sendet einem aktiven Nutzer über den Server eine Chatanfrage"""
        self.sendText("2 " + name)
        
    def answerUdpConnection(self, bool):
        """Antwortet auf eine Chatanfrage. True = akzeptieren, False = ablehnen"""
        print("hallo", self.chatten)
        if(bool):
            if self.nutzerliste.__contains__(self.anfrageliste[-1]):
                if self.chatten:
                    print("beende conection")
                    self.sendEndUdpConnection()
                    time.sleep(2)
                   
                self.sendText("8 " + self.anfrageliste[-1])
                print("self.chatten", self.chatten)
                self.chatten = True
                
                print("anfrager:", self.anfrageliste[-1])
        else:
            self.sendText("9 " + self.anfrageliste[-1]);

    def buildUdpConnection(self, line):
        """Baut udp Connection mit anderem aktiven Nutzer auf"""
        
        self.udpIP = chatPort = int(line.split(" ")[3])
        self.udpPort = chatHostAdresse = line.split(" ")[2]
        self.meinPort = int(line.split(" ")[1])
        self.chatPartner = line.split(" ")[4]
        print("connection: ", line)
        
        
        self.loadChat(self.benutzername + self.chatPartner)
        
        self.received = False
        self.setSend = True
      
       
        self.clientSocket = socket(AF_INET, SOCK_DGRAM)
       
        print("MEIN PORT:" , (self.meinPort))
        self.clientSocket.bind((("127.0.0.1"), int(self.meinPort)))
       
            
        self.clientSocketSenden = socket(AF_INET, SOCK_DGRAM)
        
        self.chatEmpfangenThread = threading.Thread(target = self.chatEmpfangen)
        self.chatEmpfangenThread.start()
        
        self.threadsendBestaetigung = threading.Thread(target = self.sendBestaetigung)
        self.threadsendBestaetigung.start()
    
    def sendBestaetigung(self):
        ''' Thread der bei eingehender nachricht eine Sende Bestaetigung zurueck sendet'''
        while True:
            if  self.threadStop:
                break
            if self.received:
                    self.clientSocketSenden.sendto(self.ok, (str(self.udpPort), int(self.udpIP)))
                    self.received = False
                    #print("SENDING OK")
           
       
    def chatEmpfangen(self):
        ''' Thread der eingehende nachrichten empfaengt'''
        while(True):
            
            modifiedMessage, data = self.clientSocket.recvfrom(2048)
            print("modifiedMessage: ", modifiedMessage)
            if modifiedMessage.decode() == 'quit':
                print("sende quit1")
                self.clientSocketSenden.sendto(("quit1").encode(), (str(self.udpPort), int(self.udpIP)))
                self.clientSocketSenden.close()
                self.clientSocket.close()
                break
            if modifiedMessage.decode() == 'quit1':
                print("sende quit1 empfangen")
                self.clientSocketSenden.close()
                self.clientSocket.close()
                break
            
                
            if(modifiedMessage == self.ok):
                self.setSend = True
                #print("RECEIVED OK")   
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
        
    def loadChat(self, key):
        if key in self.chatListe:
            dispatcher.send(signal="refillChat", sender=dispatcher.Any, liste = self.chatListe[key])
        else:
            self.chatListe[key] = []
            dispatcher.send(signal="refillChat", sender=dispatcher.Any, liste = self.chatListe[key])
              

                
    def nachrichtZuChatListe(self, key,  message, sender):
        """Fügt Nachrichten zur Chatverwaltung hinzu. Für jeden Chatpartner wird eine Liste erstelllt"""
        if key in self.chatListe:
            self.chatListe[key].append(sender + " : " + message)
        
        print(self.chatListe[key])
        dispatcher.send(signal="neueNachricht", sender = dispatcher.Any, liste = self.chatListe[key])
    
    def endUdpConnection(self):
        """Beendet UDP Connection mit Chatpartner """
       
        print("threads beenden")
        self.chatten = False
        self.clientSocketSenden.sendto(("quit").encode(), (str(self.udpPort), int(self.udpIP)))
        
  
    
    def sendEndUdpConnection(self):
        
        print("sendEndUdpConnection")
        self.sendText("10 " + self.chatPartner)
        #self.chatten = False
        self.endUdpConnection()
        
        
        
    def processReceived(self):
        """Reagiert auf eingehende Nachricht vom Server"""
        while True:
            
            antwort = self.socket.recv(1024).decode("utf-8")

            
            ''' Chatanfrage bekommen'''
            if antwort.split(" ")[0] == "5":
                

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

                
                ''' erfolgreich ausgelogged'''
            elif antwort.split(" ")[0] == "3":

                if antwort.split(" ")[1] == '200':
                    self.loggedIn = False

                    dispatcher.send(signal = self.ausgeloggt, sender = dispatcher.Any)

                ''' Aktive Nutzerliste '''
            elif antwort.split(" ")[0] == "7":

                if len(antwort.split(" ")) > 1:
                    self.nutzerliste = []
                    self.nutzerliste = antwort.split(" ")[1:-1]
                    dispatcher.send(signal = self.refillNutzer, sender = dispatcher.Any, liste = self.nutzerliste)
                    print(self.nutzerliste)
                
                '''Schliessen'''
            elif antwort.split(" ")[0] == "6":
                #if  self.chatten == True:
                #    self.sendEndUdpConnection()
                    
                self.loggedIn = False
                self.socket.close()

                break
                '''Chat von Chatpartner beendet'''
            elif antwort.split(" ")[0] == "10":
                dispatcher.send(signal=self.chatBeendet, sender = dispatcher.Any)
               # self.chatten = False
            if antwort == None:
                break

        dispatcher.send(signal="readyToQuit", sender=dispatcher.Any)

                
    
def main():   
    c = ClientPy()
    

