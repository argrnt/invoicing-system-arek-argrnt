package pl.futurecollars.invoicing

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pl.futurecollars.invoicing.service.InvoiceService
import spock.lang.Specification

@SpringBootTest
class AppTest extends Specification {

    @Autowired
    private InvoiceService invoiceService;

    def "test invoice service injection"() {

        expect:
        invoiceService
    }
}
