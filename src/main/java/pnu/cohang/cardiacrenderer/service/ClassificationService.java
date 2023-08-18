package pnu.cohang.cardiacrenderer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
@Slf4j
public class ClassificationService {
    @Value("${python.lib.path}")
    private String pythonLibPath;

    public void classification(String patientNumber) {
        try {
            String classificationPath = pythonLibPath + "/step3-classification/classification.py";
            log.info("classification ... classificationPath : {}", classificationPath);

            Runtime runtime = Runtime.getRuntime();

            Process process = runtime.exec("python " + classificationPath + " " + patientNumber, null, new File(pythonLibPath + "/step3-classification"));
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
