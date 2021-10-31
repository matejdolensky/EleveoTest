package runners;

import enums.TestCaseEnum;
import io.restassured.http.ContentType;
import io.restassured.internal.RequestSpecificationImpl;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import models.Connection;
import models.ConnectionDetail;
import models.Station;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testbase.TestBase;
import utils.Utils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
class RouteFinderBETest extends TestBase {

    Response response;
    String departureDate;
    private static final String BRNO = "Brno";
    private static final String OSTRAVA = "Ostrava";

    @BeforeEach
    void setDepartureDate() {
        departureDate = Utils.findNearestMondayForBE();
        getRequest().given().contentType(ContentType.JSON);
        getRequest().param("tariffs", "REGULAR")
                .param("toLocationType", "CITY")
                .param("toLocationId", getProperties().getProperty("api.brno.cityId"))
                .param("fromLocationType", "CITY")
                .param("fromLocationId", getProperties().getProperty("api.ostrava.cityId"));
        getRequest().param("departureDate", departureDate);
        response = getRequest().get("/routes/search/simple");
    }

    @Test
    void findCheapestConnection() {
        response.then().assertThat().statusCode(200);

        List<Connection> results = findConnection(TestCaseEnum.CHEAPEST, findAvailableConnections());

        assert results != null;
        Assertions.assertTrue(results.size() >= 1);

        log.info("Cheapest connection " + ((results.size() == 1) ? "is : " : "are : "));
        results.forEach(result ->
                log.info("Departure time: " + getTimeInString(result.departureTime) +
                        " Arrival time: " + getTimeInString(result.arrivalTime) +
                        " Travel time: " + result.travelTime +
                        " From : " + result.getConnectionDetail().departureCityName + ", " + result.getConnectionDetail().departureStationName +
                        " To : " + result.getConnectionDetail().arrivalCityName + ", " + result.getConnectionDetail().arrivalStationName +
                        " For: " + result.priceFrom + " EUR " +
                        " Through : " + result.getNumberOfStops() + " stops."));
    }

    @Test
    void findFastestArrivalConnections() {
        response.then().assertThat().statusCode(200);

        List<Connection> results = findConnection(TestCaseEnum.FASTEST_ARRIVAL, findAvailableConnections());

        assert results != null;
        Assertions.assertTrue(results.size() >= 1);

        log.info("Fastest arrival connection " + ((results.size() == 1) ? "is : " : "are : "));
        results.forEach(result ->
                log.info("Departure time: " + getTimeInString(result.departureTime) +
                        " Arrival time: " + getTimeInString(result.arrivalTime) +
                        " Travel time: " + result.travelTime +
                        " From : " + result.getConnectionDetail().departureCityName + ", " + result.getConnectionDetail().departureStationName +
                        " To : " + result.getConnectionDetail().arrivalCityName + ", " + result.getConnectionDetail().arrivalStationName +
                        " For: " + result.priceFrom + " EUR " +
                        " Through : " + result.getNumberOfStops() + " stops."));
    }

    @Test
    void findShortestConnections() {
        response.then().assertThat().statusCode(200);

        List<Connection> results = findConnection(TestCaseEnum.SHORTEST_TIME, findAvailableConnections());

        assert results != null;
        Assertions.assertTrue(results.size() >= 1);

        log.info("Shortest connection " + ((results.size() == 1) ? "is : " : "are : "));
        results.forEach(result ->
                log.info("Departure time: " + getTimeInString(result.departureTime) +
                        " Arrival time: " + getTimeInString(result.arrivalTime) +
                        " Travel time: " + result.travelTime +
                        " From : " + result.getConnectionDetail().departureCityName + ", " + result.getConnectionDetail().departureStationName +
                        " To : " + result.getConnectionDetail().arrivalCityName + ", " + result.getConnectionDetail().arrivalStationName +
                        " For: " + result.priceFrom + " EUR " +
                        " Through : " + result.getNumberOfStops() + " stops."));
    }


