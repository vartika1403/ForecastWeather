package entertainment.forecastweather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private LocationManager locationManager;
    private Location netLocation = null, gpsLocation = null, finalLocation = null;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        getObservale().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(String s) {
                        Log.d(LOG_TAG, "string s, " + s);

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    private Single<String> getObservale() {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(SingleEmitter<String> emitter) {
                try {
                    isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                    // add gps permission
                   /* if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return "";
                    }*/
                    if (isGPSEnabled)
                        gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (isNetworkEnabled)
                        netLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (gpsLocation != null && netLocation != null) {
                        //smaller the number more accurate result will
                        if (gpsLocation.getAccuracy() > netLocation.getAccuracy())
                            finalLocation = netLocation;
                        else
                            finalLocation = gpsLocation;
                    } else {
                        if (gpsLocation != null) {
                            finalLocation = gpsLocation;
                        } else if (netLocation != null) {
                            finalLocation = netLocation;
                        }
                    }
                    emitter.onSuccess(finalLocation.getProvider());
                } catch (Exception e) {
                    emitter.onError(e);
                    e.printStackTrace();
                }
            }
        });
    }


}
