package Meters.IndividualMeter.Strategies;

import Meters.IndividualMeter.IndividualMeter;

public class PostPaidStrategy implements IndividualMeterStrategy{
    /**
     * @param meter            the meter that is being used
     * @param extraConsumption the extra consumption that is being added
     */
    @Override
    public void handleExtraConsumption(IndividualMeter meter, double extraConsumption) {
        meter.debit(extraConsumption*IndividualMeter.EXTRA_CONSUMPTION_PRICE);
    }

    /**
     * @return the meter's current consumption method
     */
    @Override
    public String toString() {
        return "PostPaid";
    }
}
