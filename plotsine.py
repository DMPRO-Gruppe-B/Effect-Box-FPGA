from numpy import *
import matplotlib.pyplot as plt
from time import time

with open("sine.txt", "r") as f:
    nums = [float(n) for n in f.readlines()]
    
with open("sound.txt", "r") as g:
    orig = [float(n) for n in g.readlines()]


x = linspace(0, len(nums), 1001)

plt.plot(orig)
plt.plot(nums)
# plt.plot(x, 1000 * 32400/2**15 * sin(x * pi/(180*18) ))

plt.show()
# plt.savefig(f"sine.png")
