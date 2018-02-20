package dirusso.services.business;


import com.google.common.collect.Lists;

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
        Profile profile3 = new Profile.Builder()
                .withProfileAttributes(Lists.newArrayList(new AttributeValue("WAVES", 0), new AttributeValue("WIND", 2)))
                .withProfileId(1)
                .withName("WindSurfer").build();
        Profile profile4 = new Profile.Builder()
                .withProfileAttributes(Lists.newArrayList(new AttributeValue("FLAG", 0), new AttributeValue("WIND", 0),
                        new AttributeValue("WATER", 2), new AttributeValue("WAVES", 0)))
                .withProfileId(1)
                .withName("Swimmer").build();
        profiles.add(profile1);
        profiles.add(profile2);
        profiles.add(profile3);
        profiles.add(profile4);
        return Observable.just(profiles);
        //return WavesRestClient.getProfiles().map(responseProfiles -> responseProfiles);
    }

    @Override
    public Observable<List<Beach>> getBeaches() {

        if (beaches.isEmpty()) {
            LatitudeLongitude upLeft = new LatitudeLongitude(-34.911527, -56.145071);
            LatitudeLongitude upRight = new LatitudeLongitude(-34.909697, -56.142764);
            LatitudeLongitude downLeft = new LatitudeLongitude(-34.912081, -56.144255);
            LatitudeLongitude downRight = new LatitudeLongitude(-34.910480, -56.141584);
            Beach beach = new Beach.Builder(upLeft, upRight, downLeft, downRight).withId(1)
                                                                                 .withName("Playa Pocitos KIBON")
                                                                                 .withAttributes(Lists.newArrayList(new AttributeValue("FLAG", 2), new
                                                                                                 AttributeValue("WIND", 2),
                                                                                         new AttributeValue("WATER", 2), new AttributeValue("WAVES",
                                                                                                 2), new AttributeValue("JELLYFISH", 1)))
                                                                                 .withDescription("La playa de pocitos es una hermosa playa para " +
                                                                                         "relajarse que no se ajusta a deportes acuaticos")
                                                                                 .build();
            upLeft = new LatitudeLongitude(-34.919204, -56.147967);
            upRight = new LatitudeLongitude(-34.918192, -56.148589);
            downLeft = new LatitudeLongitude(-34.919019, -56.147881);
            downRight = new LatitudeLongitude(-34.918113, -56.148353);
            Beach beach2 = new Beach.Builder(upLeft, upRight, downLeft, downRight).withId(2)
                                                                                  .withName("Playa Departamento Seguridad")
                                                                                  .withAttributes(Lists.newArrayList(new AttributeValue("FLAG", 1),
                                                                                          new

                                                                                                  AttributeValue("WIND", 1),
                                                                                          new AttributeValue("WATER", 1), new AttributeValue("WAVES",
                                                                                                  1)))
                                                                                  .withDescription("La playa departamento es muy linda")
                                                                                  .build();
            upLeft = new LatitudeLongitude(-34.918192, -56.148589);
            upRight = new LatitudeLongitude(-34.916239, -56.148804);
            downLeft = new LatitudeLongitude(-34.918113, -56.148353);
            downRight = new LatitudeLongitude(-34.91623, -56.148117);

            Beach beach3 = new Beach.Builder(upLeft, upRight, downLeft, downRight).withId(3)
                                                                                  .withName("Playa Benito Blanco")
                                                                                  .withAttributes(Lists.newArrayList(new AttributeValue("FLAG", 0),
                                                                                          new
                                                                                                  AttributeValue("WIND", 0),
                                                                                          new AttributeValue("WATER", 0), new AttributeValue("WAVES",
                                                                                                  0), new AttributeValue("JELLYFISH", 1)))
                                                                                  .withDescription("La playa Benito Blanco ofrece actividades para " +
                                                                                          "todos los gustos")
                                                                                  .build();

            upLeft = new LatitudeLongitude(-34.916239, -56.148804);
            upRight = new LatitudeLongitude(-34.913881, -56.147634);
            downLeft = new LatitudeLongitude(-34.91623, -56.148117);
            downRight = new LatitudeLongitude(-34.91418, -56.146969);

            Beach beach4 = new Beach.Builder(upLeft, upRight, downLeft, downRight).withId(4)
                                                                                  .withName("Playa Express")
                                                                                  .withAttributes(Lists.newArrayList(new AttributeValue("FLAG", 1),
                                                                                          new
                                                                                                  AttributeValue("WIND", 2), new AttributeValue
                                                                                                  ("WAVES", 1)))
                                                                                  .withDescription("La playa express posee un estadio de volley")
                                                                                  .build();

            upLeft = new LatitudeLongitude(-34.905464, -56.132315);
            upRight = new LatitudeLongitude(-34.904742, -56.13106);
            downLeft = new LatitudeLongitude(-34.905718, -56.131822);
            downRight = new LatitudeLongitude(-34.905076, -56.130942);

            Beach beach5 = new Beach.Builder(upLeft, upRight, downLeft, downRight).withId(5)
                                                                                  .withName("Playa Buceo")
                                                                                  .withAttributes(Lists.newArrayList(new AttributeValue("FLAG", 1),
                                                                                          new
                                                                                                  AttributeValue("WIND", 1),
                                                                                          new AttributeValue("WATER", 2), new AttributeValue("WAVES",
                                                                                                  2)))
                                                                                  .withDescription("La playa Buceo es ideal para surfear")
                                                                                  .build();

            upLeft = new LatitudeLongitude(-34.913881, -56.147634);
            upRight = new LatitudeLongitude(-34.911527, -56.145071);
            downLeft = new LatitudeLongitude(-34.91418, -56.146969);
            downRight = new LatitudeLongitude(-34.912081, -56.144255);

            Beach beach6 = new Beach.Builder(upLeft, upRight, downLeft, downRight).withId(6)
                                                                                  .withName("Playa Pre Pocitos")
                                                                                  .withAttributes(Lists.newArrayList(new AttributeValue("FLAG", 0),
                                                                                          new
                                                                                                  AttributeValue("WIND", 0),
                                                                                          new AttributeValue("WATER", 1), new AttributeValue("WAVES",
                                                                                                  2), new AttributeValue("JELLYFISH", 1)))
                                                                                  .withDescription("La playa Pre Pocitos es bastante amplia")
                                                                                  .build();

            beaches.add(beach);
            beaches.add(beach2);
            beaches.add(beach3);
            beaches.add(beach4);
            beaches.add(beach5);
            beaches.add(beach6);

        }

        return Observable.just(beaches);
        //        return WavesRestClient.getBeaches().map(responseBeaches -> responseBeaches);
    }


    @Override
    public Observable<List<Beach>> reportDataFromBeach(Beach beach) {
        int beachIndex = -1;
        for (int i = 0; i < beaches.size(); i++) {
            if (beaches.get(i).getBeachId() == beach.getBeachId()) {
                beachIndex = i;
            }
        }
        for (AttributeValue attributeValue : beaches.get(beachIndex).getAttibutesValuesList()) {
            boolean isPresent = false;
            for (AttributeValue newAttributeValue : beach.getAttibutesValuesList()) {
                if (attributeValue.getAttribute().equals(newAttributeValue.getAttribute())) {
                    isPresent = true;
                }
            }
            if (!isPresent) {
                beach.getAttibutesValuesList().add(attributeValue);
            }
        }
        beaches.remove(beachIndex);
        beaches.add(beachIndex, beach);
        return Observable.just(beaches);
        //return WavesRestClient.reportDataFromBeach(beachId, beach).map(responseBeachList -> responseBeachList);
    }

    @Override
    public Observable<List<ResponseGetAllAttribute>> getAttributeList() {

        List<ResponseGetAllAttribute> attributesResponse = Lists.newArrayList(
                new ResponseGetAllAttribute("WAVES", false), new ResponseGetAllAttribute("FLAG", false), new ResponseGetAllAttribute("WIND", false),
                new ResponseGetAllAttribute("WATER", false), new ResponseGetAllAttribute("JELLYFISH", true));
        return Observable.just(attributesResponse);

        //return WavesRestClient.getAttributeList().map(responseAttributeList -> responseAttributeList);
    }

}
