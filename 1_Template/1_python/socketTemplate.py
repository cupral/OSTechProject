from __future__ import print_function
import socket
import time
from contextlib import closing

class MyClass(GeneratedClass):
    def __init__(self):
        GeneratedClass.__init__(self)

    def onLoad(self):
        #put initialization code here
        pass

    def onUnload(self):
        #put clean-up code here
        pass

    def onInput_onStart(self):
        host = '169.254.98.176'
        port = 4007
        sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        with closing(sock):
            message = 'test'.encode('utf-8')
            sock.sendto(message, (host, port))
            self.onStopped()
            #activate the output of the box
            pass

    def onInput_onStop(self):
        self.onUnload() #it is recommended to reuse the clean-up as the box is stopped
        self.onStopped() #activate the output of the box