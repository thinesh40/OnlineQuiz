package com.xon.onlinequiz;

public class User {

        String id,role,name,phone,email,password,accommodation,latitude,longitude;

    User(String mn,String role1, String em, String pa, String ph, String n, String lo,String la, String lon) {
        id = mn;
        email = em;
        password = pa;
        phone = ph;
        name = n;
        accommodation = lo;
        role =  role1;
        latitude = la;
        longitude = lon;
    }

}
