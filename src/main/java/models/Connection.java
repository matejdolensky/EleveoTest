package models;


import lombok.Getter;
import lombok.Setter;


/**
 * Data class for connection details in BE for deserialization
 */
public class Connection {
    @Getter
    public String id;
    @Getter
    public String departureTime;
    @Getter
    public String arrivalTime;
    @Getter
    public String departureStationId;
    @Getter
    public String arrivalStationId;
    @Getter
    int transfersCount;
    @Getter
    public Double priceFrom;
    @Getter
    public String travelTime;
    public Boolean bookable;

    @Setter
    @Getter
    public int numberOfStops;

    @Getter
    @Setter
    public ConnectionDetail connectionDetail;

}
