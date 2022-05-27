import Meters.IndividualMeter.Strategies.PrePaidStrategy;
import Meters.IndividualMeter.UserFacade;
import Meters.MainMeter.MainMeter;

public class Main {
    public static void main(String[] args) {
        MainMeter mainMeter = MainMeter.getInstance();
        UserFacade user1 = mainMeter.addMeter("A-15-1");
        UserFacade user2 = mainMeter.addMeter("A-21-11");

        user1.consume(10);
        user2.consume(30);
        user2.consume(500);

        mainMeter.monitorMeters();

        mainMeter.log(user1.toString());
        user1.payBill(0);

        mainMeter.log(user2.toString());
        user2.payBill(0);

        mainMeter.monitorMeters();

        UserFacade user3 = mainMeter.addMeter("A-420-3", new PrePaidStrategy(), 190, 10);
        user3.consume(40);

        mainMeter.log(user3.toString());
        user3.payBill(10);

        mainMeter.monitorMeters();

        mainMeter.activeMeter("A-420-3");

        user1.consume(10);
        user2.consume(30);
        user3.consume(20);

        mainMeter.monitorMeterHistory("A-420-3");

        mainMeter.resetAll();
        mainMeter.monitorMeters();
    }
}