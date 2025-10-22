package pl.edu.atar.recruitment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.atar.recruitment.exception.InvalidCreditCardException;

public class CreditCardService {

    Logger LOGGER = LoggerFactory.getLogger(CreditCardService.class);

    public void chargeAmount(String cardNumber, String cvc, String expiryDate, Double amount)
            throws InvalidCreditCardException {
        if (expiryDate.length() == 7) {
            LOGGER.info("Credit card number: {}, CVC: {}, Expiry date: {}, Total amount: {}", cardNumber, cvc, expiryDate, amount);
        } else {
            LOGGER.error("The credit card expiry date is invalid: {}", expiryDate);

            throw new InvalidCreditCardException("Invalid credit card expiry date!");
        }
    }
}