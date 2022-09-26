package ch.teko.weather_app.api.model;
import com.google.gson.annotations.SerializedName;

public class AirTemperature{
    @SerializedName("value")
    public double value;
    @SerializedName("unit")
    public String unit;
    @SerializedName("status")
    public String status;
}
