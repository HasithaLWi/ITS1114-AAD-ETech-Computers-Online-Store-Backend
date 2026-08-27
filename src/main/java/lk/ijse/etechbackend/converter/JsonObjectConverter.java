package lk.ijse.etechbackend.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter
public class JsonObjectConverter implements AttributeConverter<Object, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Object attribute) {
        if (attribute == null) {
            return null;
        }
        if (attribute instanceof String) {
            return (String) attribute;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error("Error converting Object to JSON string", e);
            return null;
        }
    }

    @Override
    public Object convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, Object.class);
        } catch (Exception e) {
            log.error("Error converting JSON string to Object: {}", dbData, e);
            return dbData;
        }
    }
}
