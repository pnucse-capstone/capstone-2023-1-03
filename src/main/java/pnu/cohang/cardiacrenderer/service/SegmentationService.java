package pnu.cohang.cardiacrenderer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
@Slf4j
public class SegmentationService {
    @Value("${python.lib.path}")
    private String pythonLibPath;

    public void segmentation(String patientNumber) {
        try {
            String segmentationPath = pythonLibPath + "/step1-segmentation/estimators/segmentation.py";
            log.info("segmentation ... commend : {}", pythonLibPath + "/run_in_conda.sh python " + segmentationPath + " " + patientNumber);

            ProcessBuilder pb = new ProcessBuilder(pythonLibPath + "/run_in_conda.sh", "python", segmentationPath, patientNumber);
            pb.directory(new File(pythonLibPath + "/step1-segmentation/estimators"));
            pb.inheritIO();
            try {
                Process process = pb.start();
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void segmentationResultSplit(String patientNumber) {
        try {
            String segmentationResSplitPath = pythonLibPath + "/step1-segmentation/seg3class.py";
            log.info("segmentationResultSplit ... segmentationPath : {}", segmentationResSplitPath);

            ProcessBuilder pb = new ProcessBuilder(pythonLibPath + "/run_in_conda.sh", "python", segmentationResSplitPath, patientNumber);
            pb.directory(new File(pythonLibPath + "/step1-segmentation"));
            pb.inheritIO();
            try {
                Process process = pb.start();
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
