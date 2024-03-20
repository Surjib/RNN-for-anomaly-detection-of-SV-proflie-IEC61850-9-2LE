import numpy as np
import pandas as pd
from keras import Sequential
from keras.src.layers import LSTM, Dense
from sklearn.preprocessing import StandardScaler, MinMaxScaler


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

path_normal = "E:/DZ/11sem/AI_Enregy/KP/pythonProject/SVcreateAndParse/src/main/resources/TestRun(ALLDATA_FAULT_7200_80perPeriod).csv"
path_switched = "E:/DZ/11sem/AI_Enregy/KP/pythonProject/SVcreateAndParse/src/main/resources/TestRun(ALLDATA_TIMEMESS_NOFAULT_7200_80perPeriod).csv"

data_clean = generate_dataframe(path_normal, "normal")
data_messed = generate_dataframe(path_switched, "fault")

print("done")

windows = data_clean['interval'].rolling(window=10)
print(windows)

mean_values = windows.mean()
std_values = windows.std()

data_clean['mean_interval'] = mean_values
data_clean['std_interval'] = std_values
data_clean.dropna(inplace=True)


print(data_clean)

windows = data_messed['interval'].rolling(window=5)
print(windows)

mean_values = windows.mean()
std_values = windows.std()

# вычисляем среднее значение и стандартное отклонение для каждого окна
data_messed['mean_interval'] = mean_values
data_messed['std_interval'] = std_values
data_messed.dropna(inplace=True)

print(data_messed)

mean_threshold = 0.5


# определяем метки для каждого окна данных
data_clean['label'] = np.where((data_clean['mean_interval'] <= mean_threshold),
                       0, 1)

data_messed['label'] = np.where((data_messed['mean_interval'] <= mean_threshold),
                       0, 1)

# print(data_clean.head())


# определяем размер окна
window_size = 10

num_features = 1


# разбиваем DataFrame на окна фиксированного размера
X = []
y = []
time = []


# проходимся по всем измерениям, кроме последних window_size
for i in range(len(data_messed) - window_size):
    # добавляем временную метку
    time.append(data_messed.iloc[i]['time'])

    # добавляем входные данные
    X.append(data_messed.iloc[i:i+window_size]['interval'].values.reshape(window_size, num_features))

    # добавляем выходные данные
    y.append(data_messed.iloc[i+window_size-1]['label'])


# преобразуем списки в массивы NumPy
X = np.array(X)
y = np.array(y)
time = np.array(time)

# определяем архитектуру сети
model = Sequential()
model.add(LSTM(50, input_shape=(window_size, num_features)))
model.add(Dense(1, activation='sigmoid'))

model.compile(loss='binary_crossentropy', optimizer='adam', metrics=['accuracy'])

# разбиваем данные на обучающую и тестовую выборки
train_size = int(0.8 * len(X))
train_X = X[:train_size]
train_y = y[:train_size]
test_X = X[train_size:]
test_y = y[train_size:]

# scaler = StandardScaler()
# scaler.fit(train_X)
# train_X = scaler.transform(train_X)
# test_X = scaler.transform(train_X)

# # разделяем массив X на признаки и временные метки
# X_features = X[:, :, :-1]
# X_time = X[:, :, -1]
#
# # масштабируем признаки
# scaler = StandardScaler()
# X_features_scaled = scaler.fit_transform(X_features.reshape(-1, X_features.shape[-1])).reshape(X_features.shape)
#
# # объединяем масштабированные признаки с временными метками
# X_scaled = np.concatenate((X_features_scaled, X_time[:, :, None]), axis=-1)

# обучаем модель
model.fit(train_X, train_y, epochs=100, batch_size=32, validation_data=(test_X, test_y))


# оцениваем модель на тестовой выборке
loss, accuracy = model.evaluate(test_X, test_y)
print('Точность на тестовой выборке:', accuracy, loss)

# предсказываем значения для тестовых данных
y_pred = model.predict(test_X)

# X_test_scaled = scaler.transform(test_X[:, :-1].reshape(-1, test_X.shape[-1])).reshape(test_X.shape)
# y_pred = model.predict(X_test_scaled)

# преобразуем предсказанные значения в бинарные метки
y_pred_binary = [1 if y >= 0.5 else 0 for y in y_pred]

# сравниваем предсказанные значения с фактическими значениями
accuracy = np.mean(y_pred_binary == test_y)

print('Точность модели на тестовых данных:', accuracy)



# X, y = [], []
# for i in range(3, len(data_clean)):
#     X.append(data_clean[i-3:i, 0])
#     y.append(data_clean[i, 0])
# X, y = np.array(X), np.array(y)

# print('\n1--------------------------')
# data = pd.read_csv(path_fault, encoding ="ISO-8859-1")
# print(data.info())
# # print(data.head())
# # data.dropna(inplace=True, axis=0)
#
# # target va
# # data['fault'] = np.where(
# #     (data['Ia'] > 800) | (data['Ib'] > 800) | (data['Ic'] > 800),
# #     1, 0)
# # print(data)
#
# # data['Uc_quality'] = '0000000000000'
#
# data['time'] = pd.to_datetime(data['time'])
#
# print(data)
#
# time = pd.DataFrame(data, columns=['time'])
#
# print(time)
#
# time['interval'] = time['time'].diff()
#
# time.loc[0, 'interval'] = time.loc[1, 'interval']
#
# time['interval'] = time['interval'].dt.total_seconds() * 1000
#
#
# print(time)
# # time.to_csv('test.csv', index=False)
#
# # null_vals = ((data_frame == " ?").sum().loc[lambda x: x > 0])


