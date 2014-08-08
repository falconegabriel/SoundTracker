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
import in.ubee.api.ui.views.InternalIndoorMapView;
import in.ubee.api.ui.views.InternalIndoorMapView.Options;
import in.ubee.models.Retail;
import in.ubee.models.RetailMap;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
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
import br.cin.ufpe.soundtracker.microphone.MicrophoneManager;
import br.cin.ufpe.soundtracker.microphone.ProcessedAudioListener;
import br.cin.ufpe.soundtracker.microphone.SoundMeasure;
import br.cin.ufpe.soundtracker.utils.Configurations;
import br.cin.ufpe.soundtracker.views.LoadingDialog;
import br.cin.ufpe.soundtracker.views.LocationStateWidget;
import br.cin.ufpe.soundtracker.views.LocationStateWidget.LocationState;
import br.cin.ufpe.soundtracker.views.MeasurePointView;

public class MapActivity extends Activity implements OnClickListener, OnMapsLocationListener, ProcessedAudioListener {
    public static final String TAG = MapActivity.class.getSimpleName();

    private MicrophoneManager mMicrophoneManager;

    private Retail mRetail;

    private LoadingDialog mDialog;

    private IndoorMapView mMapView;
    private MeasurePointView mMeasurePointView;
    private LocationStateWidget mLocatingView;

    private TextView mDbTextView;

    private MenuItem mRecordingMenuItem;

    private Location mLastLocation;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.map_activity);

        mRetail = Configurations.getDefaultRetail();

        ActionBar actionbar = this.getActionBar();
        actionbar.setTitle("MapActivity");

        mDialog = new LoadingDialog(this);
        mDbTextView = (TextView) findViewById(R.id.current_sound_value_text_view);
        mMicrophoneManager = new MicrophoneManager(this, this);

        InternalIndoorMapView.Options options = Options.defaultOptions();
        options.setRouteEnabled(false);
        options.setUserInteractionEnabled(false);

        mMapView = (IndoorMapView) findViewById(R.id.map_fragment_indoor_map_view);
        mMapView.setCloseRouteGravity(Gravity.LEFT | Gravity.BOTTOM, 0, 0);
        mMapView.setOptions(options);

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

                    mDialog.dismiss("");

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
        mRecordingMenuItem = menu.findItem(R.id.map_activity_recording_menu_item);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        if (item.getItemId() == R.id.map_activity_recording_menu_item) {
            setMicrophoneStatus(!mMicrophoneManager.isRecording());
        }

        return super.onMenuItemSelected(featureId, item);
    }

    private void setMicrophoneStatus(boolean state) {
        if (!mMicrophoneManager.isRecording() && state) {
            mRecordingMenuItem.setIcon(android.R.drawable.ic_media_pause);
            mRecordingMenuItem.setTitle("Clique para parar de gravar");
            mMicrophoneManager.start();
        } else if (mMicrophoneManager.isRecording() && !state) {
            mRecordingMenuItem.setIcon(android.R.drawable.ic_media_play);
            mRecordingMenuItem.setTitle("Clique para começar a gravar");
            mMicrophoneManager.stop();
            mDbTextView.setText("0.0db");
        }
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
        setMicrophoneStatus(false);
        Ubee.unregisterLocationCallback(this, this);
        mLastLocation = null;
    }

    private void showLocationErrorToast() {
        Toast.makeText(this, R.string.map_activity_error_message_location_unavailable, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null && location.getRetailId() != null) {
            mLocatingView.setLocationAvailableProcessing();
            mMapView.setUserLocation(location);
            mLastLocation = location;

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

    @Override
    public void onAudioReceived(final double value) {
        
        if (mMicrophoneManager.isRecording()) {
            
            if (value > 0 && mLastLocation != null) {
                SoundMeasure measure = new SoundMeasure();
                measure.set(mLastLocation.getX(), mLastLocation.getY());
                measure.value = (float) value;
                mMeasurePointView.addMeasure(measure);
            }

            if (value > 0) {
                mDbTextView.setText(value + "db");
            } else {
                mDbTextView.setText("Microphone unavailable");
            }
        }
    }

}
