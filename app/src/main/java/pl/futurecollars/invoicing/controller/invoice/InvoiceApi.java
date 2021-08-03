package pl.futurecollars.invoicing.controller.invoice;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.futurecollars.invoicing.model.Invoice;

@RequestMapping("invoices")
@Api(tags = {"invoices"})
public interface InvoiceApi {

    @GetMapping(produces = {"application/json;charset=UTF-8"})
    @ApiOperation("Get all invoices")
    List<Invoice> getAllInvoices();

    @PostMapping
    @ApiOperation("Add new invoice to system")
    int addInvoice(@RequestBody Invoice invoice);

    @GetMapping(value = "/{id}", produces = {"application/json;charset=UTF-8"})
    @ApiOperation("Get invoice by id")
    ResponseEntity<Invoice> getById(@PathVariable int id);

    @DeleteMapping("/{id}")
    @ApiOperation("Delete invoice with given id")
    ResponseEntity<?> deleteById(@PathVariable int id);

    @PutMapping("/{id}")
    @ApiOperation("Update invoice with given id")
    ResponseEntity<?> updateById(@PathVariable int id, @RequestBody Invoice updatedInvoice);
}
