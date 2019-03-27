package entertainment.forecastweather;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("weather")
    Call<JsonElement> getCityWeather(@Query("q") String cityName, @Query("appid") String appid, @Query("units") String unit);
}
