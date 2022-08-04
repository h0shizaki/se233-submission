package se233.chapter2.model;

public class CurrencyEntity {
    private Double rate ;
    private String date ;

    public CurrencyEntity(Double rate , String date ) {
        this.rate = rate;
        this.date = date;
    }

    //Getter and Setter
    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return String.format("%s %.4f",date,rate) ;
    }
}
