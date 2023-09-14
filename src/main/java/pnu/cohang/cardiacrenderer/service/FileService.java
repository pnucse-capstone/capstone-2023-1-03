package pnu.cohang.cardiacrenderer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pnu.cohang.cardiacrenderer.model.FileResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@Slf4j
public class FileService {
    @Value("${cardiac.mri.data.path}")
    private String cardiacDataPath;

    @Value("${python.lib.path}")
    private String pythonLibPath;

    public FileResource getNiiOriginFile(String patientNumber) throws FileNotFoundException {
        File downloadFile = new File(cardiacDataPath + "/" + patientNumber + "/" + patientNumber + "_4d.nii.gz");
        InputStreamResource resource = new InputStreamResource(new FileInputStream(downloadFile));

        return new FileResource(downloadFile, resource);
    }

    public FileResource getNiiSegmentationFile(String patientNumber, String classNumber) throws FileNotFoundException {
        String cardiacSegmentationDataPath = pythonLibPath + "/step1-segmentation-res/" + patientNumber + "/" + patientNumber + "_" + classNumber + ".nii";

        log.info("path : {}", cardiacSegmentationDataPath);

        File downloadFile = new File(cardiacSegmentationDataPath);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(downloadFile));

        return new FileResource(downloadFile, resource);
    }

    public FileResource getInfoFile(String patientNumber) throws FileNotFoundException {
        String infoFilePath = cardiacDataPath + "/" + patientNumber + "/" + "Info.cfg";

        log.info("path : {}", infoFilePath);

        File downloadFile = new File(infoFilePath);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(downloadFile));

        return new FileResource(downloadFile, resource);
    }

    public void unzipFile(String fileName) {
        Path sourceZip = Paths.get(cardiacDataPath + "/" + fileName);
        Path targetDir = Paths.get(cardiacDataPath);

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(sourceZip.toFile()))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                boolean isDirectory = zipEntry.getName().endsWith(File.separator);
                Path newPath = zipSlipProtect(zipEntry, targetDir);

                if (isDirectory) {
                    Files.createDirectories(newPath);
                } else {
                    if (newPath.getParent() != null) {
                        if (Files.notExists(newPath.getParent())) {
                            Files.createDirectories(newPath.getParent());
                        }
                    }
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Path zipSlipProtect(ZipEntry zipEntry, Path targetDir) throws IOException {
        Path targetDirResolved = targetDir.resolve(zipEntry.getName());
        Path normalizePath = targetDirResolved.normalize();
        if (!normalizePath.startsWith(targetDir)) {
            throw new IOException("Bad zip entry: " + zipEntry.getName());
        }
        return normalizePath;
    }

    public void uploadFile(MultipartFile file) throws IOException {
        file.transferTo(new File(cardiacDataPath + "/" + file.getOriginalFilename()));
    }
}
