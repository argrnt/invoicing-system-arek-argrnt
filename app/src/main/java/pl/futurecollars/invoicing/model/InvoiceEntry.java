package pl.futurecollars.invoicing.model;

import static javax.persistence.CascadeType.ALL;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceEntry {

    @Id
    @JsonIgnore
    @JoinColumn(name = "entry_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "Invoice entry id (generated by application)", required = true, example = "1")
    private Long id;

    @ApiModelProperty(value = "Product/service description", required = true, example = "Dell X12 v3")
    private String description;

    @ApiModelProperty(value = "Number of items", required = true, example = "85")
    private BigDecimal quantity;

    @ApiModelProperty(value = "Product/service net price", required = true, example = "1857.15")
    private BigDecimal netPrice;

    @ApiModelProperty(value = "Product/service tax value", required = true, example = "139.46")
    @Builder.Default
    private BigDecimal vatValue = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @ApiModelProperty(value = "Tax rate", required = true)
    private Vat vatRate;

    @JoinColumn(name = "expense_related_to_car")
    @OneToOne(cascade = ALL, orphanRemoval = true)
    @ApiModelProperty(value = "Car this expense is related to, empty if expense is not related to car")
    private Car expenseRelatedToCar;

}
