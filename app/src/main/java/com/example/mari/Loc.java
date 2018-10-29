package com.example.mari;

public class Loc {
    public float lati;
    public float longi;

    public Loc(float a, float b){
        lati = a;
        longi = b;
    }
    public void update(float a, float b){
            lati = a;
            longi = b;
    }

    public void copy(Loc loc){
        lati = loc.lati;
        longi = loc.longi;
    }
    public String print(){
        String r = "Lantitude: " + Float.toString(lati) + "\nLongitude: " + Float.toString(longi) + "\n";
        return r;
    }

}
