package app.dietdash.dto;

import app.dietdash.dto.deserializers.LocalDateListDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.LocalDate;
import java.util.List;

public class LocalDatesDto {

    @JsonDeserialize(using = LocalDateListDeserializer.class)
    private List<LocalDate> targetDates;

    public List<LocalDate> getTargetDates() {
        return targetDates;
    }

    public void setTargetDates(List<LocalDate> targetDates) {
        this.targetDates = targetDates;
    }
}
