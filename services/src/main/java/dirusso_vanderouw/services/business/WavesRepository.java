package dirusso_vanderouw.services.business;


import java.util.List;

import dirusso_vanderouw.services.models.AttributeValue;
import dirusso_vanderouw.services.models.Beach;
import dirusso_vanderouw.services.models.Profile;
import dirusso_vanderouw.services.models.ResponseGetAllAttribute;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

public interface WavesRepository {

    Observable<List<Profile>> getProfiles();

    Observable<List<Beach>> getBeaches();

    Observable<List<Beach>> reportDataFromBeach(String beachId, Beach beach);

    Observable<List<ResponseGetAllAttribute>> getAttributeList();

}
