package app.dietdash.service;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MailTrapClient {
    private final OkHttpClient client;

    @Value("${mailtrap.api.token}")
    private String apiToken;

    @Value("${mailtrap.api.endpoint}")
    private String apiEndpoint;

    public MailTrapClient() {
        this.client = new OkHttpClient();
    }

    @Async
    public void sendEmail(String to, String subject, String body, boolean isHtml) throws IOException {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        json.put("from", new JSONObject().put("email", "no-reply@diet-dash.com"));
        json.put("to", new org.json.JSONArray().put(new JSONObject().put("email", to)));
        json.put("subject", subject);

        if (isHtml) {
            json.put("html", body);
        } else {
            json.put("text", body);
        }

        RequestBody requestBody = RequestBody.create(json.toString(), JSON);

        Request request = new Request.Builder()
                .url(apiEndpoint)
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + apiToken)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println(response.body() != null ? response.body().string() : "No response body");
        }
    }
}
