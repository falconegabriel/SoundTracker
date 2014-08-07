package br.cin.ufpe.soundtracker.utils;

import in.ubee.models.Retail;

public final class Configurations {
    
    public static final String APP_ID = "e5d079f9adcb039fed69bf99023ec15509fd656b85ecad6d7afca66ce298971c";
    public static final String APP_SECRET = "91ee95fe17bb2745c9fa114871c86f9989027565f5958e052f31a9be0a6e1154";
    
    private Configurations() {} 

    public static Retail getDefaultRetail() {
        Retail retail = new Retail();
        retail.setId("53ade3b424c5e541df0001f7");
        retail.setName("Cin UFPE");
        return retail;
    }
}
