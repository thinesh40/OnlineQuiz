package com.xon.onlinequiz;

public class Lecturer {
    String lecturerID, name, phone, email, password, accommodation;

    Lecturer(String id, String em, String pa, String ph, String n, String lo) {
        lecturerID = id;
        email = em;
        password = pa;
        phone = ph;
        name = n;
        accommodation = lo;
    }
}
