import numpy as np
import pandas as pd
from keras import Sequential
from keras.src.layers import LSTM, Dense
from keras.src.regularizers import regularizers
from matplotlib import pyplot as plt
from sklearn.preprocessing import StandardScaler, MinMaxScaler
from sklearn.metrics import accuracy_score, f1_score, mean_squared_error, r2_score, roc_auc_score
import tensorflow as tf


# print(file_name, path)
def generate_dataframe(path_csv, name):

    print('\nForming dataframe [time, interval]')
    data = pd.read_csv(path_csv, encoding ="ISO-8859-1")
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


def prepare_data(data):
    windows = data['interval'].rolling(window=5)
    print(windows)

    mean_values = windows.mean()
    std_values = windows.std()

    # вычисляем среднее значение и стандартное отклонение для каждого окна
    data['mean_interval'] = mean_values
    data['std_interval'] = std_values
    data.dropna(inplace=True)

    print(data)

    mean_threshold = 0.5

    # определяем метки для каждого окна данных
    data['label'] = np.where((data['mean_interval'] <= mean_threshold),
                                   0, 1)

    # print(data.head())

    # определяем размер окна
    window_size = 32

    num_features = 1

    # разбиваем DataFrame на окна фиксированного размера
    X = []
    y = []
    time = []

    # проходимся по всем измерениям, кроме последних window_size
    for i in range(len(data) - window_size):
        # добавляем временную метку
        time.append(data.iloc[i]['time'])

        # добавляем входные данные
        X.append(data.iloc[i:i + window_size]['interval'].values.reshape(window_size, num_features))

        # добавляем выходные данные
        y.append(data.iloc[i + window_size - 1]['label'])

    # преобразуем списки в массивы NumPy
    X_done = np.array(X)
    y_done = np.array(y)
    time_done = np.array(time)
    return X_done, y_done, time_done, window_size, num_features

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

path_normal = "E:/DZ/11sem/AI_Enregy/KP/pythonProject/SVcreateAndParse/src/main/resources/TestRun(ALLDATA_NOFAULT_7200_80perPeriod).csv"
path_switched = "E:/DZ/11sem/AI_Enregy/KP/pythonProject/SVcreateAndParse/src/main/resources/TestRun(randInt).csv"



data_clean = generate_dataframe(path_normal, "normal")
data_messed = generate_dataframe(path_switched, "fault")

print("done")

interval_normal = np.array(data_clean['interval'].values)
interval_messed = np.array(data_messed['interval'].values)

# Создаем график исходных данных
plt.plot(np.arange(len(interval_normal)), interval_normal)
plt.title("Интеравал между получением SV пакетами", fontsize=10)
plt.ylabel('Интервал, ms', fontsize=8)
plt.xlabel('№ packet ', fontsize=8)
plt.show()

plt.plot(np.arange(len(interval_messed)), interval_messed)
plt.title("Интеравал между получением SV пакетами с подменой", fontsize=10)
plt.ylabel('Интервал, ms', fontsize=8)
plt.xlabel('№ packet ', fontsize=8)
plt.show()

# препподготовка данных
# X_messed, y_messed, time_messed, window_size_messed, num_features_messed = prepare_data(data_messed)
# X_clean, y_clean, time_clean, window_size_clean, num_features_clean = prepare_data(data_clean)
TRAIN_SPLIT = int(0.8 * len(data_messed))

# стандартизация данных
uni_train_mean = data_messed[:TRAIN_SPLIT].mean()
uni_train_std = data_messed[:TRAIN_SPLIT].std()

uni_data = (data_messed - uni_train_mean) / uni_train_std

univariate_past_history = 32
univariate_future_target = 0

x_train_uni, y_train_uni = univariate_data(uni_data, 0, TRAIN_SPLIT,
                                           univariate_past_history,
                                           univariate_future_target)
x_val_uni, y_val_uni = univariate_data(uni_data, TRAIN_SPLIT, None,
                                       univariate_past_history,
                                       univariate_future_target)

# определяем архитектуру сети
model = Sequential()
model.add(LSTM(50, input_shape=(x_train_uni.shape[-2:]), kernel_regularizer=regularizers.L2(0.01), dropout = 0.2, return_sequences=True) )
# model.add(LSTM(50, activation='relu'))
model.add(Dense(1, activation='sigmoid'))
model.summary()

model.compile(loss='binary_crossentropy', optimizer='adam', metrics=['accuracy'])

model.summary
# разбиваем данные на обучающую и тестовую выборки
# train_size = int(0.8 * len(X_messed))
# train_X = X_messed[:train_size]
# train_y = y_messed[:train_size]
# test_X = X_messed[train_size:]
# test_y = y_messed[train_size:]

BATCH_SIZE = 256
BUFFER_SIZE = 2000

train_univariate = tf.data.Dataset.from_tensor_slices((x_train_uni, y_train_uni))
train_univariate = train_univariate.cache().batch(BATCH_SIZE).repeat()

val_univariate = tf.data.Dataset.from_tensor_slices((x_val_uni, y_val_uni))
val_univariate = val_univariate.batch(BATCH_SIZE).repeat()

