import matplotlib.pyplot as plt
import sys

colors = ["red", "white", "red", "orange"]



for i, arg in enumerate(sys.argv[1:]):

    data = [float(s) for s in open(arg).readlines()]
    plt.plot(data[:int(len(data) * 0.7)]) #, color=colors[i])
plt.axis("off")
plt.savefig("sound.png", transparent=True)
plt.show()
