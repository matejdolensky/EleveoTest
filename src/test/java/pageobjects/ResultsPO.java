package pageobjects;

import com.codeborne.selenide.*;
import data.ConnectionDto;
import enums.TestCaseEnum;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selectors.by;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

@Slf4j
public class ResultsPO {

    @Setter
    public static String dayOfConnection;


    public List<ConnectionDto> findRoute(TestCaseEnum connectionType) {
        List<ConnectionDto> allAvailableConnections = getAllAvailableConnections();
        switch (connectionType) {
            case FASTEST_ARRIVAL -> {
                String fastestArrivalTime = allAvailableConnections.stream().sorted(Comparator.comparing(ConnectionDto::getArrivalTime)).collect(Collectors.toList()).get(0).getArrivalTime();
                return allAvailableConnections.stream().filter(connection -> connection.getArrivalTime().equals(fastestArrivalTime)).collect(Collectors.toList());
            }
            case SHORTEST_TIME -> {
                String shortestTime = allAvailableConnections.stream().sorted(Comparator.comparing(ConnectionDto::getTravelTime)).collect(Collectors.toList()).get(0).getTravelTime();
                return allAvailableConnections.stream().filter(connection -> connection.getTravelTime().equals(shortestTime)).collect(Collectors.toList());
            }
            case CHEAPEST -> {
                int cheapestPrice = allAvailableConnections.stream()
                        .sorted(Comparator.comparingInt(ConnectionDto::getPrice))
                        .collect(Collectors.toList()).get(0).getPrice();
                return allAvailableConnections.stream().filter(connection -> connection.getPrice() == cheapestPrice).collect(Collectors.toList());
            }
            default -> {
                log.error("Unsupported connection type search!");
                return null;
            }
        }
    }


    private List<ConnectionDto> getAllAvailableConnections() {
        $$(".h-full.pb-1\\.5.flex.flex-col.justify-between").shouldBe(CollectionCondition.sizeGreaterThan(0), 10000);

        List<ConnectionDto> results = new ArrayList<>();

        List<SelenideElement> elements = $$(".h-full.pb-1\\.5.flex.flex-col.justify-between");
        elements.forEach(selenideElement -> {
            selenideElement = selenideElement.parent();

            ConnectionDto connection = new ConnectionDto();
            selenideElement.scrollTo();


            if (!selenideElement.$(byText("Vypredané")).isDisplayed()) {
                selenideElement.$("svg", 0).click();
                connection.setPrice(Integer.parseInt(selenideElement.$("button").getText().replace("od", "").replace("CZK", "").replaceAll("\\s", "")));

                connection.setTravelTime(selenideElement.$("span").getOwnText().split(" ")[0]);
                connection.setIsDirect(selenideElement.$(byText("Priame spojenie")).exists());

                if (connection.getIsDirect()) {
                    SelenideElement clientCard = selenideElement.$(".cardOpenTransferContainer.cardOpenTransferContainer-first");

                    clientCard.$(".cardOpenTransfer.font-bold.sm\\:text-14.self-center").shouldBe(Condition.visible);

                    List<SelenideElement> connectionDetails = clientCard.$$(".cardOpenTransfer.font-bold.sm\\:text-14");

                    connection.setConnectionDate(clientCard.$(".col-span-3.font-bold.sm\\:text-14.hidden").getOwnText());

                    connection.setDepartureTime(connectionDetails.get(0).getOwnText());
                    connection.setDepartureStationName(connectionDetails.get(1).getOwnText().replace("\n", "").replace("\r", ""));

                    connection.setArrivalTime(connectionDetails.get(connectionDetails.size() - 2).getOwnText());
                    connection.setArrivalStationName(connectionDetails.get(connectionDetails.size() - 1).getOwnText().replace("\n", "").replace("\r", ""));
                } else {
                    selenideElement.$$(".cardOpenTransferContainer.cardOpenTransferContainer").shouldHave(CollectionCondition.sizeGreaterThanOrEqual(2));
                    List<SelenideElement> clientCards = selenideElement.$$(".cardOpenTransferContainer.cardOpenTransferContainer");
                    List<SelenideElement> departureDetails = clientCards.get(0).$$(".cardOpenTransfer.font-bold.sm\\:text-14");
                    List<SelenideElement> arrivalDetails = clientCards.get(clientCards.size() - 1).$$(".cardOpenTransfer.font-bold.sm\\:text-14");

                    connection.setConnectionDate(clientCards.get(0).$(".col-span-3.font-bold.sm\\:text-14.hidden").getOwnText());

                    connection.setDepartureTime(departureDetails.get(0).getOwnText());
                    connection.setDepartureStationName(departureDetails.get(1).getOwnText().replace("\n", "").replace("\r", ""));

                    connection.setArrivalTime(arrivalDetails.get(arrivalDetails.size() - 2).getOwnText());
                    connection.setArrivalStationName(arrivalDetails.get(arrivalDetails.size() - 1).getOwnText().replace("\n", "").replace("\r", ""));


                }

                List<SelenideElement> connections = selenideElement.$$(".text-13.lg\\:text-14.group-hover\\:underline");

                connections.forEach(element -> {
                    element.click();
                    SelenideElement wrapper = $(".modal-wrapper");
                    wrapper.shouldBe(Condition.visible);
                    int numberOfStops = wrapper.$$(".flex-auto.h-4.flex.items-center.text-primary-blue").size() - 1;
                    connection.setNumberOfStops(connection.getNumberOfStops() + numberOfStops);
                    $(by("aria-label", "Zavřít okno.")).click();
                });
                selenideElement.$("svg", 0).click();
                results.add(connection);
            } else {
                log.info("Connection is not available.");
            }
        });

        return results.stream().filter(connection -> connection.getDepartureStationName().contains(Utils.departureCityName))
                .filter(connection -> connection.getArrivalStationName().contains(Utils.arrivalCityName))
                .filter(connection -> connection.getConnectionDate().contains(Utils.connectionDate)).collect(Collectors.toList());
    }
}
