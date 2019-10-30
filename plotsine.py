from numpy import *
import matplotlib.pyplot as plt
from time import time

with open("sine.txt", "r") as f:
    nums = [float(n) for n in f.readlines()]
    

plt.plot(nums)
plt.show()
# plt.savefig(f"sine.png")
