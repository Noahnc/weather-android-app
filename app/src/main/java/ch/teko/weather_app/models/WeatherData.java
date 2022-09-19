package ch.teko.weather_app.models;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class WeatherData{
    @SerializedName("ok")
    public boolean ok;
    @SerializedName("total_count")
    public int total_count;
    @SerializedName("row_count")
    public int row_count;
    @SerializedName("result")
    public ArrayList<Result> result;
}
