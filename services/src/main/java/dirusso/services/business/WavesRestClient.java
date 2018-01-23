package dirusso.services.business;


import java.util.List;

import dirusso.services.models.Beach;
import dirusso.services.models.Profile;
import dirusso.services.models.ResponseGetAllAttribute;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Rest interface for retrofit
 */
public interface WavesRestClient {

    @GET("Profiles")
    Observable<List<Profile>> getProfiles();

    @GET("Beaches")
    Observable<List<Beach>> getBeaches();

    @PUT("Beaches/{beachId}")
    Observable<List<Beach>> reportDataFromBeach(@Path("beachId") String beachId, @Body Beach beach);

    @GET("Attributes")
    Observable<List<ResponseGetAllAttribute>> getAttributeList();
}
