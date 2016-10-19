package com.techpro.chat.ticklechat.models;


import android.net.Uri;

import com.techpro.chat.ticklechat.utils.ValidationUtils;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class LoginModel {

    @Getter
    @Setter
    private Data data;

    public static class Data {

        @Getter
        @Setter
        private String city_name;
        @Getter
        @Setter
        private String area_id;
        @Getter
        @Setter
        private String location;
        @Getter
        @Setter
        private String about;
        @Getter
        @Setter
        private String contact;
        @Getter
        @Setter
        private String id;
        @Getter
        @Setter
        private String city_id;
        @Getter
        @Setter
        private String name;
        @Getter
        @Setter
        private String website;
        @Getter
        @Setter
        private String exotel_digits;

        @Getter
        @Setter
        private String url;
        @Getter
        @Setter
        private String location_id;
        @Getter
        @Setter
        private String pincode;
        @Getter
        @Setter
        private String mobile_number;
        @Getter
        @Setter
        private String address;
        @Getter
        @Setter
        private String email;
        @Getter
        @Setter
        private String facebook_url;
        @Getter
        @Setter
        private Uri photo_uri;


    }

}
