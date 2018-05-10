import numpy as np
import pandas as pd
import tensorflow as tf
from tensorflow.python.data import Dataset
tf.logging.set_verbosity(tf.logging.ERROR)

pd.options.display.max_rows = 10
pd.options.display.float_format = '{:.1f}'.format
my_dataframe = pd.read_csv("file:///Users/mnilsen/NetBeansProjects/CSVSplitter/output_2/xethru_presence_movinglist_20180424_093504_1.csv", sep=",", header=None)
data = my_dataframe[5]

frame = np.fromstring(data[0].translate(None,']['),sep=',');

sz = np.shape(data)[0]
print sz
for i in range(1,sz):
	arr = np.fromstring(data[i].translate(None,']['),sep=',')
	frame = np.vstack((frame,arr))

df = pd.DataFrame(data=frame)
print df.describe

incol = tf.feature_column.numeric_column(key = 'moving_list', shape = [127,31])
passengers = tf.feature_column.numeric_column(key = 'passengers_present')
bystanders = tf.feature_column.numeric_column(key = 'bystanders_present')
equipment = tf.feature_column.numeric_column(key = 'equipment_operating')



