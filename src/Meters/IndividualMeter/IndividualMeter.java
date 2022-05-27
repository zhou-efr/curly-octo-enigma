package Meters.IndividualMeter;

import Meters.IndividualMeter.Strategies.IndividualMeterStrategy;
import Meters.IndividualMeter.Strategies.PostPaidStrategy;
import Meters.IndividualMeter.Strategies.PrePaidStrategy;
import Meters.MainMeter.MainMeter;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

/**
 * @param Date Month and year of the record
 * @param consumption Consumption of the meter in the month
 * @param balance Balance of the meter at the end of the month
 */
record MeterRecord(String Date, double consumption, double balance) {}

/**
 * This class represents an individual meter.
 */
public class IndividualMeter {
    public static final double CONSUMPTION_LIMIT = 1000;
    public static final double BASIC_CONSUMPTION_LIMIT = 200;
    public static final double CONSUMPTION_SPIKE = 50;
    public static final double EXTRA_CONSUMPTION_PRICE = 0.5;

    private IndividualMeterStrategy strategy;
    private MainMeter mainMeter;
    private double consumption;
    private boolean active;
    private double balance;
    private final Stack<MeterRecord> history;
    private final String apartmentNumber;

    /**
     * Default individual meter is postpaid.
     */
    public IndividualMeter(MainMeter mainMeter, String apartmentNumber) {
        this(mainMeter, apartmentNumber, new PostPaidStrategy(), 0.0, 0.0);
    }

    /**
     * @param strategy payment strategy to be set
     * @param mainMeter main meter to be set
     */
    public IndividualMeter(MainMeter mainMeter, String apartmentNumber, IndividualMeterStrategy strategy, double consumption, double balance) {
        this.strategy = strategy;
        this.mainMeter = mainMeter;
        this.active = true;
        this.balance = balance;
        this.consumption = consumption;
        this.history = new Stack<>();
        this.apartmentNumber = apartmentNumber;
    }

    /**
     * @param strategy payment strategy to be set
     */
    public void setStrategy(IndividualMeterStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * @return how much the meter is charged
     */
    public String toPay() {
        if (getBalance() < 0) {
            return -1*getBalance() + "RM to pay";
        }else {
            return getBalance() + "RM left";
        }
    }

    /**
     * Reset the meter for the month and store the meter state to the history
     * if it's the fifth (or more) month the user is in debt the meter switches to prepaid
     * @param date date of the record
     */
    public void monthlyPayment(String date) {
        history.add(new MeterRecord(date, consumption, balance > 0 ? 0 : balance));
        mainMeter.log("Individual meter " + apartmentNumber + " was reset for the month " + date + " with a balance of " + toPay() + " and a consumption of " + consumption);
        consumption = 0;

        if (balance < 0) {
            boolean debt = true;
            int i = 0;
            Iterator<MeterRecord> it = history.iterator();
            while (it.hasNext() && i < 5) {
                MeterRecord record = it.next();
                if (record.balance() >= 0) {
                    debt = false;
                }
                i++;
            }
            if (debt) {
                setStrategy(new PrePaidStrategy());
            }
        }
    }

    /**
     * @param consumed amount of consumed energy
     * @throws InterruptedException if the meter intercept a consumption spike
     */
    public boolean consume(double consumed) throws InterruptedException {
        if (!active) {throw new InterruptedException("disabled meter");}
        if (consumed > CONSUMPTION_SPIKE){
            mainMeter.warning("Individual meter " + apartmentNumber + " was interrupted for the consumption of " + consumed + " kWh");
            temporaryDisable();
        }

        setConsumption(consumption + consumed);
        if (consumption > BASIC_CONSUMPTION_LIMIT){
            strategy.handleExtraConsumption(this, consumed);
        }

        return active;
    }

    /**
     * disables the meter for 5 minutes (1 second for testing purposes)
     * @throws InterruptedException if the meter intercept a consumption spike
     */
    private void temporaryDisable() throws InterruptedException {
        active = false;
        mainMeter.warning("Meter disabled due to consumption spike");
//        Thread.sleep(1000);
        mainMeter.warning("Meter enabled");
        active = true;
    }

    /**
     * @param amount amount to be added to the balance
     */
    public void topUp(double amount){
        balance += amount;
        mainMeter.log("Individual meter " + apartmentNumber + " was topped up with " + amount + " and the balance is now " + balance);
    }

    /**
     * @param amount amount to be paid
     */
    public void debit(double amount){
        balance -= amount;
    }

    /**
     * @return the consumption
     */
    public double getConsumption() {
        return consumption;
    }

    /**
     * Updates the consumption, and disables the meter if the consumption is over the limit.
     * @param consumption the consumption to set
     */
    private void setConsumption(double consumption) {
        if (consumption > CONSUMPTION_LIMIT) {
            active = false;
            mainMeter.warning("Individual meter " + apartmentNumber + " was disabled for exceeding the consumption limit");
        }
        this.consumption = consumption;
    }

    /**
     * @return the balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return the mainMeter
     */
    public MainMeter getMainMeter() {
        return mainMeter;
    }

    /**
     * @param mainMeter the mainMeter to set
     */
    public void setMainMeter(MainMeter mainMeter) {
        this.mainMeter = mainMeter;
    }

    /**
     * @return the history of consumption and payments
     */
    public Stack<MeterRecord> getHistory() {
        return history;
    }

    /**
     * @return the apartmentNumber
     */
    public String getApartmentNumber() {
        return apartmentNumber;
    }

    @Override
    public String toString() {
        return "IndividualMeter{" + "strategy=" + strategy +
                ", consumption=" + consumption  + " kWh" +
                ", status=" + (active ? "active" : "disabled") +
                ", balance=" + toPay() +
                ", apartmentNumber='" + apartmentNumber + '\'' +
                '}';
    }

    /**
     * display the history of consumption and payments
     */
    public void displayHistory(){
        StringBuilder s = new StringBuilder();
        s.append("History of consumption and payments for individual meter ").append(apartmentNumber).append(":\n");
        for (MeterRecord record : history){
            s.append(record.toString()).append("\n");
        }
        mainMeter.log(s.toString());
    }

    /**
     * Reset the meter to original state (including consumption, balance and strategy)
     */
    public void reset() {
        consumption = 0;
        balance = 0;
        strategy = new PostPaidStrategy();
    }
}
