package pl.futurecollars.invoicing.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class IdService {

    private final Path idFilePath;
    private final FileService fileService;
    private int nextId = 1;

    public IdService(Path idFilePath, FileService fileService) {
        this.idFilePath = idFilePath;
        this.fileService = fileService;

        try {
            List<String> lines = fileService.readAllLines(idFilePath);
            if (lines.isEmpty()) {
                fileService.writeToFile(idFilePath, "1");
            } else {
                nextId = Integer.parseInt(lines.get(0));
            }
        } catch (IOException exception) {
            throw new RuntimeException("Failed to initialize idFilePath", exception);
        }

    }

    public int getNextId() {
        try {
            fileService.writeToFile(idFilePath, String.valueOf(nextId + 1));
            return nextId++;
        } catch (IOException exception) {
            throw new RuntimeException("Failed to read idFilePath", exception);
        }
    }
}
