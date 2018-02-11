package dirusso.services.business;


import com.google.common.collect.Lists;
import com.google.gson.Gson;

import java.util.List;

import javax.inject.Inject;

import dirusso.services.models.AttributeValue;
import dirusso.services.models.Beach;
import dirusso.services.models.LatitudeLongitude;
import dirusso.services.models.Profile;
import dirusso.services.models.ResponseGetAllAttribute;
import rx.Observable;

/**
 * Photo repository implementation
 */
public class WavesApiClientImpl implements WavesRepository {

    private WavesRestClient WavesRestClient;
    private List<Beach> beaches;

    @Inject
    public WavesApiClientImpl(WavesRestClient WavesRestClient, List<Beach> beaches) {
        this.WavesRestClient = WavesRestClient;
        this.beaches = beaches;
    }

    @Override
    public Observable<List<Profile>> getProfiles() {
        List<Profile> profiles = Lists.newArrayList();

        Profile profile1 = new Profile.Builder()
                .withProfileAttributes(Lists.newArrayList(new AttributeValue("FLAG", 2), new AttributeValue("WAVES", 2)))
                .withProfileId(0)
                .withName("Surfer").build();
        Profile profile2 = new Profile.Builder()
                .withProfileAttributes(Lists.newArrayList(new AttributeValue("FLAG", 0), new AttributeValue("WIND", 0),
                        new AttributeValue("WATER", 0)))
                .withProfileId(1)
                .withName("Family").build();

        profiles.add(profile1);
        profiles.add(profile2);
        return Observable.just(profiles);
        //return WavesRestClient.getProfiles().map(responseProfiles -> responseProfiles);
    }

    @Override
    public Observable<List<Beach>> getBeaches() {


        LatitudeLongitude upLeft = new LatitudeLongitude(-34.911527, -56.145071);
        LatitudeLongitude upRight = new LatitudeLongitude(-34.909697, -56.142764);
        LatitudeLongitude downLeft = new LatitudeLongitude(-34.912081, -56.144255);
        LatitudeLongitude downRight = new LatitudeLongitude(-34.910480, -56.141584);
        Beach beach = new Beach.Builder(upLeft, upRight, downLeft, downRight).withId(1234)
                                                                             .withName("Playa Pocitos KIBON")
                                                                             .withAttributes(Lists.newArrayList(new AttributeValue("FLAG", 2), new
                                                                                             AttributeValue("WIND", 2),
                                                                                     new AttributeValue("WATER", 2), new AttributeValue("WAVES",
                                                                                             2), new AttributeValue("JELLYFISH", 2)))
                                                                             .withDescription("La playa de pocitos es una hermosa playa para " +
                                                                                     "relajarse que no se ajusta a deportes acuaticos")
                                                                             .build();
        List<Beach> beaches = Lists.newArrayList(beach);
        return Observable.just(beaches);
        //        return WavesRestClient.getBeaches().map(responseBeaches -> responseBeaches);
    }


    @Override
    public Observable<List<Beach>> reportDataFromBeach(String beachId, Beach beach) {
        String beachJson = new Gson().toJson(beach);
        return Observable.just(Lists.newArrayList());
        //return WavesRestClient.reportDataFromBeach(beachId, beach).map(responseBeachList -> responseBeachList);
    }

    @Override
    public Observable<List<ResponseGetAllAttribute>> getAttributeList() {

        List<ResponseGetAllAttribute> attributesResponse = Lists.newArrayList(
                new ResponseGetAllAttribute("WAVES"), new ResponseGetAllAttribute("FLAG"), new ResponseGetAllAttribute("WIND"), new
                        ResponseGetAllAttribute("WATER"), new ResponseGetAllAttribute("JELLYFISH"));
        return Observable.just(attributesResponse);

        //return WavesRestClient.getAttributeList().map(responseAttributeList -> responseAttributeList);
    }

}
