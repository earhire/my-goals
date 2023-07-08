package com.example.my_goals.models;

public  class Session {
    public static class SessionDetails {
        public static String fn, sn, userUI, userType, email;

        public static void logout(){
            fn = null;
            sn = null;
            userUI = null;
            userType = null;
            email = null;
        }
    }
}

