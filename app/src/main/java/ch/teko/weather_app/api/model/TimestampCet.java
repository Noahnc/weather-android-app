package ch.teko.weather_app.api.model;
import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class TimestampCet{
    @SerializedName("values")
    public Date value;
    @SerializedName("unit")
    public String unit;
    @SerializedName("status")
    public String status;
}
