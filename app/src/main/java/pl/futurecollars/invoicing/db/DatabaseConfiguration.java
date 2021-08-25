package pl.futurecollars.invoicing.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.futurecollars.invoicing.db.file.FileBasedDatabase;
import pl.futurecollars.invoicing.db.jpa.InvoiceRepository;
import pl.futurecollars.invoicing.db.jpa.JpaDatabase;
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase;
import pl.futurecollars.invoicing.db.sql.SqlDatabase;
import pl.futurecollars.invoicing.service.FileService;
import pl.futurecollars.invoicing.service.IdService;
import pl.futurecollars.invoicing.service.JsonService;

@Slf4j
@Configuration
public class DatabaseConfiguration {

    @Bean
    @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "file")
    public IdService idService(FileService fileService,
                               @Value("${invoicing-system.database.directory}") String databaseDirectory,
                               @Value("${invoicing-system.database.id.file}") String idFile)
            throws IOException {
        Path idFilePath = Files.createTempFile(databaseDirectory, idFile);
        return new IdService(idFilePath, fileService);
    }

    @Bean
    @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "file")
    public Database fileBasedDatabase(IdService idService,
                                      FileService fileService,
                                      JsonService jsonService,
                                      @Value("${invoicing-system.database.directory}") String databaseDirectory,
                                      @Value("${invoicing-system.database.invoices.file}") String invoicesFile)
            throws IOException {
        log.info("File database has been detected");
        Path databaseFilePath = Files.createTempFile(databaseDirectory, invoicesFile);
        return new FileBasedDatabase(databaseFilePath, idService, fileService, jsonService);
    }

    @Bean
    @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "memory")
    public Database inMemoryDatabase() {
        log.info("File database has been detected");
        return new InMemoryDatabase();
    }

    @Bean
    @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "sql")
    public Database sqlDatabase(JdbcTemplate jdbcTemplate) {
        log.info("SQL database has been detected");
        return new SqlDatabase(jdbcTemplate);
    }

    @Bean
    @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "jpa")
    public Database jpaDatabase(InvoiceRepository invoiceRepository) {
        log.info("JPA database has been detected");
        return new JpaDatabase(invoiceRepository);
    }
}
