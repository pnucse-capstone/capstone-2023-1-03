"""
This code does training, validation and testing of the model for automated cardiac disease diagnosis.
Basically Implementation of first stage of the disease diagnosis model 
"""
from __future__ import print_function
import numpy as np
import os
import subprocess
import matplotlib.pyplot as plt
import itertools
import sys
import pandas as pd
import joblib
from sklearn.tree import DecisionTreeClassifier, export_graphviz
from sklearn.ensemble import (RandomForestClassifier, ExtraTreesClassifier)

import matplotlib.pyplot as plt
from sklearn.metrics import confusion_matrix
import sklearn
from datetime import datetime
import time
from xgboost import XGBClassifier
from sklearn.metrics import accuracy_score
from sklearn.model_selection import cross_val_score, cross_val_predict
from sklearn.linear_model import LogisticRegression
from sklearn.naive_bayes import GaussianNB
from sklearn import svm
from sklearn.ensemble import VotingClassifier
from sklearn.preprocessing import StandardScaler  
from sklearn.neural_network import MLPClassifier
from sklearn.neighbors import KNeighborsClassifier

sys.path.append("../")

patientName = sys.argv[1];

# Disease Mapping
NOR = 'NOR'
# 30 patients with previous myocardial infarction
# (ejection fraction of the left ventricle lower than 40% and several myocardial segments with abnormal contraction) - MINF
MINF = 'MINF'
# 30 patients with dilated cardiomyopathy
# (diastolic left ventricular volume >100 mL/m2 and an ejection fraction of the left ventricle lower than 40%) - DCM
DCM = 'DCM'
# 30 patients with hypertrophic cardiomyopathy
# (left ventricular cardiac mass high than 110 g/m2,
# several myocardial segments with a thickness higher than 15 mm in diastole and a normal ejecetion fraction) - HCM
HCM = 'HCM'
# 30 patients with abnormal right ventricle (volume of the right ventricular
# cavity higher than 110 mL/m2 or ejection fraction of the rigth ventricle lower than 40%) - RV
RV = 'RV'
heart_disease_label_map = {NOR:0, MINF:1,DCM:2,HCM:3, RV:4}

# Path to cardiac features generated from given manually annotated training data
# These features are used for training and validation of model
full_training = './training_data/Cardiac_parameters_training.csv'

train = './training_data/Cardiac_parameters_train.csv'
validation = './training_data/Cardiac_parameters_validation.csv'

# Path to cardiac features generated from segmentations predicted by the segmentation network
test_on_prediction = '../step2-feature-extraction-res/{}.csv'.format(patientName)

# test_on_prediction = './prediction_data/Cardiac_parameters_minmax_k_16.csv'

# Features columns selection
START_COL = 1
END_COL = 21

class_names = [NOR, MINF, DCM, HCM, RV]
class_names_for_cm = [NOR, MINF, DCM, HCM, RV] #'ARV'-> RV로 수정

def load_dataframe(csv_file, shuffle=False):
    """
    Load Patient information from the csv file as a dataframe
    """
    df = pd.read_csv(csv_file)
    if shuffle:
        df = df.sample(frac=1).reset_index(drop=True)
    # patient_data = df.to_dict("records")
    # return patient_data
    return df
    
def CardiacDiagnosisModelTester(clf, final_test_path, name, scaler, save_dir='./', label_available=False, prediction_csv=None):
    """
    This code does the cardiac disease classification (5-classes)
    """
    class_names = [NOR, MINF, DCM, HCM, RV]
    df = load_dataframe(final_test_path)
    features = list(df.columns[np.r_[START_COL:END_COL]])
    X_df = df[features]
    # print (features)
    X_scaled = scaler.transform(X_df)  
    y_pred = clf.predict(X_scaled)
    print ("Writing predictions to file", name)
    target = open(save_dir + '/{}.txt'.format(patientName), 'w')
    classes = {NOR: 0,MINF:0,DCM:0,HCM:0,RV:0}
    for pid, pred in zip(df['Name'], y_pred):
        classes[class_names[pred]] +=1
        line = '{} {}'.format(pid, class_names[pred])
        target.write(line)
        target.write("\n")
    target.close()
    print (classes)
    if label_available:
        y_true,_ = encode_target(df, 'GROUP', heart_disease_label_map)
        accuracy = accuracy_score(y_true['GROUP'], y_pred)
        print("Accuracy: %.2f%%" % (accuracy * 100.0))
    else:
        if prediction_csv:
            df = load_dataframe(test_on_prediction)
            df['GROUP'] = [class_names[pred] for pred in y_pred]
            df.to_csv(prediction_csv,  index=False)

if __name__ == '__main__':
    save_dir ='../step3-classification-res'

    if not os.path.exists(save_dir):
        os.makedirs(save_dir)

    EN_clf = joblib.load('./model.pkl')
    scaler = joblib.load('./scaler.pkl')
    ##################### Automated Caridiac Diagnosis on Final Test data ########################### 
    # No Group/label is available in final test dataset 
    CardiacDiagnosisModelTester(EN_clf, test_on_prediction, name='EnsembleOnFinalTestSet', scaler=scaler,  save_dir=save_dir, label_available=False, prediction_csv=test_on_prediction)

