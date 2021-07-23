package pl.futurecollars.invoicing.service

import pl.futurecollars.invoicing.TestHelpers
import spock.lang.Specification

class JsonServiceTest extends Specification{

    def "can convert object to json and read it back"() {
        given:
        def jsonService = new JsonService()
        def invoice = TestHelpers.invoice(12)

        when:
        def invoiceAsString = jsonService.objectToJson(invoice)

        and:
        def invoiceFromJson = jsonService.stringToObject(invoiceAsString)

        then:
        invoice == invoiceFromJson
    }
}
