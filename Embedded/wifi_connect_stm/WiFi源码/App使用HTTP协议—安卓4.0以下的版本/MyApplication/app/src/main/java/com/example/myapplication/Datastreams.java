package com.example.myapplication;

import java.util.List;

public class Datastreams {
    private List<Datapoints> datapoints;
    private String id;
    public void setDatapoints(List<Datapoints> datapoints){this.datapoints=datapoints;}
    public List<Datapoints> getDatapoints(){return datapoints;}
    //public String getId(){return  id;}
}
