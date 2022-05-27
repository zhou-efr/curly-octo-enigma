package Meters.IndividualMeter.Strategies;

import Meters.IndividualMeter.IndividualMeter;

public class PrePaidStrategy implements IndividualMeterStrategy{

    /**
     * @param meter the meter to be charged
     * @param extraConsumption the extra consumption to be added to the meter's consumption
     * @throws InterruptedException if not enough funds are available
     */
    @Override
    public void handleExtraConsumption(IndividualMeter meter, double extraConsumption) throws InterruptedException {
        meter.debit(extraConsumption*IndividualMeter.EXTRA_CONSUMPTION_PRICE);
        if (meter.getBalance() < 0) {
            meter.setActive(false);
            meter.getMainMeter().warning("Meter " + meter.getApartmentNumber() + " is deactivated due to insufficient funds");
            throw new InterruptedException("insufficient funds");
        }
    }

    /**
     * @return the meter's current consumption method
     */
    @Override
    public String toString() {
        return "PrePaidStrategy";
    }
}
