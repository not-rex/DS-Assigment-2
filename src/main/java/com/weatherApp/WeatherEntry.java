package com.weatherApp;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class WeatherEntry {
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("state")
    private String state;
    
    @JsonProperty("time_zone")
    private String timeZone;
    
    @JsonProperty("lat")
    private double lat;
    
    @JsonProperty("lon")
    private double lon;
    
    @JsonProperty("local_date_time")
    private String localDateTime;
    
    @JsonProperty("local_date_time_full")
    private String localDateTimeFull;
    
    @JsonProperty("air_temp")
    private double airTemp;
    
    @JsonProperty("apparent_t")
    private double apparentT;
    
    @JsonProperty("cloud")
    private String cloud;
    
    @JsonProperty("dewpt")
    private double dewpt;
    
    @JsonProperty("press")
    private double press;
    
    @JsonProperty("rel_hum")
    private int relHum;
    
    @JsonProperty("wind_dir")
    private String windDir;
    
    @JsonProperty("wind_spd_kmh")
    private int windSpdKmh;
    
    @JsonProperty("wind_spd_kt")
    private int windSpdKt;
    
    // Lamport timestamp and last updated time
    private int timestamp;
    private long lastUpdated;

    // Getters and Setters for all fields

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Repeat for all other fields

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public String getLocalDateTime() {
		return localDateTime;
	}

	public void setLocalDateTime(String localDateTime) {
		this.localDateTime = localDateTime;
	}

	public String getLocalDateTimeFull() {
		return localDateTimeFull;
	}

	public void setLocalDateTimeFull(String localDateTimeFull) {
		this.localDateTimeFull = localDateTimeFull;
	}

	public double getAirTemp() {
		return airTemp;
	}

	public void setAirTemp(double airTemp) {
		this.airTemp = airTemp;
	}

	public double getApparentT() {
		return apparentT;
	}

	public void setApparentT(double apparentT) {
		this.apparentT = apparentT;
	}

	public String getCloud() {
		return cloud;
	}

	public void setCloud(String cloud) {
		this.cloud = cloud;
	}

	public double getDewpt() {
		return dewpt;
	}

	public void setDewpt(double dewpt) {
		this.dewpt = dewpt;
	}

	public double getPress() {
		return press;
	}

	public void setPress(double press) {
		this.press = press;
	}

	public int getRelHum() {
		return relHum;
	}

	public void setRelHum(int relHum) {
		this.relHum = relHum;
	}

	public String getWindDir() {
		return windDir;
	}

	public void setWindDir(String windDir) {
		this.windDir = windDir;
	}

	public int getWindSpdKmh() {
		return windSpdKmh;
	}

	public void setWindSpdKmh(int windSpdKmh) {
		this.windSpdKmh = windSpdKmh;
	}

	public int getWindSpdKt() {
		return windSpdKt;
	}

	public void setWindSpdKt(int windSpdKt) {
		this.windSpdKt = windSpdKt;
	}

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    // Overrides the default equals method to compare WeatherEntry objects.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WeatherEntry that = (WeatherEntry) o;

        return Objects.equals(id, that.id);
    }

    // Overrides the default hashCode method to generate hash.
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Overrides the toString method for better readability.
    @Override
    public String toString() {
        return "WeatherEntry{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", state='" + state + '\'' +
                ", timeZone='" + timeZone + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", localDateTime='" + localDateTime + '\'' +
                ", localDateTimeFull='" + localDateTimeFull + '\'' +
                ", airTemp=" + airTemp +
                ", apparentT=" + apparentT +
                ", cloud='" + cloud + '\'' +
                ", dewpt=" + dewpt +
                ", press=" + press +
                ", relHum=" + relHum +
                ", windDir='" + windDir + '\'' +
                ", windSpdKmh=" + windSpdKmh +
                ", windSpdKt=" + windSpdKt +
                ", timestamp=" + timestamp +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
