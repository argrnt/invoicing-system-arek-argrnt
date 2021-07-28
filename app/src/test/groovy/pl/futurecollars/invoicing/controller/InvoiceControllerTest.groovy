package pl.futurecollars.invoicing.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import pl.futurecollars.invoicing.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.service.JsonService
import spock.lang.Specification
import spock.lang.Unroll

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
@SpringBootTest
@Unroll
class InvoiceControllerTest extends Specification {

    private static final String ENDPOINT = "/invoices"

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private JsonService jsonService

    private int addInvoiceAndReturnId(String invoiceAsJson) {
        Integer.valueOf(
                mockMvc.perform(
                        post(ENDPOINT)
                                .content(invoiceAsJson)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                        .andExpect(status().isOk())
                        .andReturn()
                        .response
                        .contentAsString
        )
    }

    private List<Invoice> addInvoices(int count) {
        (1..count).collect({ id ->
            def invoice = TestHelpers.invoice(id)
            invoice.id = addInvoiceAndReturnId(jsonService.objectToJson(invoice))
            return invoice
        })
    }

    private List<Invoice> getAllInvoices() {
        def response = mockMvc.perform(get(ENDPOINT))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        return jsonService.stringToObject(response, Invoice[])
    }

    private Invoice getInvoiceById(int id) {
        def response = mockMvc.perform(get("$ENDPOINT/$id"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        return jsonService.stringToObject(response, Invoice)
    }

    private ResultActions deleteInvoice(int id) {
        mockMvc.perform(delete("$ENDPOINT/$id"))
                .andExpect(status().isNoContent())
    }

    private String invoiceAsJson(int id) {
        return jsonService.objectToJson(TestHelpers.invoice(id))
    }

    def setup() {
        getAllInvoices().each { invoice -> deleteInvoice(invoice.id) }
    }

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
        def expectedInvoices = addInvoices(numberOfInvoices)

        when:
        def invoices = getAllInvoices()

        then:
        invoices.size() == numberOfInvoices
        invoices == expectedInvoices
    }

    def "correct invoice is returned when getting by id"() {
        given:
        def expectedInvoices = addInvoices(5)
        def verifiedInvoice = expectedInvoices.get(2)

        when:
        def invoice = getInvoiceById(verifiedInvoice.getId())

        then:
        invoice == verifiedInvoice
    }

    def "404 is returned when invoice id is not found when getting invoice by id [#id]"() {
        given:
        addInvoices(11)

        expect:
        mockMvc.perform(
                get("$ENDPOINT/$id")
        )
                .andExpect(status().isNotFound())

        where:
        id << [-100, -2, -1, 0, 168, 1256]
    }

    def "404 is returned when invoice id is not found when deleting invoice [#id]"() {
        given:
        addInvoices(11)

        expect:
        mockMvc.perform(
                delete("$ENDPOINT/$id")
        )
                .andExpect(status().isNotFound())

        where:
        id << [-77, -21, -1, 0, 12, 23, 79, 117, 1000]
    }

    def "404 is returned when invoice id is not found when updating invoice [#id]"() {
        given:
        addInvoices(11)

        expect:
        mockMvc.perform(
                put("$ENDPOINT/$id")
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
                put("$ENDPOINT/$id")
                        .content(jsonService.objectToJson(updatedInvoice))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNoContent())

        getInvoiceById(id) == updatedInvoice
    }

    def "invoice can be deleted"() {
        given:
        def invoices = addInvoices(69)

        expect:
        invoices.each { invoice -> deleteInvoice(invoice.getId()) }
        getAllInvoices().size() == 0
    }

}
