package Meters.MainMeter;

import Meters.IndividualMeter.IndividualMeter;
import Meters.IndividualMeter.Strategies.IndividualMeterStrategy;
import Meters.IndividualMeter.UserFacade;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MainMeter {
    private static final MainMeter instance;

    static {
        try {
            instance = new MainMeter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HashMap<String, IndividualMeter> meters = new HashMap<>();
    Logger logger = Logger.getLogger("MainMeterLog");
    FileHandler fh;

    /**
     * Initialize the logger
     * @throws IOException if the logger cannot be created
     */
    private MainMeter () throws IOException {
        fh = new FileHandler("C:\\Users\\thepa\\IdeaProjects\\ElectricityMeter\\logs\\MainMeterLog.log");
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
    }

    /**
     * @return the single instance of MainMeter
     */
    public static MainMeter getInstance() {
        return instance;
    }

    /**
     * Add a meter to the list of meter managed by the main meter
     * @param name the name of the meter
     * @return the user facade for the meter
     */
    public UserFacade addMeter(String name) {
        return addMeter(name, new IndividualMeter(this, name));
    }

    /**
     * Add a meter to the list of meter managed by the main meter
     * @param name the name of the meter
     * @param strategy the payment strategy to use
     * @return the user facade for the meter
     */
    public UserFacade addMeter(String name, IndividualMeterStrategy strategy) {
        return addMeter(name, new IndividualMeter(this, name, strategy, 0.0, 0.0));
    }

    /**
     * constructor for testing purposes
     * @param name the name of the meter
     * @param strategy the payment strategy to use
     * @param consumption the consumption of the meter
     * @param balance the balance of the meter
     * @return the user facade for the meter
     */
    public UserFacade addMeter(String name, IndividualMeterStrategy strategy, double consumption, double balance) {
        return addMeter(name, new IndividualMeter(this, name, strategy, consumption, balance));
    }

    /**
     * Add a meter to the list of meter managed by the main meter
     * @param name the name of the meter
     * @param meter the IndividualMeter to add
     */
    public UserFacade addMeter(String name, IndividualMeter meter) {
        meters.put(name, meter);
        log("Meter for apartment " + name + " added");
        return new UserFacade(meter);
    }

    /**
     * @param name the name of the meter
     * @return the meter with the given name
     */
    public IndividualMeter getMeter(String name) {
        return meters.get(name);
    }

    /**
     * @param name the name of the meter to active
     */
    public void activeMeter(String name) {
        meters.get(name).setActive(true);
        log("Meter for apartment " + name + " activated");
    }

    /**
     * @param name the name of the meter to deactivate
     */
    public void deactivateMeter(String name) {
        meters.get(name).setActive(false);
        log("Meter for apartment " + name + " deactivated");
    }

    /**
     * Remove a meter from the list of meter managed by the main meter
     * @param name the name of the meter to remove
     */
    public void removeMeter(String name) {
        meters.remove(name);
        log("Meter for apartment " + name + " removed");
    }


    /**
     * @param name the name of the meter
     * @param strategy the payment strategy to use
     */
    public void changeStrategy(String name, IndividualMeterStrategy strategy) {
        IndividualMeter meter = meters.get(name);
        meter.setStrategy(strategy);
        log("Strategy for apartment " + name + " changed");
    }

    /**
     * Reset the main meter
     */
    public void reset() {
        meters.clear();
        log("Main meter reset");
    }

    /**
     * Entirely reset the given meter
     * @param name the name of the meter
     */
    public void reset(String name) {
        meters.get(name).reset();
        log("Meter for apartment " + name + " reset");
    }

    /**
     * @return the list of meters managed by the main meter
     */
    public HashMap<String, IndividualMeter> getMeters() {
        return meters;
    }

    /**
     * reset all the meters managed by the main meter
     */
    public void resetAll() {
        for (String meter : meters.keySet()) {
            reset(meter);
        }
    }

    /**
     * @param name the name of the meter to display history
     */
    public void monitorMeterHistory(String name) {
        meters.get(name).displayHistory();
    }

    /**
     * display all the meters managed by the main meter
     */
    public void monitorMeters() {
        for (IndividualMeter meter : meters.values()) {
            log(meter.toString());
        }
        log("total consumption: " + getTotalConsumption() + " kWh");
    }

    /**
     * @return the total consumption of all the meters managed by the main meter
     */
    public double getTotalConsumption() {
        double total = 0;
        for (IndividualMeter meter : meters.values()) {
            total += meter.getConsumption();
        }
        return total;
    }

    /**
     * add the given message to the log
     * @param s the string to log
     */
    public void log(String s) {
        logger.info("\033[0;32m"+s+"\033[0m");
    }

    /**
     * add the given message to the log
     * @param s the string to log
     */
    public void warning(String s) {
        logger.warning("\033[0;33m"+s+"\033[0m");
    }
}
