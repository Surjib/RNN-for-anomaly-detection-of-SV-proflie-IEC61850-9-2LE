import numpy as np
import pandas as pd
from matplotlib import pyplot as plt
# import tensorflow as tf




def generate_dataframe(path_csv, name):
    print('\nForming dataframe [time, interval]')
    data = pd.read_csv(path_csv, encoding="ISO-8859-1")
    print(data.info())

    data['time'] = pd.to_datetime(data['time'])

    print(data)

    time = pd.DataFrame(data, columns=['time'])

    print(time)

    time['interval'] = time['time'].diff()

    time.loc[0, 'interval'] = time.loc[1, 'interval']

    time['interval'] = time['interval'].dt.total_seconds() * 1000

    print(time)
    time.to_csv(name, index=False)

    return time


def univariate_data(dataset, start_index, end_index, history_size, target_size):
    data = []
    labels = []

    start_index = start_index + history_size
    if end_index is None:
        end_index = len(dataset) - target_size

    for i in range(start_index, end_index):
        indices = range(i - history_size, i)
        # Reshape data from (history_size,) to (history_size, 1)
        data.append(np.reshape(dataset[indices], (history_size, 1)))
        labels.append(dataset[i + target_size])
    return np.array(data), np.array(labels)

def create_time_steps(length):
  return list(range(-length, 0))

def show_plot(plot_data, delta, title):
  labels = ['History', 'True Future', 'Model Prediction']
  marker = ['.-', 'rx', 'go']
  time_steps = create_time_steps(plot_data[0].shape[0])
  if delta:
    future = delta
  else:
    future = 0

  plt.title(title)
  for i, x in enumerate(plot_data):
    if i:
      plt.plot(future, plot_data[i], marker[i], markersize=10,
               label=labels[i])
    else:
      plt.plot(time_steps, plot_data[i].flatten(), marker[i], label=labels[i])
  plt.legend()
  plt.xlim([time_steps[0], (future+5)*2])
  plt.xlabel('Time-Step')
  plt.show()
  return plt

def baseline(history):
  return np.mean(history)

path_normal = "E:/DZ/11sem/AI_Enregy/KP/pythonProject/SVcreateAndParse/src/main/resources/TestRun(ALLDATA_NOFAULT_7200_80perPeriod).csv"
path_switched = "E:/DZ/11sem/AI_Enregy/KP/pythonProject/SVcreateAndParse/src/main/resources/TestRun(ALLDATA_TIMEMESS_NOFAULT_7200_80perPeriod).csv"

# data_clean = generate_dataframe(path_normal, "normal")
df = generate_dataframe(path_switched, "fault")

print("done")

TRAIN_SPLIT = int(0.8 * len(df))
# tf.random.set_seed(13)

uni_data = df['interval']
uni_data.index = df['time']
uni_data.head()

# стандартизация данных
uni_train_mean = uni_data[:TRAIN_SPLIT].mean()
uni_train_std = uni_data[:TRAIN_SPLIT].std()

uni_data = (uni_data - uni_train_mean) / uni_train_std

univariate_past_history = 32
univariate_future_target = 12

x_train_uni, y_train_uni = univariate_data(uni_data, 0, TRAIN_SPLIT,
                                           univariate_past_history,
                                           univariate_future_target)
x_val_uni, y_val_uni = univariate_data(uni_data, TRAIN_SPLIT, None,
                                       univariate_past_history,
                                       univariate_future_target)

print ('Single window of past history')
print (x_train_uni[0])
print ('\n Target interval to predict (scaled)')
print (y_train_uni[0])


show_plot([x_train_uni[0], y_train_uni[0]], 0, 'Sample Example')

# решение без РНС
show_plot([x_train_uni[0], y_train_uni[0], baseline(x_train_uni[0])], 0,
           'Baseline Prediction Example')

# print(baseline(x_train_uni[0]) * uni_train_std + uni_train_mean)

BATCH_SIZE = 256
BUFFER_SIZE = 2000

# train_univariate = tf.data.Dataset.from_tensor_slices((x_train_uni, y_train_uni))
# train_univariate = train_univariate.cache().batch(BATCH_SIZE).repeat()
#
# val_univariate = tf.data.Dataset.from_tensor_slices((x_val_uni, y_val_uni))
# val_univariate = val_univariate.batch(BATCH_SIZE).repeat()
#
#
# simple_lstm_model = tf.keras.models.Sequential([
#     tf.keras.layers.LSTM(8, input_shape=x_train_uni.shape[-2:]),
#     tf.keras.layers.Dense(1)
# ])
#
# simple_lstm_model.compile(optimizer='adam', loss='mae')
#
# print(x_train_uni.shape)
#
#
# for x, y in val_univariate.take(1):
#     print(simple_lstm_model.predict(x).shape)
#
# EVALUATION_INTERVAL = 200
# EPOCHS = 10
#
# simple_lstm_model.fit(train_univariate, epochs=EPOCHS,
#                       steps_per_epoch=EVALUATION_INTERVAL,
#                       validation_data=val_univariate, validation_steps=50)
#
# for x, y in val_univariate.take(3):
#   plot = show_plot([x[0].numpy(), y[0].numpy(),
#                     simple_lstm_model.predict(x)[0]], 0, 'Simple LSTM model')
#   plot.show()