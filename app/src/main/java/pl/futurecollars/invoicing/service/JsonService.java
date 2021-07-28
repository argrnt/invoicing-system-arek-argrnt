package pl.futurecollars.invoicing.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.model.Invoice;

@Service
public class JsonService {

    private final ObjectMapper objectMapper;

    public JsonService() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public String objectToJson(Invoice invoice) {
        try {
            return objectMapper.writeValueAsString(invoice);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert object to json", e);
        }
    }

    public <T> T stringToObject(String objectAsString, Class<T> className) {
        try {
            return objectMapper.readValue(objectAsString, className);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert string to object", e);
        }
    }
}
