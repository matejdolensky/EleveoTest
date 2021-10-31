package runners;

import data.ConnectionDto;
import enums.TestCaseEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
import pageobjects.MainPagePO;
import pageobjects.ResultsPO;
import testbase.TestBase;
import utils.Utils;

import java.io.IOException;
import java.util.List;

import static com.codeborne.selenide.Selenide.open;

@Slf4j
class RouteFinderFETest extends TestBase {

    String nearestMondayMonth;
    String nearestMondayDay;
    private static final String BRNO = "Brno";
    private static final String OSTRAVA = "Ostrava";


    @BeforeEach
    public void openUrl() {
        open(getProperties().getProperty("fe.url"));
        nearestMondayMonth = Utils.findNearestMondayMonthAndYear();
        nearestMondayDay = Utils.findNearestMondayDay();
        Utils.arrivalCityName = BRNO;
        Utils.departureCityName = OSTRAVA;
    }

    @Test
    void findFastestArrivalConnections() {
        ResultsPO resultsPO = new MainPagePO().searchForConnections(nearestMondayDay, nearestMondayMonth, OSTRAVA, BRNO);
        List<ConnectionDto> results = resultsPO.findRoute(TestCaseEnum.FASTEST_ARRIVAL);
        Assertions.assertTrue(results.size() >= 1);
        log.info("Fastest arrival connection " + ((results.size() == 1) ? "is : " : "are : "));
        results.forEach(result ->
                log.info("Departure time: " + result.getDepartureTime() +
                        " Arrival time: " + result.getArrivalTime() +
                        " Travel time: " + result.getTravelTime() +
                        " From : " + result.getDepartureStationName() +
                        " To : " + result.getArrivalStationName() +
                        " For: " + result.getPrice() + " CZK " +
                        " Through : " + result.getNumberOfStops() + " stops."));
    }

    @Test
    void findShortestConnections() {
        ResultsPO resultsPO = new MainPagePO().searchForConnections(nearestMondayDay, nearestMondayMonth, OSTRAVA, BRNO);
        List<ConnectionDto> results = resultsPO.findRoute(TestCaseEnum.SHORTEST_TIME);
        Assertions.assertTrue(results.size() >= 1);
        log.info("Shortest connection " + ((results.size() == 1) ? "is : " : "are : "));
        results.forEach(result ->
                log.info("Departure time: " + result.getDepartureTime() +
                        " Arrival time: " + result.getArrivalTime() +
                        " Travel time: " + result.getTravelTime() +
                        " From : " + result.getDepartureStationName() +
                        " To : " + result.getArrivalStationName() +
                        " For: " + result.getPrice() + " CZK " +
                        " Through : " + result.getNumberOfStops() + " stops."));
    }

    @Test
    void findCheapestConnections() {
        ResultsPO resultsPO = new MainPagePO().searchForConnections(nearestMondayDay, nearestMondayMonth, OSTRAVA, BRNO);
        List<ConnectionDto> results = resultsPO.findRoute(TestCaseEnum.CHEAPEST);
        Assertions.assertTrue(results.size() >= 1);
        log.info("Cheapest connection " + ((results.size() == 1) ? "is : " : "are : "));
        results.forEach(result ->
                log.info("Departure time: " + result.getDepartureTime() +
                        " Arrival time: " + result.getArrivalTime() +
                        " Travel time: " + result.getTravelTime() +
                        " From : " + result.getDepartureStationName() +
                        " To : " + result.getArrivalStationName() +
                        " For: " + result.getPrice() + " CZK " +
                        " Through : " + result.getNumberOfStops() + " stops."));
    }
}
