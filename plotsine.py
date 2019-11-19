import matplotlib.pyplot as plt
import sys

for arg in sys.argv[1:]:
    data = [float(s) for s in open(arg).readlines()]
    plt.plot(data)

plt.show()
