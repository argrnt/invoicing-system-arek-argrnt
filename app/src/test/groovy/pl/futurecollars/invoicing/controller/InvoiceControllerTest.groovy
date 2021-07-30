package pl.futurecollars.invoicing.controller

import org.springframework.http.MediaType
import pl.futurecollars.invoicing.TestHelpers

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class InvoiceControllerTest extends ControllerTest {

    def "empty array is returned when no invoices were added"() {
        expect:
        getAllInvoices() == []
    }

   def "add invoice returns sequential id"() {
        given:
        def invoiceAsJson = invoiceAsJson(1)

        expect:
        def firstId = addInvoiceAndReturnId(invoiceAsJson)
        addInvoiceAndReturnId(invoiceAsJson) == firstId + 1
        addInvoiceAndReturnId(invoiceAsJson) == firstId + 2
        addInvoiceAndReturnId(invoiceAsJson) == firstId + 3
        addInvoiceAndReturnId(invoiceAsJson) == firstId + 4
    }

    def "all invoices are returned when getting all invoices"() {
        given:
        def numberOfInvoices = 3
        def expectedInvoices = addUniqueInvoices(numberOfInvoices)

        when:
        def invoices = getAllInvoices()

        then:
        invoices.size() == numberOfInvoices
        invoices == expectedInvoices
    }

    def "correct invoice is returned when getting by id"() {
        given:
        def expectedInvoices = addUniqueInvoices(5)
        def verifiedInvoice = expectedInvoices.get(2)

        when:
        def invoice = getInvoiceById(verifiedInvoice.getId())

        then:
        invoice == verifiedInvoice
    }

    def "404 is returned when invoice id is not found when getting invoice by id [#id]"() {
        given:
        addUniqueInvoices(11)

        expect:
        mockMvc.perform(
                get("$INVOICE_ENDPOINT/$id")
        )
                .andExpect(status().isNotFound())

        where:
        id << [-100, -2, -1, 0, 168, 1256]
    }

    def "404 is returned when invoice id is not found when deleting invoice [#id]"() {
        given:
        addUniqueInvoices(11)

        expect:
        mockMvc.perform(
                delete("$INVOICE_ENDPOINT/$id")
        )
                .andExpect(status().isNotFound())

        where:
        id << [-77, -21, -1, 0, 12, 23, 79, 117, 1000]
    }

    def "404 is returned when invoice id is not found when updating invoice [#id]"() {
        given:
        addUniqueInvoices(11)

        expect:
        mockMvc.perform(
                put("$INVOICE_ENDPOINT/$id")
                        .content(invoiceAsJson(1))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())

        where:
        id << [-77, -21, -1, 0, 12, 23, 79, 117, 1000]
    }

    def "invoice date can be modified"() {
        given:
        def id = addInvoiceAndReturnId(invoiceAsJson(44))
        def updatedInvoice = TestHelpers.invoice(123)
        updatedInvoice.id = id

        expect:
        mockMvc.perform(
                put("$INVOICE_ENDPOINT/$id")
                        .content(jsonService.objectToJson(updatedInvoice))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNoContent())

        getInvoiceById(id) == updatedInvoice
    }

    def "invoice can be deleted"() {
        given:
        def invoices = addUniqueInvoices(69)

        expect:
        invoices.each { invoice -> deleteInvoice(invoice.getId()) }
        getAllInvoices().size() == 0
    }
}
