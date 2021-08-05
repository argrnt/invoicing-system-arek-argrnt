package pl.futurecollars.invoicing.controller.invoice

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.controller.ControllerTest
import pl.futurecollars.invoicing.service.JsonService
import static pl.futurecollars.invoicing.TestHelpers.invoice
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class InvoiceControllerTest extends ControllerTest {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private JsonService jsonService


    def "empty array is returned when no invoices were added"() {
        expect:
        getAllInvoices() == []
    }

    def "empty array is returned when no invoices were added2"() {
        when:
        def response = mockMvc.perform(get(INVOICE_ENDPOINT))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString
        then:
        response == "[]"
    }

    def "empty array is returned when no invoices were created"() {
        expect:
        mockMvc.perform(get(INVOICE_ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString == "[]"
    }

    def "empty array is returned when no invoices were created2"() {
        when:
        def response = mockMvc.perform(get(INVOICE_ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString
        then:
        response == "[]"
    }


    def "add invoice returns sequential id"() {
        expect:
        def firstId = addInvoiceAndReturnId(invoice(1))
        addInvoiceAndReturnId(invoice(2)) == firstId + 1
        addInvoiceAndReturnId(invoice(3)) == firstId + 2
        addInvoiceAndReturnId(invoice(4)) == firstId + 3
        addInvoiceAndReturnId(invoice(5)) == firstId + 4
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
        def id = addInvoiceAndReturnId(invoice(44))
        def updatedInvoice = invoice(123)
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
