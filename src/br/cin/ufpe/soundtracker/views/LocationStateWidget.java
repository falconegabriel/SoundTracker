package br.cin.ufpe.soundtracker.views;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import br.cin.ufpe.soundtracker.R;

public class LocationStateWidget extends ImageView {
    private LocationState locationState = LocationState.STOPPED;

    public LocationStateWidget(final Context context) {
        super(context);
    }

    public LocationStateWidget(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public LocationStateWidget(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setLocationUnavailable() {
        if (this.locationState != LocationState.LOCATION_UNAVAILABLE) {
            this.locationState = LocationState.LOCATION_UNAVAILABLE;
            this.setImageResource(R.drawable.ic_location_fail);
        }
    }

    public void setLocationAvailableProcessing() {
        if (this.locationState != LocationState.PROCESSING_AVAILABLE_LOCATION) {
            this.locationState = LocationState.PROCESSING_AVAILABLE_LOCATION;
            this.setImageResource(R.drawable.ic_location);
        }
    }

    public void setLocationNotFound() {
        if (this.locationState != LocationState.LOCATION_NOT_FOUND) {
            this.locationState = LocationState.LOCATION_NOT_FOUND;
            this.setImageResource(R.drawable.ic_location);
            AnimationDrawable animation = (AnimationDrawable) this.getResources().getDrawable(R.drawable.map_activity_location_processing_animation);
            this.setImageDrawable(animation);
            animation.start();
        }
    }

    public void setLocationInitializating() {
        if (this.locationState != LocationState.INITIALIZATING) {
            this.locationState = LocationState.INITIALIZATING;
            AnimationDrawable animation = (AnimationDrawable) this.getResources().getDrawable(R.drawable.map_activity_location_processing_animation);
            this.setImageDrawable(animation);
            animation.start();
        }
    }

    public LocationState getLocatingState() {
        return this.locationState;
    }
    
    public enum LocationState {
        LOCATION_NOT_FOUND, PROCESSING_AVAILABLE_LOCATION, INITIALIZATING, LOCATION_UNAVAILABLE, STOPPED
    }
}