    /**
     * Finds all connections via api, removes connections which does not meets input criteria (not direct, not bookable, not on desired date)
     * then finds all other details such as number of stops
     *
     * @return list of available connections
     */
    private List<Connection> findAvailableConnections() {
        List<Connection> foundConnections = response.jsonPath().getList("routes", Connection.class);
        Assertions.assertTrue(foundConnections.size() >= 1); // at least 1 connection found
        foundConnections = foundConnections.stream()
                .filter(connection -> connection.bookable)
                .filter(connection -> connection.departureTime.contains(departureDate))
                .filter(connection -> connection.getTransfersCount() == 0)
                .collect(Collectors.toList());

        ((RequestSpecificationImpl) getRequest()).removeParam("toLocationType").removeParam("toLocationId").removeParam("fromLocationType").removeParam("fromLocationId").removeParam("departureDate");

        foundConnections.forEach(connection -> {
            connection.setConnectionDetail(
                    getRequest().param("routeId", connection.id)
                            .param("fromStationId", connection.departureStationId)
                            .param("toStationId", connection.arrivalStationId)
                            .get("/routes/" + connection.id + "/simple").jsonPath()
                            .getObject("", ConnectionDetail.class));

            ((RequestSpecificationImpl) getRequest()).removeParam("routeId").removeParam("fromStationId").removeParam("toStationId");

            List<Station> stations = getRequest().get("/consts/timetables/" + connection.id).jsonPath().getList("stations", Station.class);
            int firstStationIndex = 0;
            int lastStationIndex = 0;
            for (int i = 0; i < stations.size(); i++) {
                if (stations.get(i).stationId.equals(connection.departureStationId)) {
                    firstStationIndex = i;
                }
                if (stations.get(i).stationId.equals(connection.arrivalStationId)) {
                    lastStationIndex = i;
                }
            }

            connection.setNumberOfStops(lastStationIndex - firstStationIndex);

        });

        foundConnections = foundConnections.stream()
                .filter(connection -> connection.getConnectionDetail().departureCityName.contains(OSTRAVA))
                .filter(connection -> connection.getConnectionDetail().arrivalCityName.contains(BRNO))
                .collect(Collectors.toList());

        return foundConnections;
    }


    /**
     * Method for finding connections as per TestCaseEnum (fastest, cheapest, shortest time)
     *
     * @param connectionType   - Enum of test connection type
     * @param foundConnections - all available connections
     * @return - list of results
     */
    private List<Connection> findConnection(TestCaseEnum connectionType, List<Connection> foundConnections) {
        switch (connectionType) {
            case FASTEST_ARRIVAL -> {
                String fastestArrivalTime = foundConnections.stream().sorted(Comparator.comparing(Connection::getArrivalTime)).collect(Collectors.toList()).get(0).getArrivalTime();
                return foundConnections.stream().filter(connection -> connection.getArrivalTime().equals(fastestArrivalTime)).collect(Collectors.toList());
            }
            case SHORTEST_TIME -> {
                String shortestTime = foundConnections.stream().sorted(Comparator.comparing(Connection::getTravelTime)).collect(Collectors.toList()).get(0).getTravelTime();
                return foundConnections.stream().filter(connection -> connection.getTravelTime().equals(shortestTime)).collect(Collectors.toList());
            }
            case CHEAPEST -> {
                Double cheapestPrice = foundConnections.stream()
                        .sorted(Comparator.comparingDouble(Connection::getPriceFrom))
                        .collect(Collectors.toList()).get(0).getPriceFrom();
                return foundConnections.stream().filter(connection -> Objects.equals(connection.getPriceFrom(), cheapestPrice)).collect(Collectors.toList());
            }
            default -> {
                log.error("Unsupported connection type search!");
                return null;
            }
        }
    }

    private String getTimeInString(String dateTimeFormat) {
        return LocalDateTime.parse(dateTimeFormat.split("\\+")[0]).toLocalTime().toString();
    }
}
