package data;

import lombok.Data;

@Data
public class ConnectionDto {
    private String departureTime;
    private String arrivalTime;
    private String travelTime;
    private int price;
    private Boolean isDirect;
    private int numberOfStops;
    private String connectionDate;
    private String departureStationName;
    private String arrivalStationName;

}
