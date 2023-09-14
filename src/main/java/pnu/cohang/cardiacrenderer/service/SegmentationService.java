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
            log.info("segmentation ... segmentationPath : {}", segmentationPath);

            Runtime runtime = Runtime.getRuntime();

            Process process = runtime.exec("python " + segmentationPath + " " + patientNumber, null, new File(pythonLibPath + "/step1-segmentation/estimators"));
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;

            while ((line = br.readLine()) != null) {
                log.info(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void segmentationResultSplit(String patientNumber) {
        try {
            String segmentationResSplitPath = pythonLibPath + "/step1-segmentation/seg3class.py";
            log.info("segmentationResultSplit ... segmentationPath : {}", segmentationResSplitPath);

            Runtime runtime = Runtime.getRuntime();

            Process process = runtime.exec("python " + segmentationResSplitPath + " " + patientNumber, null, new File(pythonLibPath + "/step1-segmentation"));
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;

            while ((line = br.readLine()) != null) {
                log.info(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
