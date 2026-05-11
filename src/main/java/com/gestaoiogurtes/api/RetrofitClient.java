package com.gestaoiogurtes.api;

import com.gestaoiogurtes.config.ApiConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * Singleton que fornece uma instância partilhada do cliente {@link Retrofit}.
 *
 * <h3>Configuração</h3>
 * <p>
 * Lida automaticamente a partir de {@link ApiConfig}:
 * <ul>
 * <li>URL base – {@code ApiConfig.BASE_URL}</li>
 * <li>Timeout – {@code ApiConfig.TIMEOUT} segundos</li>
 * <li>Logging – activado se {@code ApiConfig.LOGGING_ENABLED == true}</li>
 * </ul>
 * </p>
 *
 * <h3>Utilização</h3>
 * 
 * <pre>{@code
 * MyApiInterface api = RetrofitClient.getInstance().getService(MyApiInterface.class);
 * Call<List<IogurteVM>> call = api.listarTodos();
 * }</pre>
 */
public final class RetrofitClient {
    // TODO: Desdafios do relatorio

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

    // ──────────────────────────────────────────────────────────────────────────
    // Singleton
    // ──────────────────────────────────────────────────────────────────────────

    private static volatile RetrofitClient instance;

    /**
     * Devolve a instância única (criação lazy thread-safe via double-checked
     * locking).
     */
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

    // ──────────────────────────────────────────────────────────────────────────
    // Estado interno
    // ──────────────────────────────────────────────────────────────────────────

    private final Retrofit retrofit;

    private RetrofitClient() {
        // ── OkHttp client ────────────────────────────────────────────────────
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(ApiConfig.TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(ApiConfig.TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(ApiConfig.TIMEOUT, TimeUnit.SECONDS);

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

    // ──────────────────────────────────────────────────────────────────────────
    // API pública
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Cria (ou devolve em cache) um proxy da interface Retrofit indicada.
     *
     * @param <T>          tipo da interface Retrofit
     * @param serviceClass interface Retrofit anotada com {@code @GET},
     *                     {@code @POST}, etc.
     * @return instância da interface pronta a usar
     */
    public <T> T getService(Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }
}