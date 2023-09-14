import nibabel as nib
import numpy as np
import sys

patientName = sys.argv[1]
resPath = "../step1-segmentation-res/{}/".format(patientName)

lab = nib.load(resPath + '{}_4D.nii'.format(patientName))
label = np.array(lab.dataobj)

class1 = np.where(label == 1, 1, 0)
class2 = np.where(label == 2, 2, 0)
class3 = np.where(label == 3, 3, 0)

ni_img1 = nib.Nifti1Image(class1, lab.affine, lab.header)
ni_img2 = nib.Nifti1Image(class2, lab.affine, lab.header)
ni_img3 = nib.Nifti1Image(class3, lab.affine, lab.header)

nib.save(ni_img1, resPath + "{}_class1.nii".format(patientName))
nib.save(ni_img2, resPath + "{}_class2.nii".format(patientName))
nib.save(ni_img3, resPath + "{}_class3.nii".format(patientName))