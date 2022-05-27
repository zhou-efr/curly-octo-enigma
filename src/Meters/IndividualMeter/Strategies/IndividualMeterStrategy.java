package Meters.IndividualMeter.Strategies;

import Meters.IndividualMeter.IndividualMeter;

public interface IndividualMeterStrategy {
    public void handleExtraConsumption(IndividualMeter meter, double extraConsumption) throws InterruptedException;
}
