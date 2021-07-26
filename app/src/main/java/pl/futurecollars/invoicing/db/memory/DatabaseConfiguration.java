package pl.futurecollars.invoicing.db.memory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.config.PathConfiguration;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.service.FileService;
import pl.futurecollars.invoicing.service.IdService;
import pl.futurecollars.invoicing.service.JsonService;

@Configuration
public class DatabaseConfiguration {

    @Bean
    public IdService idService(FileService fileService) throws IOException {
        Path idFilePath = Files.createTempFile(PathConfiguration.DATABASE_PATH, PathConfiguration.ID_PATH);
        return new IdService(idFilePath, fileService);
    }

    @Bean
    public Database fileBasedDatabase(IdService idService, FileService fileService, JsonService jsonService) throws IOException {
        Path databaseFilePath = Files.createTempFile(PathConfiguration.DATABASE_PATH, PathConfiguration.INVOICES_PATH);
        return new FileBasedDatabase(databaseFilePath, idService, fileService, jsonService);
    }
}
