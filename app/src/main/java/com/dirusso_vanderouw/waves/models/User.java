package com.dirusso_vanderouw.waves.models;

import java.io.Serializable;
import java.util.List;

import dirusso_vanderouw.services.models.Profile;

/**
 * Created by matia on 4/1/2017.
 */
public class User implements Serializable {
    private long mUserId;
    private String mName;
    private String mEmail;
    private List<Profile> mProfiles;

    private User() {
    }

    private User(Builder builder) {
        this.mEmail = builder.mEmail;
        this.mName = builder.mName;
        this.mUserId = builder.mUserId;
        this.mProfiles = builder.mProfiles;
    }

    public long getuserId() {
        return mUserId;
    }

    public String getName() {
        return mName;
    }

    public String getEmail() {
        return mEmail;
    }

    public List<Profile> getProfiles() {
        return mProfiles;
    }

    static class Builder {
        private long mUserId;
        private String mName;
        private String mEmail;
        private List<Profile> mProfiles;

        public Builder withId(long id) {
            this.mUserId = id;
            return this;
        }

        public Builder withName(String name) {
            this.mName = name;
            return this;
        }

        public Builder withEmail(String email) {
            this.mEmail = email;
            return this;
        }

        public Builder withProfiles(List<Profile> profiles) {
            this.mProfiles = profiles;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
