package pl.futurecollars.invoicing.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.service.JsonService
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print



@AutoConfigureMockMvc
@SpringBootTest
@Stepwise
class InvoiceControllerStepwiseTest extends Specification{

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private JsonService jsonService;

    private Invoice originalInvoice = TestHelpers.invoice(1)

    private LocalDate updatedDate = LocalDate.of(2021, 07, 27)

    private static final ENDPOINT = "/invoices"

    @Shared
    private int invoiceId

    def "empty array is returned when no invoices were added"() {
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
        def response = mockMvc.perform(get("/invoices/$invoiceId"))
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
                put("/invoices/$invoiceId")
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
        def response = mockMvc.perform(get("/invoices/$invoiceId"))
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
        mockMvc.perform(delete("/invoices/$invoiceId"))
                .andExpect(status().isNoContent())

        and:
        mockMvc.perform(delete("/invoices/$invoiceId"))
                .andExpect(status().isNotFound())

        and:
        mockMvc.perform(get("/invoices/$invoiceId"))
                .andExpect(status().isNotFound())
    }
}