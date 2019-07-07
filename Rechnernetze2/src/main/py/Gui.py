import sys
from PyQt5.QtWidgets import *
from PyQt5.QtGui import *
from PyQt5.QtCore import *
from pydispatch import dispatcher
from tcp_client import *
#from Chatprogramm.Rechnernetze2.src.main.py.tcp_client import ClientPy, registOKEvent
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
        self.sig = registOKEvent()

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
        self.sig.registokEvent.connect(self.switchToHome)

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


        print(self.stacked.currentWidget())

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



    def akzeptiere(self):
        self.c.answerUdpConnection(True)
    def ablehnen(self):
        self.c.answerUdpConnection(False)

    def textFeldChanged(self):
        #absenden Button aktivieren?
        if len(self.textFeld.toPlainText()) != 0:
            self.send.setDisabled(False)
        else:
            self.send.setDisabled(True)

    def loginChange(self, change):
        # Login und Registrier Buttons aktivieren?
        if len(self.password.text()) != 0 and len(self.username.text()) != 0:
            self.login.setDisabled(False)
            self.register.setDisabled(False)
        else:
            self.login.setDisabled(True)
            self.register.setDisabled(True)

    def absenden(self):
        #Nachricht absenden

        message = self.textFeld.toPlainText()
        self.c.send(message)
        self.textFeld.clear()


    def einloggen(self):
        
        self.c.login(self.username.text(), self.password.text(), "1")



    def registrieren(self):

        self.c.login(self.username.text(), self.password.text(), "0")


    def refillNutzerliste(self, liste):
        #Nutzerliste neu befuellen
        self.nutzerliste.clear()

        self.nutzerliste.addItems(liste)
        

    def refillChat(self, liste):
        print(type(liste))
        self.chatliste.clear()
        self.chatliste.addItems(liste)

    def addToChat(self, text):
        self.chatliste.addItem(text)

    def chatWith(self, item):
        self.c.requestUdpConnection(item.text())

    def showChatAnfrage(self, anfrager):

        self.diaText = "Neue Chatanfrage von" + anfrager

        self.dialog.show()




    def back(self):
        #Zurueck zur letzten Ansicht
        if self.lastView != 0:
            temp = self.lastView
            self.lastView =  self.currentView
            self.currentView =  temp
            self.stacked.setCurrentIndex(self.currentView)
            self.show()



    def switchToHome(self):
        self.lastView = self.currentView
        self.currentView = self.nutzerView
        self.stacked.setCurrentIndex(2)
        self.show()

    def switchToChat(self):
        self.lastView = self.currentView
        self.currentView = self.chatView
        self.stacked.setCurrentIndex(1)
        self.show()

    def switchToLogin(self):
        self.username.setText("")
        self.password.setText("")
        self.lastView = None
        self.currentView = self.loginView
        self.stacked.setCurrentIndex(0)
        self.show()

    def switchToDialog(self, anfrager):
        self.diaText.setText("Neue Anfrage von " + anfrager)
        self.stacked.setCurrentIndex(3)
        self.show


    def abmelden(self):

        print("test")
        self.c.closeConnection()


def main():
    app = QApplication(sys.argv)
    w = Fenster()

    sys.exit(app.exec_())

if __name__== "__main__":
    main()
    


