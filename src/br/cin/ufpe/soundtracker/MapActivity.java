package br.cin.ufpe.soundtracker;

import in.ubee.android.view.IndoorMapView;
import in.ubee.api.Ubee;
import in.ubee.api.exception.RetailMapImageInvalidException;
import in.ubee.api.exception.RetailMapsUnavailableException;
import in.ubee.api.exception.UbeeAPIException;
import in.ubee.api.location.LocationError;
import in.ubee.api.maps.OnMapsLocationListener;
import in.ubee.api.models.Location;
import in.ubee.api.ui.listener.OnMapViewLoadListener;
import in.ubee.models.Retail;
import in.ubee.models.RetailMap;

import java.text.DecimalFormat;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import br.cin.ufpe.soundtracker.utils.Configurations;
import br.cin.ufpe.soundtracker.views.LoadingDialog;
import br.cin.ufpe.soundtracker.views.LocationStateWidget;
import br.cin.ufpe.soundtracker.views.LocationStateWidget.LocationState;
import br.cin.ufpe.soundtracker.views.MeasurePointView;

public class MapActivity extends Activity implements OnClickListener, OnMapsLocationListener, MicrophoneInputListener {
    public static final String TAG = MapActivity.class.getSimpleName();

    private Retail mRetail;

    private LoadingDialog mDialog;

    private IndoorMapView mMapView;
    private MeasurePointView mMeasurePointView;
    private LocationStateWidget mLocatingView;

