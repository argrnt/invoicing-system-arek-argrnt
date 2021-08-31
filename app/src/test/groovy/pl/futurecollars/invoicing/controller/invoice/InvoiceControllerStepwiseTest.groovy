package pl.futurecollars.invoicing.controller.invoice

import com.mongodb.client.MongoDatabase
import org.springframework.context.ApplicationContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.service.JsonService
import spock.lang.Requires
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import static pl.futurecollars.invoicing.TestHelpers.invoice
import java.time.LocalDate
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
@SpringBootTest
@Stepwise
class InvoiceControllerStepwiseTest extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private JsonService jsonService

    @Shared
    private int invoiceId

    @Shared
    private boolean isSetupDone = false

    private Invoice originalInvoice = invoice(1)
    private LocalDate updatedDate = LocalDate.of(2021, 07, 27)
    private static final String ENDPOINT = "/invoices"

    @Autowired
    private ApplicationContext context

    def setup() {
        if (!isSetupDone) {
            deleteAllInvoices()
            isSetupDone = true
        }
    }

    void deleteAllInvoices() {
        getAllInvoices().each { invoice -> deleteInvoice(invoice.id) }
    }

    List<Invoice> getAllInvoices() {
        def response = mockMvc.perform(get(ENDPOINT))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .getContentAsString()

        return jsonService.stringToObject(response, Invoice[])
    }

    void deleteInvoice(long id) {
        mockMvc.perform(delete("$ENDPOINT/$id"))
                .andExpect(status().isNoContent())
    }

    @Requires({ System.getProperty('spring.profiles.active', 'memory').contains("mongo")})

    def "database is dropped to ensure clean state"() {
        expect:
        MongoDatabase mongoDatabase = context.getBean(MongoDatabase)
        mongoDatabase.drop()
    }

    def "empty array is returned when no invoices were created"() {

        when:
        def response = mockMvc.perform(get(ENDPOINT))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        then:
        response == "[]"
    }

    def "add single invoice"() {
        given:
        def invoiceAsJson = jsonService.objectToJson(originalInvoice)

        when:
        invoiceId = Integer.valueOf(
                mockMvc.perform(
                        post(ENDPOINT)
                                .content(invoiceAsJson)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn()
                        .response
                        .contentAsString
        )

        then:
        invoiceId > 0
    }


    def "one invoice is returned when getting all invoices"() {

        given:
        def expectedInvoice = originalInvoice
        expectedInvoice.id = invoiceId

        when:
        def response = mockMvc.perform(get(ENDPOINT))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def invoices = jsonService.stringToObject(response, Invoice[])

        then:
        invoices.size() == 1
        invoices[0] == expectedInvoice
    }


    def "invoice is returned correctly when getting by id"() {
        given:
        def expectedInvoice = originalInvoice
        expectedInvoice.id = invoiceId

        when:
        def response = mockMvc.perform(get("$ENDPOINT/$invoiceId"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def invoice = jsonService.stringToObject(response, Invoice)

        then:
        invoice == expectedInvoice
    }


    def "invoice date can be modified"() {
        given:
        def modifiedInvoice = originalInvoice
        modifiedInvoice.date = updatedDate

        def invoiceAsJson = jsonService.objectToJson(modifiedInvoice)

        expect:
        mockMvc.perform(
                put("$ENDPOINT/$invoiceId")
                        .content(invoiceAsJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isNoContent())
    }

    def "updated invoice is returned correctly when getting by id"() {
        given:
        def expectedInvoice = originalInvoice
        expectedInvoice.id = invoiceId
        expectedInvoice.date = updatedDate

        when:
        def response = mockMvc.perform(get("$ENDPOINT/$invoiceId"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def invoices = jsonService.stringToObject(response, Invoice)

        then:
        invoices == expectedInvoice
    }

    def "invoice can be deleted"() {
        expect:
        mockMvc.perform(delete("$ENDPOINT/$invoiceId"))
                .andExpect(status().isNoContent())

        and:
        mockMvc.perform(delete("$ENDPOINT/$invoiceId"))
                .andExpect(status().isNotFound())

        and:
        mockMvc.perform(get("$ENDPOINT/$invoiceId"))
                .andExpect(status().isNotFound())
    }
}
