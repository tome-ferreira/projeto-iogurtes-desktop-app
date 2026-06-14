package com.gestaoiogurtes.api;

import com.gestaoiogurtes.config.ApiConfig;
import com.gestaoiogurtes.utils.SessionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;


public final class RetrofitClient {

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonDeserializer<LocalDateTime>) (json, type, ctx) -> {
                        String raw = json.getAsString();
                        raw = raw.replaceAll("(\\.\\d{6})\\d+", "$1");
                        return LocalDateTime.parse(raw,
                                DateTimeFormatter.ofPattern(
                                        "yyyy-MM-dd'T'HH:mm:ss[.SSSSSS][.SSSSS][.SSSS][.SSS][.SS][.S]"));
                    })
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonSerializer<LocalDateTime>) (src, type, ctx) -> new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDate.class,
                    (JsonDeserializer<LocalDate>) (json, type, ctx) -> LocalDate.parse(json.getAsString()))
            .registerTypeAdapter(LocalDate.class,
                    (JsonSerializer<LocalDate>) (src, type, ctx) -> new JsonPrimitive(src.toString()))
            .create();

    
    // Singleton

    private static volatile RetrofitClient instance;

    public static RetrofitClient getInstance() {
        if (instance == null) {
            synchronized (RetrofitClient.class) {
                if (instance == null) {
                    instance = new RetrofitClient();
                }
            }
        }
        return instance;
    }

   
    // Estado interno
   

    private final Retrofit retrofit;

    private RetrofitClient() {
        // ── OkHttp client ────────────────────────────────────────────────────
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(ApiConfig.TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(ApiConfig.TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(ApiConfig.TIMEOUT, TimeUnit.SECONDS);

        // ── Interceptor de autorização JWT ────────────────────────────────────
        // Lê o token da sessão activa em cada pedido.
        // Se não houver token (ex: /auth/login), o pedido é enviado sem header.
        httpClientBuilder.addInterceptor(chain -> {
            Request original = chain.request();
            String token = SessionManager.getInstance().getAuthToken();
            if (token != null && !token.isBlank()) {
                Request authenticated = original.newBuilder()
                        .header("Authorization", "Bearer " + token)
                        .build();
                return chain.proceed(authenticated);
            }
            return chain.proceed(original);
        });

        if (ApiConfig.LOGGING_ENABLED) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClientBuilder.addInterceptor(loggingInterceptor);
        }

        OkHttpClient httpClient = httpClientBuilder.build();

        // ── Retrofit instance ─────────────────────────────────────────────────
        this.retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    // API pública
    /**
     * Cria (ou devolve em cache) um proxy da interface Retrofit indicada.
     */
    public <T> T getService(Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }
}