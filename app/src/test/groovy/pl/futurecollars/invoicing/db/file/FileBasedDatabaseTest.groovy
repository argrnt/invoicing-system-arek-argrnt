package pl.futurecollars.invoicing.db.file

import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.db.AbstractDatabaseTest
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.service.FileService
import pl.futurecollars.invoicing.service.IdService
import pl.futurecollars.invoicing.service.JsonService
import java.nio.file.Files
import java.nio.file.Path
import static pl.futurecollars.invoicing.TestHelpers.invoice

class FileBasedDatabaseTest extends AbstractDatabaseTest{

    Path dbPath

    @Override
    Database getDatabaseInstance() {

        def fileService = new FileService()
        def idPath = File.createTempFile('ids', '.txt').toPath()
        def idService = new IdService(idPath, fileService)

        dbPath = File.createTempFile('invoices', '.txt').toPath()
        new FileBasedDatabase<>(dbPath, idService, fileService, new JsonService(), Invoice)
    }

    def "file based database writes invoices to correct file"() {
        given:
        def db = getDatabaseInstance()

        when:
        db.save(invoice(4))

        then:
        1 == Files.readAllLines(dbPath).size()

        when:
        db.save(invoice(5))

        then:
        2 == Files.readAllLines(dbPath).size()
    }
}
