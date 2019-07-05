import sys
from PyQt5.QtWidgets import *
from PyQt5.QtGui import *
from PyQt5.QtCore import *
#import Chatprogramm.Rechnernetze2.src.main.py.tcp_client
#from Chatprogramm.Chatprogramm.Rechnernetze2.src.main.py.tcp_client import UpdatedListeEvent



class Fenster(QWidget):
    def __init__(self):
        super().__init__()

        self.setStyleSheet("""QListWidget{
                            background: gray;
                        }
                        """
                           )
        self.login = QPushButton("Login")
        self.register = QPushButton("Register")
        self.logReg = QHBoxLayout()
        self.userBox = QHBoxLayout()
        self.passBox = QHBoxLayout()
        self.loginView = QWidget()
        self.chatView = QWidget()
        self.nutzerView = QWidget()
        self.lBox = QVBoxLayout()
        self.nBox = QVBoxLayout()
        self.cBox = QVBoxLayout()
        self.currentView = 0
        self.lastView = 0
        self.disposeWidget = QWidget()
        self.stacked = QStackedWidget()
        self.mainlayout = QVBoxLayout()

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

        #### LOGIN FENSTER

        #self.sig.updatedEvent.connect(self.updateListView)

        self.login.move(120, 400)
        self.login.setToolTip('this is my Button')
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
        self.nutzerTop.addStretch()
        self.nutzerTop.addWidget(self.nutzerBack)
        self.nutzerTop.addWidget(self.nutzerAbmelden)
        self.nutzerTop.addStretch()

        self.nBox.addLayout(self.nutzerTop)
        self.nBox.addWidget(self.nutzerliste)

        self.nutzerView.setLayout(self.nBox)

        self.nutzerliste.setSelectionMode(QAbstractItemView.SingleSelection)
        #self.nutzerliste.itemSelectionChanged(self.chatView)

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
        #TODO Nachricht an Client senden
        message = self.textFeld.toPlainText()
        #TODO message objekt erstellen mit LocalDateTime, sender, text
        self.addToChat(message)


    def einloggen(self):
        #Beim Server einloggen
        #TODO Client einloggen

        self.username.text()
        self.password.text()

        self.switchView("Nutzer")

    def registrieren(self):
        #Beim Server registrieren
        #TODO Clien registrieren
        self.username.text()
        self.password.text()
        self.switchView("Chat")

    def refillNutzerliste(self, liste):
        #Nutzerliste neu befuellen
        self.nutzerliste.clear()
        self.nutzerliste.addItems(liste)

    def refillChat(self, liste):
        self.chatliste.clear()
        self.chatliste.addItems(liste)

    def addToChat(self, text):
        self.chatliste.addItem(text)

    def chatWith(self):
        print("hallo")
        #TODO Client Chatanfrage an chatPartner


    def back(self):
        #Zurueck zur letzten Ansicht
        if self.currentView != 0:
            temp = self.lastView
            self.lastView =  self.currentView
            self.currentView =  temp
            self.stacked.setCurrentIndex(self.currentView)

    def switchView(self, whereTo):
        #Ansicht wechseln
        if whereTo == "Chat":

            self.lastView = self.currentView
            self.currentView = self.chatView
            self.stacked.setCurrentIndex(1)

        elif whereTo == "Nutzer":

            self.lastView = self.currentView
            self.currentView = self.nutzerView
            self.stacked.setCurrentIndex(2)


        elif whereTo == "Login":

            self.lastView = None
            self.currentView = self.loginView
            self.stacked.setCurrentIndex(0)

        self.show()

    def abmelden(self):
        #TODO Client abmelden

        self.switchView("Login")






app = QApplication(sys.argv)


w = Fenster()


sys.exit(app.exec_())