    private TextView mDbTextView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.map_activity);

        mRetail = Configurations.getDefaultRetail();

        ActionBar actionbar = this.getActionBar();
        actionbar.setTitle("MapActivity");

        mDialog = new LoadingDialog(this);
        mDbTextView = (TextView) findViewById(R.id.current_sound_value_text_view);

        mMapView = (IndoorMapView) findViewById(R.id.map_fragment_indoor_map_view);
        mMapView.setCloseRouteGravity(Gravity.LEFT | Gravity.BOTTOM, 0, 0);

        mMicInput = new MicrophoneInput(this);
        mMicInput.setSampleRate(mSampleRate);
        mMicInput.setAudioSource(mAudioSource);
        mMicInput.start();

        if (!mMapView.isRetailLoaded()) {
            mDialog.show();
            mMeasurePointView = new MeasurePointView(this);

            mMapView.setRetail(mRetail, new OnMapViewLoadListener() {

                @Override
                public void onRetailLoadFinished(Retail retail, List<RetailMap> retailMaps) {
                    mMapView.setRetailMap(retailMaps.get(0));
                }

                @Override
                public void onRetailMapLoadFinished(RetailMap retailMap) {
                    mMapView.removeView(mMeasurePointView);
                    mDialog.dismiss("");
                    mMapView.addView(mMeasurePointView);
                }

                @Override
                public void onLoadError(UbeeAPIException error) {
                    if (error instanceof RetailMapsUnavailableException || error instanceof RetailMapImageInvalidException) {
                        Log.w(TAG, error);
                        Toast.makeText(MapActivity.this, error.getClass() + ": " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        this.mLocatingView = (LocationStateWidget) this.findViewById(R.id.map_activity_location_button);
        this.mLocatingView.setOnClickListener(this);

        this.mLocatingView.setLocationInitializating();

        this.mLocatingView.setSelected(false);

    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_activity_menu, menu);
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == R.id.map_activity_recording_menu_item) {
            //TODO Começar a adicionar os valores medidos no mapa.
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onClick(final View v) {
        if (v == mLocatingView) {
            if (mLocatingView.getLocatingState() == LocationState.LOCATION_UNAVAILABLE) {
                mLocatingView.setLocationInitializating();
                try {
                    Ubee.requestLocationOnMaps(this, this);
                } catch (UbeeAPIException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Ubee.registerLocationCallback(this, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Ubee.unregisterLocationCallback(this, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showLocationErrorToast() {
        Toast.makeText(this, R.string.map_activity_error_message_location_unavailable, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null && location.getRetailId() != null && this.mRetail.getId().equals(location.getRetailId())) {
            mLocatingView.setLocationAvailableProcessing();
            mMapView.setUserLocation(location);

        } else {
            this.onError(LocationError.UNAVAILABLE);
        }

    }

    @Override
    public void onError(LocationError error) {
        if (error == null) {
            error = LocationError.UNAVAILABLE;
        }

        if (error.equals(LocationError.NETWORK_UNAVAILABLE)) {
            if (mLocatingView.getLocatingState() != LocationState.LOCATION_UNAVAILABLE) {
                Toast.makeText(this, R.string.map_activity_error_message_location_unavailable_network_cause, Toast.LENGTH_LONG).show();
                mLocatingView.setLocationUnavailable();
                mMapView.setUserLocation(null);
            }

        } else if (error.equals(LocationError.TEMPORARY_UNAVAILABLE)) {
            mLocatingView.setLocationNotFound();
            mMapView.setUserLocation(null);

        } else if (error.equals(LocationError.WIFI_UNAVAILABLE)) {
            if (mLocatingView.getLocatingState() != LocationState.LOCATION_UNAVAILABLE) {
                Toast.makeText(this, R.string.map_activity_error_message_location_unavailable_wifi_cause, Toast.LENGTH_LONG).show();
                mLocatingView.setLocationUnavailable();
                mMapView.setUserLocation(null);
            }

        } else if (error.equals(LocationError.UNAVAILABLE) || error.equals(LocationError.UNAUTHORIZED)) {
            if (mLocatingView.getLocatingState() != LocationState.LOCATION_UNAVAILABLE) {
                mLocatingView.setLocationUnavailable();
                mMapView.setUserLocation(null);
                showLocationErrorToast();
            }
        }
    }

    MicrophoneInput mMicInput; // The micInput object provides real time audio.

    double mOffsetdB = 10; // Offset for bar, i.e. 0 lit LEDs at 10 dB.
    // The Google ASR input requirements state that audio input sensitivity
    // should be set such that 90 dB SPL at 1000 Hz yields RMS of 2500 for
    // 16-bit samples, i.e. 20 * log_10(2500 / mGain) = 90.
    double mGain = 2500.0 / Math.pow(10.0, 90.0 / 20.0);
    // For displaying error in calibration.
    double mDifferenceFromNominal = 0.0;
    double mRmsSmoothed; // Temporally filtered version of RMS.
    double mAlpha = 0.9; // Coefficient of IIR smoothing filter for RMS.

    private int mSampleRate = 8000; // The audio sampling rate to use.
    private int mAudioSource = MediaRecorder.AudioSource.VOICE_RECOGNITION;

    // Variables to monitor UI update and check for slow updates.
    private volatile boolean mDrawing;

    @Override
    public void processAudioFrame(short[] audioFrame) {
        if (!mDrawing) {
            mDrawing = true;
            // Compute the RMS value. (Note that this does not remove DC).
            double rms = 0;
            for (int i = 0; i < audioFrame.length; i++) {
                rms += audioFrame[i] * audioFrame[i];
            }
            rms = Math.sqrt(rms / audioFrame.length);

            // Compute a smoothed version for less flickering of the display.
            mRmsSmoothed = mRmsSmoothed * mAlpha + (1 - mAlpha) * rms;
            final double rmsdB = 20.0 * Math.log10(mGain * mRmsSmoothed);

            // Set up a method that runs on the UI thread to update of the LED
            // bar
            // and numerical display.
            mDbTextView.post(new Runnable() {
                @Override
                public void run() {
                    // The bar has an input range of [0.0 ; 1.0] and 10
                    // segments.
                    // Each LED corresponds to 6 dB.
                    // mBarLevel.setLevel((mOffsetdB + rmsdB) / 60);

                    DecimalFormat df = new DecimalFormat("##");
                    df.setMinimumFractionDigits(1);
                    mDbTextView.setText(df.format(20 + rmsdB));

                    // DecimalFormat df_fraction = new DecimalFormat("#");
                    // int one_decimal = (int) (Math.round(Math.abs(rmsdB *
                    // 10))) % 10;
                    // mdBFractionTextView.setText(Integer.toString(one_decimal));
                    mDrawing = false;
                }
            });
        }
    }
}
