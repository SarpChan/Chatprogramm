import sys
from PyQt5.QtWidgets import *
from pydispatch import dispatcher

#from tcp_client import *
from Chatprogramm.Rechnernetze2.src.main.py.tcp_client import ClientPy
#from Chatprogramm.Chatprogramm.Rechnernetze2.src.main.py.tcp_client import UpdatedListeEvent


class Fenster(QWidget):
    def __init__(self):
        super().__init__()

        self.setStyleSheet("""QListWidget{
                            background: gray;
                        }
                        """
                           )
        self.c = ClientPy()
        self.login = QPushButton("Login")
        self.register = QPushButton("Register")
        self.logReg = QHBoxLayout()
        self.userBox = QHBoxLayout()
        self.passBox = QHBoxLayout()
        self.loginView = QWidget()
        self.chatView = QWidget()
        self.nutzerView = QWidget()
        self.dialogView = QWidget()
        self.lBox = QVBoxLayout()
        self.nBox = QVBoxLayout()
        self.cBox = QVBoxLayout()
        self.currentView = 0
        self.lastView = 0
        self.disposeWidget = QWidget()
        self.stacked = QStackedWidget()
        self.mainlayout = QVBoxLayout()
        self.diaText = QLabel()
        self.accept = QPushButton("acccept")
        self.refuse = QPushButton("refuse")
        self.accRef = QHBoxLayout()
        self.diaBox = QVBoxLayout()


        self.user = QLabel("username")
        self.username = QLineEdit()

        self.passw = QLabel("password")
        self.password = QLineEdit()

        self.bot = QHBoxLayout()
        self.textFeld = QTextEdit()
        self.send = QPushButton("absenden")

        self.chatTop = QHBoxLayout()
        self.nutzerTop = QHBoxLayout()
        self.chatBack = QPushButton("back")
        self.nutzerBack = QPushButton("back")
        self.chatAbmelden = QPushButton("abmelden")
        self.nutzerAbmelden = QPushButton("abmelden")

        self.nutzerliste = QListWidget()
        self.chatliste = QListWidget()


        self.initMe()

    def initMe(self):

        ### DIALOG

        self.accRef.addStretch()
        self.accRef.addWidget(self.accept)
        self.accRef.addWidget(self.refuse)
        self.accRef.addStretch()

        self.diaBox.addStretch()
        self.diaBox.addWidget(self.diaText)
        self.diaBox.addLayout(self.accRef)
        self.diaBox.addStretch()

        self.accept.clicked.connect(self.akzeptiere)
        self.refuse.clicked.connect(self.ablehnen)

        self.dialogView.setLayout(self.diaBox)

        #### LOGIN FENSTER

        #self.sig.updatedEvent.connect(self.updateListView)

        self.login.move(120, 400)
        self.login.clicked.connect(self.einloggen)
        self.login.setDisabled(True)
        self.register.move(200, 400)
        self.register.clicked.connect(self.registrieren)
        self.register.setDisabled(True)

        self.username.textChanged.connect(self.loginChange)


        self.userBox.addStretch()
        self.userBox.addWidget(self.user)
        self.userBox.addWidget(self.username)
        self.userBox.addStretch()

        self.password.setEchoMode(QLineEdit.Password)
        self.password.textChanged.connect(self.loginChange)

        self.passBox.addStretch()
        self.passBox.addWidget(self.passw)
        self.passBox.addWidget(self.password)
        self.passBox.addStretch()

        self.logReg.addStretch()
        self.logReg.addWidget(self.login)
        self.logReg.addWidget(self.register)
        self.logReg.addStretch()
        
        self.lBox.addStretch()
        self.lBox.addLayout(self.userBox)
        self.lBox.addLayout(self.passBox)
        self.lBox.addLayout(self.logReg)
        self.lBox.addStretch()

        ### LOGIN FENSTER

        self.loginView.setLayout(self.lBox)

        self.stacked.addWidget(self.loginView)
        self.stacked.addWidget(self.chatView)
        self.stacked.addWidget(self.nutzerView)
        self.stacked.addWidget(self.dialogView)

        self.mainlayout.addWidget(self.stacked)
        self.stacked.setCurrentIndex(1)
        self.stacked.setCurrentWidget(self.loginView)
        self.stacked.setVisible(True)
        self.setLayout(self.mainlayout)

        self.setGeometry(0, 0, 400, 600)
        self.setWindowTitle("Chatprogramm")
        self.setFixedSize(400, 600)
        self.show()
        ### Uebergreifend

        self.chatBack.clicked.connect(self.back)
        self.chatAbmelden.clicked.connect(self.abmelden)
        self.chatTop.addStretch()
        self.chatTop.addWidget(self.chatBack)
        self.chatTop.addWidget(self.chatAbmelden)
        self.chatTop.addStretch()

        ### CHAT

        self.textFeld.textChanged.connect(self.textFeldChanged)
        self.send.clicked.connect(self.absenden)
        self.send.setDisabled(True)
        self.bot.addStretch()
        self.bot.addWidget(self.textFeld)
        self.bot.addWidget(self.send)
        self.bot.addStretch()
        self.chatliste.setSelectionMode(QAbstractItemView.NoSelection)

        self.cBox.addLayout(self.chatTop)
        self.cBox.addWidget(self.chatliste)
        self.cBox.addLayout(self.bot)

        self.chatView.setLayout(self.cBox)

        ### NUTZER

        self.nutzerBack.clicked.connect(self.back)
        self.nutzerAbmelden.clicked.connect(self.abmelden)
        self.nutzerTop.addStretch()
        self.nutzerTop.addWidget(self.nutzerBack)
        self.nutzerTop.addWidget(self.nutzerAbmelden)
        self.nutzerTop.addStretch()

        self.nBox.addLayout(self.nutzerTop)
        self.nBox.addWidget(self.nutzerliste)

        self.nutzerView.setLayout(self.nBox)

        self.nutzerliste.setSelectionMode(QAbstractItemView.SingleSelection)
        self.nutzerliste.itemClicked.connect(self.chatWith)
        #self.nutzerliste.itemSelectionChanged(self.chatView)

        dispatcher.connect(self.switchToHome, signal = "logErfolg", sender= dispatcher.Any)
        dispatcher.connect(self.switchToChat, signal = "chatErfolg", sender= dispatcher.Any)
        dispatcher.connect(self.switchToLogin, signal = "logout", sender= dispatcher.Any)
        dispatcher.connect(self.refillNutzerliste, signal = "refillNutzer", sender= dispatcher.Any)
        dispatcher.connect(self.refillChat, signal = "refillChat", sender= dispatcher.Any)
        dispatcher.connect(self.switchToDialog, signal = "neueChatAnfrage", sender= dispatcher.Any)
        dispatcher.connect(self.refillChat, signal = "neueNachricht", sender = dispatcher.Any)
        dispatcher.connect(self.chatBeendet, signal="chatEnde", sender = dispatcher.Any)
        dispatcher.connect(self.onClose, signal="aboutToQuit", sender = dispatcher.Any)

    def onClose(self):
        self.c.schliessen()
        dispatcher.send(signal="readyToQuit" , sender = dispatcher.Any)

    def chatBeendet(self):
        """Handlermethode des ChatBeendet Events. Wird ausgelöst, wenn Chat Partner die Verbindung trennt."""
        self.switchToHome()

    def akzeptiere(self):
        """" Löst im Client das Akzeptieren einer Chatanfrage aus
        """
        self.c.answerUdpConnection(True)
        
        
    def ablehnen(self):
        """" Löst im Client das Ablehnen einer Chatanfrage aus
                """
        self.stacked.setCurrentIndex(self.currentView)
        self.c.answerUdpConnection(False)
        
        

    def textFeldChanged(self):
        """" Methode eines Listeners, der überprüft, ob im Text Feld des Chats etwas steht, was versendet werden kann. Kein Leerstring
                """
        if len(self.textFeld.toPlainText()) != 0:
            self.send.setDisabled(False)
        else:
            self.send.setDisabled(True)

    def loginChange(self, change):
        """"Methode eines Listeners, der darauf achtet, ob sowohl der login-Input
        als auch Passwort-Input gefüllt sind"""
        if len(self.password.text()) != 0 and len(self.username.text()) != 0:
            self.login.setDisabled(False)
            self.register.setDisabled(False)
        else:
            self.login.setDisabled(True)
            self.register.setDisabled(True)

    def absenden(self):
        """Handler Methode des absenden Buttons. Gibt dem Client das Signal eine Nachricht
        an einen Chat Partner zu senden"""

        message = self.textFeld.toPlainText()
        self.c.send(message)
        self.textFeld.clear()


    def einloggen(self):
        """Handler Methode des login Buttons.Gibt dem Client das Signal sich beim Server anzumelden """
        
        self.c.login(self.username.text(), self.password.text(), "1")



    def registrieren(self):
        """Handler Methode des login Buttons.Gibt dem Client das Signal sich beim Server zu registrieren """
        self.c.login(self.username.text(), self.password.text(), "0")


    def refillNutzerliste(self, liste):
        """ Methode eines Listeners auf die Nutzerliste des Clients. Füllt die Nutzerliste in der NutzerView """
        self.nutzerliste.clear()

        self.nutzerliste.addItems(liste)
        

    def refillChat(self, liste):
        """ Methode eines Listeners auf die aktuelle Chatliste des Clients. Füllt den Chatverlauf in der ChatView """

        self.chatliste.clear()
        self.chatliste.addItems(liste)

    def chatWith(self, item):
        """" Sendet Client einen Nutzernamen, mit dem ein Chat begonnen werden soll"""
        self.c.requestUdpConnection(item.text())

    def back(self):
        """ Handler Methode de back Buttons. Ruft die letzte View auf,
        wenn die letzte View nicht der Login oder Chat ist."""
        if self.currentView == 1:
            self.c.sendEndUdpConnection()
            
        if self.lastView != 0 or self.lastView!=1:
            temp = self.lastView
            self.lastView =  self.currentView
            self.currentView =  temp
            self.stacked.setCurrentIndex(self.currentView)
            self.show()



    def switchToHome(self):
        """Setzt die nutzerView als aktuelle Ansicht"""
        self.lastView = self.currentView
        self.currentView = 2
        self.stacked.setCurrentIndex(2)
        self.show()

    def switchToChat(self):
        """Setzt die chatView als aktuelle Ansicht"""
        
        self.lastView = self.currentView
        self.currentView = 1
        self.stacked.setCurrentIndex(1)
        self.show()


    def switchToLogin(self):
        """Setzt die loginView als aktuelle Ansicht"""
        self.username.setText("")
        self.password.setText("")
        self.lastView = None
        self.currentView = 0
        self.stacked.setCurrentIndex(0)
        self.show()

    def switchToDialog(self, anfrager):
        """Setzt die Chat Anfrage als aktuelle Ansicht"""
        self.diaText.setText("Neue Anfrage von " + anfrager)
        self.stacked.setCurrentIndex(3)
        self.show


    def abmelden(self):
        """ Handler Methode des abmelden buttons. Meldet den Client vom Server ab"""

        if self.currentView == 1:
            self.c.sendEndUdpConnection()


        self.c.closeConnection()
def closeFunc():
    sys.exit()
def closeEvent():
    dispatcher.send(signal = "aboutToQuit", sender = dispatcher.Any)

def main():
    app = QApplication(sys.argv)
    app.aboutToQuit.connect(closeEvent)
    dispatcher.connect(closeFunc, signal="readyToQuit", sender = dispatcher.Any)
    w = Fenster()


    sys.exit(app.exec_())

if __name__== "__main__":
    main()
 
    


