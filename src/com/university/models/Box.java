package com.university.models;

public class Box {

    private String  boxId;
    private String  bloc;
    private String  floor;
    private boolean isAvailable;

    public Box(String boxId, String bloc, String floor, boolean isAvailable) {
        this.boxId       = boxId;
        this.bloc        = bloc;
        this.floor       = floor;
        this.isAvailable = isAvailable;
    }

    public String  getBoxId()      { return boxId; }
    public String  getBloc()       { return bloc; }
    public String  getFloor()      { return floor; }
    public boolean isAvailable()   { return isAvailable; }
    public void setAvailable(boolean available) { this.isAvailable = available; }

    @Override
    public String toString() {
        return "Box[" + boxId + "] Bloc " + bloc
             + " Floor " + floor
             + " | " + (isAvailable ? "Available" : "Booked");
    }
}