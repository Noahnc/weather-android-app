package ch.teko.weather_app.models;

import com.google.gson.annotations.SerializedName;
public class Values{
    @SerializedName("timestamp_cet")
    public TimestampCet timestamp_cet;
    @SerializedName("air_temperature")
    public AirTemperature air_temperature;
}
