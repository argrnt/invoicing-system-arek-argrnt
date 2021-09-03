package pl.futurecollars.invoicing.db.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.service.FileService;
import pl.futurecollars.invoicing.service.IdService;
import pl.futurecollars.invoicing.service.JsonService;

@Configuration
@ConditionalOnProperty(name = "invoicing-system.database", havingValue = "file")
public class FileDatabaseConfiguration {

    @Bean
    public IdService idService(
            FileService fileService,
            @Value("${invoicing-system.database.directory}") String databaseDirectory,
            @Value("${invoicing-system.database.id.file}") String idFile
    ) throws IOException {
        Path idFilePath = Files.createTempFile(databaseDirectory, idFile);
        return new IdService(idFilePath, fileService);
    }

    @Bean
    public Database<Invoice> invoiceFileBasedDatabase(
            IdService idService,
            FileService fileService,
            JsonService jsonService,
            @Value("${invoicing-system.database.directory}") String databaseDirectory,
            @Value("${invoicing-system.database.invoices.file}") String invoicesFile
    ) throws IOException {
        Path databaseFilePath = Files.createTempFile(databaseDirectory, invoicesFile);
        return new FileBasedDatabase<>(databaseFilePath, idService, fileService, jsonService, Invoice.class);
    }

    @Bean
    public Database<Company> companyFileBasedDatabase(
            IdService idService,
            FileService fileService,
            JsonService jsonService,
            @Value("${invoicing-system.database.directory}") String databaseDirectory,
            @Value("${invoicing-system.database.companies.file}") String companiesFile
    ) throws IOException {
        Path databaseFilePath = Files.createTempFile(databaseDirectory, companiesFile);
        return new FileBasedDatabase<>(databaseFilePath, idService, fileService, jsonService, Company.class);
    }

}
