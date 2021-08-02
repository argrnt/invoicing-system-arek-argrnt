package pl.futurecollars.invoicing.service

import static pl.futurecollars.invoicing.TestHelpers.invoice
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification

class JsonServiceTest extends Specification{

    def "can convert object to json and read it back"() {
        given:
        def jsonService = new JsonService()
        def invoice = invoice(12)

        when:
        def invoiceAsString = jsonService.objectToJson(invoice)

        and:
        def invoiceFromJson = jsonService.stringToObject(invoiceAsString, Invoice)

        then:
        invoice == invoiceFromJson
    }
}
