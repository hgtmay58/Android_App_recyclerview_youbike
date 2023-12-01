package edu.takming.myapplicationrecyclerview_youbike_new1121;

public class Bike {
    private String name;
    private String address;
    private String totalNumber;
    private String lendNumber;
    private String returnNumber;

    public Bike(String name, String address,
                String totalNumber, String lendNumber,
                String returnNumbe) {
        this.name = name;
        this.address = address;
        this.totalNumber = totalNumber;
        this.lendNumber = lendNumber;
        this.returnNumber = returnNumbe;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getTotalNumber() {
        return totalNumber;
    }

    public String getLendNumber() {
        return lendNumber;
    }

    public String getReturnNumber() {
        return returnNumber;
    }
}