epochs = 100
epoch_range = np.arange(1,epochs+1,1)
# обучаем модель
history = model.fit(train_univariate, epochs=epochs, batch_size=32, validation_data=val_univariate)

accuracy_list = []
accuracy_list.append(history.history['accuracy'])


print("__________ТОЧНОСТЬ_________", accuracy_list)

# оцениваем модель на тестовой выборке
loss, accuracy = model.evaluate()
print('Точность на тестовой выборке:', accuracy, loss)

# # предсказываем значения для тестовых данных
# y_pred = model.predict(test_X)
#
# #точность
# plt.plot(epoch_range, accuracy_list[0])
# plt.title("Accuracy", fontsize=10)
# plt.ylabel('Accuracy rate', fontsize=8)
# plt.xlabel('epoch', fontsize=8)
# plt.show()
#
#
#
# # Создаем график
# plt.plot(np.arange(len(y_pred)), y_pred)
# plt.title("Проверка модели на тестовой выборке (0-в норме, 1-обнаружена подмена)", fontsize=10)
# plt.ylabel('Предсказанное значение', fontsize=8)
# plt.show()
#
# plt.subplot(1, 2, 1)
# plt.plot(np.arange(len(y_pred)), y_pred)
# plt.title("predict", fontsize=10)
# plt.ylabel('Предсказанное значение', fontsize=8)
#
# plt.subplot(1, 2, 2)
# plt.plot(np.arange(len(test_y)), test_y)
# plt.title("test", fontsize=10)
# plt.ylabel('Предсказанное значение', fontsize=8)
#
# plt.show()
#
#
#
# # X_test_scaled = scaler.transform(test_X[:, :-1].reshape(-1, test_X.shape[-1])).reshape(test_X.shape)
# # y_pred = model.predict(X_test_scaled)
# y_pred_binary = [1 if y >= 0.5 else 0 for y in y_pred]
#
# # вычисляем метрики качества
# accuracy = accuracy_score(test_y, y_pred_binary)
# f1 = f1_score(test_y, y_pred_binary)
# rmse = mean_squared_error(test_y, y_pred, squared=False)
# r2 = r2_score(test_y, y_pred)
# # auc = roc_auc_score(test_y, y_pred)
#
#
# # сравниваем предсказанные значения с фактическими значениями
# accuracy = np.mean(y_pred_binary == test_y)
#
# print('Точность модели на тестовых данных:', accuracy, loss)
# print('Точность:', accuracy)
# print('F1-мера:', f1)
# print('RMSE:', rmse)
#
# plt.plot(np.arange(len(y_pred_binary)), y_pred_binary)
# plt.title("Проверка модели на тестовой выборке (0-в норме, 1-обнаружена подмена)", fontsize=10)
# plt.ylabel('Предсказанное значение', fontsize=8)
# plt.show()
#
# train_size = int(0.8 * len(X_messed))
# train_X_new = X_clean[:train_size]
# train_y_new = y_clean[:train_size]
# test_X_new = X_clean[train_size:]
# test_y_new = y_clean[train_size:]
#
# print("s")
#
# y_pred_new = model.predict(test_X_new)
#
#
# plt.plot(np.arange(len(y_pred_new)), y_pred_new)
# plt.title("Проверка модели на новой выборке (0-в норме, 1-обнаружена подмена)", fontsize=10)
# plt.ylabel('Предсказанное значение', fontsize=8)
# plt.show()
#
#
#
#
# # X_test_scaled = scaler.transform(test_X[:, :-1].reshape(-1, test_X.shape[-1])).reshape(test_X.shape)
# # y_pred = model.predict(X_test_scaled)
#
# # преобразуем предсказанные значения в бинарные метки
# y_pred_new_binary = [1 if y >= 0.5 else 0 for y in y_pred_new]
#
# plt.plot(np.arange(len(y_pred_new_binary)), y_pred_new_binary)
# plt.title("Проверка модели на новой выборке (0-в норме, 1-обнаружена подмена)", fontsize=10)
# plt.ylabel('Предсказанное значение', fontsize=8)
# plt.show()
#
# # предсказываем значения для тестовых данных
# y_pred_train = model.predict(train_X)
#
#
#
# # # Создаем график
# # plt.plot(np.arange(len(y_pred)), y_pred)
# # plt.title("Проверка модели на тестовой выборке (0-в норме, 1-обнаружена подмена)", fontsize=10)
# # plt.ylabel('Предсказанное значение', fontsize=8)
# # plt.show()
#
# plt.subplot(1, 2, 1)
# plt.plot(np.arange(len(y_pred_train)), y_pred_train)
# plt.title("predict_on_train", fontsize=10)
# plt.ylabel('Предсказанное значение', fontsize=8)
#
# plt.subplot(1, 2, 2)
# plt.plot(np.arange(len(train_y)), train_y)
# plt.title("train", fontsize=10)
# plt.ylabel('Предсказанное значение', fontsize=8)
#
# plt.show()
#
#
#
#
