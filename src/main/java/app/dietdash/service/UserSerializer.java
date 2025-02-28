package app.dietdash.service;

import app.dietdash.model.User;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class UserSerializer extends JsonSerializer<User> {
    @Override
    public void serialize(User user, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (user != null) {
            gen.writeObject(Boolean.TRUE);
        } else {
            gen.writeObject(Boolean.FALSE);
        }
    }
}