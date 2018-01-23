package dirusso.services.business;


import java.util.List;

import dirusso.services.models.Beach;
import dirusso.services.models.Profile;
import dirusso.services.models.ResponseGetAllAttribute;
import rx.Observable;

public interface WavesRepository {

    Observable<List<Profile>> getProfiles();

    Observable<List<Beach>> getBeaches();

    Observable<List<Beach>> reportDataFromBeach(String beachId, Beach beach);

    Observable<List<ResponseGetAllAttribute>> getAttributeList();

}
