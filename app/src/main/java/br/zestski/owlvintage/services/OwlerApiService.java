package br.zestski.owlvintage.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import br.zestski.owlvintage.models.response.APIResponse;
import br.zestski.owlvintage.models.response.OverTheAirResponse;
import br.zestski.owlvintage.models.status.StatusModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Interface for defining API endpoints and their corresponding request methods.
 *
 * @author Zestski
 */
public interface OwlerApiService {

    /**
     * Fetches the OTA data.
     *
     * @param appVersionCode The version code of the application.
     * @return A Call object for making the API request.
     */
    @GET("/v1/version.json")
    Call<OverTheAirResponse> getOverTheAir(
            @Header("app-version") int appVersionCode
    );

    /**
     * Verifies the user's credentials.
     *
     * @param authorizationHeader The authorization header containing the user's credentials.
     * @return A Call object for making the API request.
     */
    @GET("/v1/account/verify_credentials.json")
    Call<APIResponse> verifyCredentials(
            @Header("Authorization") @NonNull String authorizationHeader
    );

    /**
     * Fetches the home timeline.
     *
     * @param authorizationHeader The authorization header containing the user's credentials.
     * @param page                The page number.
     * @param count               The number of statuses per page.
     * @param full                Whether to fetch full status details.
     * @return A Call object for making the API request.
     */
    @GET("/v1/statuses/home_timeline.json")
    Call<List<StatusModel>> getHomeTimeline(
            @Header("Authorization") @Nullable String authorizationHeader,
            @Query("page") int page,
            @Query("count") int count,
            @Query("full") boolean full
    );

    /**
     * Fetches the public timeline.
     *
     * @param authorizationHeader The authorization header containing the user's credentials.
     * @param page                The page number.
     * @param count               The number of statuses per page.
     * @param full                Whether to fetch full status details.
     * @return A Call object for making the API request.
     */
    @GET("/v1/statuses/public_timeline.json")
    Call<List<StatusModel>> getPublicTimeline(
            @Header("Authorization") @Nullable String authorizationHeader,
            @Query("page") int page,
            @Query("count") int count,
            @Query("full") boolean full
    );

    /**
     * Sends a new status update.
     *
     * @param authorizationHeader The authorization header containing the user's credentials.
     * @param status              The status message to be posted.
     * @param source              The source of the status update.
     * @return A Call object for making the API request.
     */
    @POST("/v1/statuses/update.json")
    Call<APIResponse> sendStatus(
            @Header("Authorization") @NonNull String authorizationHeader,
            @Query("status") @NonNull String status,
            @Query("source") @Nullable String source
    );
}