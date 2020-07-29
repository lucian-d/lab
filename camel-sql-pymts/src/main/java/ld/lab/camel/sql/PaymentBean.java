package ld.lab.camel.sql;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Bean that generates and processes payments.
 */
public class PaymentBean {

    private int counter = 100;
    private Random ran = new Random();

    /**
     * Generates a new payment structured as a {@link Map}
     */
    public Map<String, Object> generatePayment() {
        Map<String, Object> booking = new HashMap<>();
        booking.put("id", counter++);
        booking.put("debit", counter % 2 == 0 ? "Bob" : "Joe");
        booking.put("amount", ran.nextInt(100));
        booking.put("credit", counter % 2 == 0 ? "Tim" : "Ana");
        return booking;
    }

    /**
     * Check the payment for froud.
     *
     * @param data  the payment as a {@link Map}
     * @return the investigation result.
     */
    public String checkFraud(Map<String, Object> data) {
        int amount = (int) data.get("amount");
        String answer = (amount > 50)?"IS fraud":"IS NOT fraud";
        return "Payment #" + data.get("id") + " from " + data.get("debit") + " to " + data.get("credit") + " of " + data.get("amount") + "GBP " + answer;
    }
}
