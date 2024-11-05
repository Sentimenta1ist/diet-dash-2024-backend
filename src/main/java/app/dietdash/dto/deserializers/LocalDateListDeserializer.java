package app.dietdash.dto.deserializers;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LocalDateListDeserializer extends JsonDeserializer<List<LocalDate>> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

    @Override
    public List<LocalDate> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        List<String> dateStrings = ctxt.readValue(p, List.class);
        return dateStrings.stream()
                .map(date -> LocalDate.parse(date, formatter))
                .toList();
    }
}