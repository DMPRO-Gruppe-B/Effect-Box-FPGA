import pyglet
import playsound
from pyo import *

# def playMusicPyglet():
#     sound = pyglet.media.load('./musicBox.wav', streaming=False)
#     sound.play()

# playMusicPyglet()
# pyglet.app.run()


# def toScreen():
#     print("HEI ")

# def playMusicPlaysound():
#     playsound('/home/mina/dmpro/Effect-Box-FPGA/src/musicBox.wav')

# playMusicPlaysound()



#########################################################################################
s = Server()

s.boot()


s.amp = 0.2

a = Sine()

hr = Harmonizer(a).out()

ch = Chorus(a).out()

sh = FreqShift(a).out()

s.gui(locals())
