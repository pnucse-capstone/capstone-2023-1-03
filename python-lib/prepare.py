from __future__ import division
import numpy as np
import os, shutil, sys

sys.path.insert(0,'step1-segmentation/estimators/')
from config import *
from test_utils import *

sys.path.insert(0,'../models/')
from network import *
from network_ops import *

model = FCMultiScaleResidualDenseNet(inputs,
                                     targets,
                                     weight_maps,
                                     batch_class_weights,
                                     num_class=conf.num_class,
                                     n_pool = 3,
                                     n_feat_first_layer = [16, 16, 16],
                                     growth_rate = 16,
                                     n_layers_per_block = [2, 3, 4, 5, 4, 3, 2],
                                     weight_decay = 5e-6,
                                     dropout_rate = 0.2,
                                     optimizer = AdamOptimizer(conf.learning_rate),
                                     metrics_list = ['sW_CE_loss', 'mBW_Dice_loss', 'L2_loss', 'Total_loss', 'avgDice_score',
                                                     'Dice_class_1', 'Dice_class_2', 'Dice_class_3'],
                                     metrics_to_optimize_on = ['Total_loss']
                                     )

# initialise the estimator with the net
print('Preparing the Tester..')
tester = Tester(model, conf, model_path)