package pl.edu.atar.recruitment.service;

public class DiscountService {

    public double getDiscountedOrderTotal(Double recruitmentFee, Double discount) {
        return recruitmentFee - (recruitmentFee * discount);
    }
}