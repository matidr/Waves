package dirusso_vanderouw.services.business;


import java.util.List;

import dirusso_vanderouw.services.models.AttributeValue;
import dirusso_vanderouw.services.models.Beach;
import dirusso_vanderouw.services.models.Profile;
import dirusso_vanderouw.services.models.ResponseGetAllAttribute;
import dirusso_vanderouw.services.models.ResponseGetAllProfiles;
import dirusso_vanderouw.services.models.ResponseGetDataForBeach;
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
