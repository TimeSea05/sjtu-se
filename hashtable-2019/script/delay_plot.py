#!/usr/bin/python3
import matplotlib.pyplot as plt
import numpy as np

linear_delays = []
cuckoo_delays = []

with open("../test/linear_delay.txt", 'r') as linear_delay_file :
    lines = linear_delay_file.readlines()
    for line in lines:
        line = line.strip("\n")
        linear_delays.append(int(line))

with open("../test/cuckoo_delay.txt", "r") as cuckoo_delay_file:
    lines = cuckoo_delay_file.readlines()
    for line in lines:
        line = line.strip("\n")
        cuckoo_delays.append(int(line))

plt.ylabel('delay of insertion(ns)')
plt.xlabel('time')
plt.title('Insertion Delay of Linear Probing Hashtables')
plt.plot(linear_delays)
plt.show()

plt.title('Insertion delay of Cuckoo Hashtables')
plt.plot(cuckoo_delays)
plt.show()

# Your data
linear_probing_get = {'min': 100, 'max': 19200, 'avg': 286.32, 'throughput': 3492595.7}
linear_probing_get_set = {'min': 0, 'max': 14800, 'avg': 143, 'throughput': 7037297.7}
cuckoo_get = {'min': 100, 'max': 47600, 'avg': 308.5, 'throughput': 3241386.0}
cuckoo_get_set = {'min': 0, 'max': 82500, 'avg': 1451.24, 'throughput': 587030.2}

# Collect the data for the bar chart
averages = [linear_probing_get['avg'], linear_probing_get_set['avg'], cuckoo_get['avg'], cuckoo_get_set['avg']]
min_delays = [linear_probing_get['min'], linear_probing_get_set['min'], cuckoo_get['min'], cuckoo_get_set['min']]
max_delays = [linear_probing_get['max'], linear_probing_get_set['max'], cuckoo_get['max'], cuckoo_get_set['max']]

# Calculate the error bars (intervals)
lower_error = np.array(averages) - np.array(min_delays)
upper_error = np.array(max_delays) - np.array(averages)
error_bars = [lower_error, upper_error]

# Create the bar chart
x_labels = ['Linear Probing Get', 'Linear Probing Get-Set', 'Cuckoo Get', 'Cuckoo Get-Set']
x = np.arange(len(x_labels))
width = 0.35

fig, ax = plt.subplots()
rects = ax.bar(x, averages, width, yerr=error_bars, capsize=7, align='center', alpha=0.5, ecolor='black')

# Label the chart
ax.set_ylabel('Delay')
ax.set_title('Comparison of Average Delays')
ax.set_xticks(x)
ax.set_xticklabels(x_labels)
ax.set_yscale('log')
ax.yaxis.grid(True)

# Function to auto-label bars
def autolabel(rects):
    for rect in rects:
        height = rect.get_height()
        ax.annotate('{:.2f}'.format(height),
                    xy=(rect.get_x() + rect.get_width() / 2, height),
                    xytext=(0, 3),
                    textcoords="offset points",
                    ha='center', va='bottom')

autolabel(rects)

# Show the chart
plt.show()

throughputs = [linear_probing_get['throughput'], linear_probing_get_set['throughput'], cuckoo_get['throughput'], cuckoo_get_set['throughput']]
fig, ax = plt.subplots()
rects = ax.bar(x, throughputs, width, capsize=7, align='center', alpha=0.5)
ax.set_ylabel('Throughput')
ax.set_title('Comparison of Throughputs')
ax.set_xticks(x)
ax.set_xticklabels(x_labels)
ax.yaxis.grid(True)
autolabel(rects)
plt.show()