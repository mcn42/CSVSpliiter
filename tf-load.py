import numpy as np
import pandas as pd
import tensorflow as tf
from tensorflow.python.data import Dataset
tf.logging.set_verbosity(tf.logging.ERROR)

pd.options.display.max_rows = 10
pd.options.display.float_format = '{:.1f}'.format
my_dataframe = pd.read_csv("file:///Users/michaeln/Downloads/output_2/xethru_presence_movinglist_20180424_093504_1.csv", sep=",", header=None)
data = my_dataframe[5]

frame = np.fromstring(data[0].translate(None,']['),sep=',');

sz = np.shape(data)[0]
print sz
for i in range(1,sz):
	arr = np.fromstring(data[i].translate(None,']['),sep=',')
	frame = np.vstack((frame,arr))

print frame
df = pd.DataFrame(data=frame)
print df.describe

