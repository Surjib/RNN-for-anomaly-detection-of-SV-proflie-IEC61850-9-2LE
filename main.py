import numpy as np
import pandas as pd
from keras import Sequential
from keras.src.layers import LSTM, Dense
from keras.src.regularizers import regularizers
from matplotlib import pyplot as plt
from sklearn.preprocessing import StandardScaler, MinMaxScaler
from sklearn.metrics import accuracy_score, f1_score, mean_squared_error, r2_score, roc_auc_score



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

path_normal = "E:/DZ/11sem/AI_Enregy/KP/pythonProject/SVcreateAndParse/src/main/resources/TestRun(ALLDATA_NOFAULT_7200_80perPeriod).csv"
path_switched = "E:/DZ/11sem/AI_Enregy/KP/pythonProject/SVcreateAndParse/src/main/resources/TestRun(ALLDATA_TIMEMESS_NOFAULT_7200_80perPeriod).csv"

data_clean = generate_dataframe(path_normal, "normal")
data_messed = generate_dataframe(path_switched, "fault")

print("done")

# препподготовка данных
X_messed, y_messed, time_messed, window_size_messed, num_features_messed = prepare_data(data_messed)
X_clean, y_clean, time_clean, window_size_clean, num_features_clean = prepare_data(data_clean)


# определяем архитектуру сети
model = Sequential()
model.add(LSTM(50, input_shape=(window_size_messed, num_features_messed), kernel_regularizer=regularizers.L2(0.01), dropout = 0.2, return_sequences=True))
model.add(LSTM(50, activation='relu'))
model.add(Dense(1, activation='sigmoid'))

model.compile(loss='binary_crossentropy', optimizer='adam', metrics=['accuracy'])

# разбиваем данные на обучающую и тестовую выборки
train_size = int(0.8 * len(X_messed))
train_X = X_messed[:train_size]
train_y = y_messed[:train_size]
test_X = X_messed[train_size:]
test_y = y_messed[train_size:]

# обучаем модель
model.fit(train_X, train_y, epochs=100, batch_size=32, validation_data=(test_X, test_y))


# оцениваем модель на тестовой выборке
loss, accuracy = model.evaluate(test_X, test_y)
print('Точность на тестовой выборке:', accuracy, loss)

# предсказываем значения для тестовых данных
y_pred = model.predict(test_X)



# Создаем график
plt.plot(np.arange(len(y_pred)), y_pred)
plt.title("Проверка модели на тестовой выборке (0-в норме, 1-обнаружена подмена)", fontsize=10)
plt.ylabel('Предсказанное значение', fontsize=8)
plt.show()


# X_test_scaled = scaler.transform(test_X[:, :-1].reshape(-1, test_X.shape[-1])).reshape(test_X.shape)
# y_pred = model.predict(X_test_scaled)
y_pred_binary = [1 if y >= 0.5 else 0 for y in y_pred]

# вычисляем метрики качества
accuracy = accuracy_score(test_y, y_pred_binary)
f1 = f1_score(test_y, y_pred_binary)
rmse = mean_squared_error(test_y, y_pred, squared=False)
r2 = r2_score(test_y, y_pred)
# auc = roc_auc_score(test_y, y_pred)


# сравниваем предсказанные значения с фактическими значениями
accuracy = np.mean(y_pred_binary == test_y)

print('Точность модели на тестовых данных:', accuracy, loss)
print('Точность:', accuracy)
print('F1-мера:', f1)
print('RMSE:', rmse)

plt.plot(np.arange(len(y_pred_binary)), y_pred_binary)
plt.title("Проверка модели на тестовой выборке (0-в норме, 1-обнаружена подмена)", fontsize=10)
plt.ylabel('Предсказанное значение', fontsize=8)
plt.show()

train_size = int(0.8 * len(X_messed))
train_X1 = X_clean[:train_size]
train_y1 = y_clean[:train_size]
test_X1 = X_clean[train_size:]
test_y1 = y_clean[train_size:]

print("s")

y_pred_new = model.predict(test_X1)


plt.plot(np.arange(len(y_pred_new)), y_pred_new)
plt.title("Проверка модели на новой выборке (0-в норме, 1-обнаружена подмена)", fontsize=10)
plt.ylabel('Предсказанное значение', fontsize=8)
plt.show()




# X_test_scaled = scaler.transform(test_X[:, :-1].reshape(-1, test_X.shape[-1])).reshape(test_X.shape)
# y_pred = model.predict(X_test_scaled)

# преобразуем предсказанные значения в бинарные метки
y_pred_new_binary = [1 if y >= 0.5 else 0 for y in y_pred_new]

plt.plot(np.arange(len(y_pred_new_binary)), y_pred_new_binary)
plt.title("Проверка модели на новой выборке (0-в норме, 1-обнаружена подмена)", fontsize=10)
plt.ylabel('Предсказанное значение', fontsize=8)
plt.show()






