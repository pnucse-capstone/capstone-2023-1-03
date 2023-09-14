import os, re
import numpy as np
import pandas as pd
import sys
# print sys.path
sys.path.append("../") 
# Custom
from utils_heart import * 

patientName = sys.argv[1]
patient_input_path = "../../cardiac-data"

HEADER = ["Name", "ED[vol(LV)]", "ES[vol(LV)]", "ED[vol(RV)]", "ES[vol(RV)]",
          "ED[mass(MYO)]", "ES[vol(MYO)]", "EF(LV)", "EF(RV)", "ED[vol(LV)/vol(RV)]", "ES[vol(LV)/vol(RV)]", "ED[mass(MYO)/vol(LV)]", "ES[vol(MYO)/vol(LV)]",
          "ES[max(mean(MWT|SA)|LA)]", "ES[stdev(mean(MWT|SA)|LA)]", "ES[mean(stdev(MWT|SA)|LA)]", "ES[stdev(stdev(MWT|SA)|LA)]", 
          "ED[max(mean(MWT|SA)|LA)]", "ED[stdev(mean(MWT|SA)|LA)]", "ED[mean(stdev(MWT|SA)|LA)]", "ED[stdev(stdev(MWT|SA)|LA)]", "GROUP"]

HEADER2 = ["Name", "ED[vol(LV)]", "ED[vol(RV)]", 
           "EF(LV)", "EF(RV)",
           "ED[max(MTH)]", "GROUP"]


def calculate_metrics_from_pred(data_path, pred_name='prediction'):
    pred_files = next(os.walk(data_path))[2]
    print(pred_files)
    res=[]
    res2=[]
    seen_patient = []
    for patient in sorted(pred_files):
        if patientName not in seen_patient:
            seen_patient.append(patientName)
            ed = "%s_ED.nii" %(patientName)
            es = "%s_ES.nii" %(patientName)
            # Load data
            ed_data = nib.load(os.path.join(data_path, ed))
            es_data = nib.load(os.path.join(data_path, es))

            ed_lv, ed_rv, ed_myo = heart_metrics(ed_data.get_data(),
                            ed_data.header.get_zooms())
            es_lv, es_rv, es_myo = heart_metrics(es_data.get_data(),
                            es_data.header.get_zooms())
            ef_lv = ejection_fraction(ed_lv, es_lv)
            ef_rv = ejection_fraction(ed_rv, es_rv)


            myo_properties = myocardial_thickness(os.path.join(data_path, es))
            es_myo_thickness_max_avg = np.amax(myo_properties[0])
            es_myo_thickness_std_avg = np.std(myo_properties[0])
            es_myo_thickness_mean_std = np.mean(myo_properties[1])
            es_myo_thickness_std_std = np.std(myo_properties[1])

            myo_properties = myocardial_thickness(os.path.join(data_path, ed))
            ed_myo_thickness_max_avg = np.amax(myo_properties[0])
            ed_myo_thickness_std_avg = np.std(myo_properties[0])
            ed_myo_thickness_mean_std = np.mean(myo_properties[1])
            ed_myo_thickness_std_std = np.std(myo_properties[1])
            # print (es_myo_thickness_max_avg, es_myo_thickness_std_avg, es_myo_thickness_mean_std, es_myo_thickness_std_std,
            #      ed_myo_thickness_max_avg, ed_myo_thickness_std_avg, ed_myo_thickness_std_std, ed_myo_thickness_std_std)

            patient_info = {}
            info_path = "%s/Info.cfg" %(patientName)
            with open(os.path.join(patient_input_path, info_path)) as f_in:
                for line in f_in:
                    l = line.rstrip().split(": ")
                    patient_info[l[0]] = l[1]

            bsa_val = bsa(float(patient_info['Height']), float(patient_info['Weight']))


            heart_param = {'EDV_LV': ed_lv, 'EDV_RV': ed_rv, 'ESV_LV': es_lv, 'ESV_RV': es_rv,
                   'ED_MYO': ed_myo, 'ES_MYO': es_myo, 'EF_LV': ef_lv, 'EF_RV': ef_rv,
                   'ES_MYO_MAX_AVG_T': es_myo_thickness_max_avg, 'ES_MYO_STD_AVG_T': es_myo_thickness_std_avg, 'ES_MYO_AVG_STD_T': es_myo_thickness_mean_std, 'ES_MYO_STD_STD_T': es_myo_thickness_std_std,
                   'ED_MYO_MAX_AVG_T': ed_myo_thickness_max_avg, 'ED_MYO_STD_AVG_T': ed_myo_thickness_std_avg, 'ED_MYO_AVG_STD_T': ed_myo_thickness_mean_std, 'ED_MYO_STD_STD_T': ed_myo_thickness_std_std,}
            r=[]
            pid = patientName
            r.append(pid)
            r.append(heart_param['EDV_LV'])
            r.append(heart_param['ESV_LV'])
            r.append(heart_param['EDV_RV'])
            r.append(heart_param['ESV_RV'])
            r.append(heart_param['ED_MYO'])
            r.append(heart_param['ES_MYO'])
            r.append(heart_param['EF_LV'])
            r.append(heart_param['EF_RV'])
            r.append(ed_lv/ed_rv)
            r.append(es_lv/es_rv)
            r.append(ed_myo/ed_lv)
            r.append(es_myo/es_lv)
            r.append(heart_param['ES_MYO_MAX_AVG_T'])
            r.append(heart_param['ES_MYO_STD_AVG_T'])
            r.append(heart_param['ES_MYO_AVG_STD_T'])
            r.append(heart_param['ES_MYO_STD_STD_T'])

            r.append(heart_param['ED_MYO_MAX_AVG_T'])
            r.append(heart_param['ED_MYO_STD_AVG_T'])
            r.append(heart_param['ED_MYO_AVG_STD_T'])
            r.append(heart_param['ED_MYO_STD_STD_T'])
            # Apppend Blank for Stage 1 results to be populated
            r.append('')
            res.append(r) 


            r2=[]
            r2.append(pid)
            r2.append(heart_param['EDV_LV'] / bsa_val / 1000)
            r2.append(heart_param['EDV_RV'] / bsa_val / 1000)
            
        
            r2.append(heart_param['EF_LV'])
            r2.append(heart_param['EF_RV'])

            r2.append(heart_param['ED_MYO_MAX_AVG_T'])
            # Apppend Blank for Stage 1 results to be populated
            r2.append('')
            res2.append(r2)

        # break
    df = pd.DataFrame(res, columns=HEADER[:len(HEADER)])
    if not os.path.exists('../step2-feature-extraction-res'):
        os.makedirs('../step2-feature-extraction-res')
    df.to_csv("../step2-feature-extraction-res/{}.csv".format(patientName), index=False)


    df = pd.DataFrame(res2, columns=HEADER2[:len(HEADER2)])

    df.to_csv("../step2-feature-extraction-res/{}.csv".format(patientName + "_to_db"), index=False)


if __name__ == '__main__':
    # Data directories:
    # Path to final test set segmentation results of ACDC 2017 challenge
    # test_prediction_path = '../../ACDC_DataSet/ACDC_17_TestSegmentationResults'
    test_prediction_path = '../step1-segmentation-res/{}'.format(patientName)
    print(test_prediction_path)
    calculate_metrics_from_pred(test_prediction_path, pred_name='minmax_k_16')
