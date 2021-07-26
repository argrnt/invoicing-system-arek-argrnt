package pl.futurecollars.invoicing.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FileService {

    public List<String> readAllLines(Path path) throws IOException {
        return Files.readAllLines(path);
    }

    public void writeToFile(Path path, String line) throws IOException {
        Files.write(path, line.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
    }

    public void appendLineToFile(Path path, String line) throws IOException {
        Files.write(path, (line + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
    }

    public void writeLinesToFile(Path path, List<String> lines) throws IOException {
        Files.write(path, lines, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
