package ch.teko.weather_app.models;
import com.google.gson.annotations.SerializedName;

public class AirTemperature{
    @SerializedName("value")
    public double value;
    @SerializedName("unit")
    public String unit;
    @SerializedName("status")
    public String status;
}
