import numpy as np
import pandas as pd

path_fault = "E:/DZ/11sem/AI_Enregy/KP/pythonProject/SVcreateAndParse/src/main/resources/TestRun(ALLDATA_NOFAULT_7200_80perPeriod).csv"

# print(file_name, path)


print('\n1--------------------------')
data = pd.read_csv(path_fault, encoding ="ISO-8859-1")
print(data)
# data.dropna(inplace=True, axis=0)

# target va
# data['fault'] = np.where(
#     (data['Ia'] > 800) | (data['Ib'] > 800) | (data['Ic'] > 800),
#     1, 0)
# print(data)

data['Uc_quality'] = '0000000000000'


data['time'] = pd.to_datetime(data['time'])


print(data)