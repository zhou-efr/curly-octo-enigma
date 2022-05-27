package Meters.IndividualMeter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class UserFacade {
    private final IndividualMeter meter;

    public UserFacade(IndividualMeter meter) {
        this.meter = meter;
    }

    public void topUp(double amount) {
        meter.topUp(amount);
    }

    public void consume(double amount) {
        try {
            boolean isConsumed = meter.consume(amount);
            if (!isConsumed) {throw new InterruptedException("off limit consumption");}
        } catch (InterruptedException e) {
            meter.getMainMeter().log("Consumption interrupted du to " + e.getMessage());
        }
        meter.getMainMeter().log("Consumption successful");
    }

    public double getBalance() {
        return meter.getBalance();
    }

    @Override
    public String toString() {
        return "Meter for apartment " + meter.getApartmentNumber() + " {" + "balance=" + meter.toPay() + ", " +
                "current consumption=" + meter.getConsumption() + "KWh" +
                '}';
    }

    public void payBill(double amount) {
        if (amount > 0){topUp(amount);}
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        meter.monthlyPayment(localDate.getMonthValue() + "/" + localDate.getYear());
    }
}
