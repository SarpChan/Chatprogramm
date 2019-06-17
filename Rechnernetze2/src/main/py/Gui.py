from Rechnernetze2.src.main.py.appjar import gui


def press(button):
    if button == "Cancel":
        app.stop()
    else:
        usr = app.getEntry("Username")
        pwd = app.getEntry("Password")


app = gui("Chatsystem","400x600" )
app.setBg("grey")

app.addLabelEntry("Username")
app.addLabelEntry("Password")
app.addButtons(["Submit", "Cancel"], press)

