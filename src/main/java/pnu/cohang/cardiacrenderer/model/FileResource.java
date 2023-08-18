package pnu.cohang.cardiacrenderer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.io.InputStreamResource;

import java.io.File;

@AllArgsConstructor
@Getter
public class FileResource {
    private File file;
    private InputStreamResource inputStreamResource;
}
