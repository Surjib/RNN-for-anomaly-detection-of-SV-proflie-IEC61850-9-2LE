# Project Overview
- Name: RNN-for-anomaly-detection-of-SV-proflie-IEC61850-9-2LE
- Description: Java application designed for creating and capturing Sampled Values (IEC61850) and python application for anomaly detection using RNN.
- Technologies Used: Java, Pcap4j, Python, Sklearn, Keras, Pandas.
# Dependencies:
- JavaFX Controls (version 17.0.2)
- Pcap4j (2.0.0-alpha.6)
- WinPcap (4.1.3)
- Scikit-learn (1.5.2)
- Pandas (2.2.3)
- Keras (3.5.0)
- Matplotlib (3.9.2)
- PSCAD v46

Structure
1) Test grid for normal and abnormal powergrid simulation: results get written in comtrade files
![alt text](https://github.com/Surjib/RNN-for-anomaly-detection-of-SV-proflie-IEC61850-9-2LE/blob/master/SVcreateAndParse/src/main/resources/test_grid.png)
2) ComtradeParser.java: The entry point of the application; creates Sampled Values and an SV subscriber that creates .csv file suitable for RNN training.
3) main.py: RNN training and anomaly detection

# Getting Started
- Clone the repository: git clone
- Build the project: Use Maven to build the project.
- Run the application: Execute the main class ComtradeParser.
- Start the PSCAD simulation
- Run the main.py

  ![alt text](https://github.com/Surjib/RNN-for-anomaly-detection-of-SV-proflie-IEC61850-9-2LE/blob/master/SVcreateAndParse/src/main/resources/pred_train_comp.png)
  ![alt text](https://github.com/Surjib/RNN-for-anomaly-detection-of-SV-proflie-IEC61850-9-2LE/blob/master/SVcreateAndParse/src/main/resources/prediction_newdata.png)
  ![alt text](https://github.com/Surjib/RNN-for-anomaly-detection-of-SV-proflie-IEC61850-9-2LE/blob/master/SVcreateAndParse/src/main/resources/mse.png)
  ![alt text](https://github.com/Surjib/RNN-for-anomaly-detection-of-SV-proflie-IEC61850-9-2LE/blob/master/SVcreateAndParse/src/main/resources/accuracy.png)
