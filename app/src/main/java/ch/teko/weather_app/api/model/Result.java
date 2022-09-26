package ch.teko.weather_app.api.model;
import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class Result{
    @SerializedName("station")
    public String station;
    @SerializedName("timestamp")
    public Date timestamp;
    @SerializedName("values")
    public Values values;
}
