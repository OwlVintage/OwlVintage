package br.zestski.owlvintage.services;

import androidx.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import br.zestski.owlvintage.BuildConfig;
import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Class responsible for creating and managing Retrofit clients for different base URLs.
 *
 * @author Zestski
 */
public class RetrofitClient {

    private static final String BASE_URL_OWLER_CLOUD = "https://api.owler.cloud/";
    private static final String BASE_URL_OWL_VINTAGE = "https://owlvintage.vercel.app/";

    private static Retrofit retrofitOwlerCloud, retrofitOwlVintage;

    /**
     * Gets or creates a Retrofit client for the Owler Cloud base URL.
     *
     * @return The Retrofit client for Owler Cloud.
     */
    public static Retrofit getClientForOwlerCloud() {
        if (retrofitOwlerCloud == null) {
            retrofitOwlerCloud = createRetrofit(BASE_URL_OWLER_CLOUD);
        }
        return retrofitOwlerCloud;
    }

    /**
     * Gets or creates a Retrofit client for the Owl Vintage base URL.
     *
     * @return The Retrofit client for Owl Vintage.
     */
    public static Retrofit getClientForOwlVintage() {
        if (retrofitOwlVintage == null) {
            retrofitOwlVintage = createRetrofit(BASE_URL_OWL_VINTAGE);
        }
        return retrofitOwlVintage;
    }

    /**
     * Creates a new Retrofit instance with the specified base URL.
     *
     * @param baseUrl The base URL for the Retrofit client.
     * @return The created Retrofit instance.
     */
    private static Retrofit createRetrofit(
            @NonNull String baseUrl
    ) {
        var loggingInterceptor = new HttpLoggingInterceptor();

        if (BuildConfig.DEBUG) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        var okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    okhttp3.Request originalRequest = chain.request();
                    okhttp3.Request requestWithNoCache = originalRequest.newBuilder()
                            .cacheControl(CacheControl.FORCE_NETWORK)
                            .build();
                    return chain.proceed(requestWithNoCache);
                })
                .build();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}