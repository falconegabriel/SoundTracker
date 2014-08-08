package br.cin.ufpe.soundtracker.microphone;

import java.text.DecimalFormat;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;

public class MicrophoneManager implements MicrophoneInputListener {

    private Handler mHandler;
    
    private ProcessedAudioListener mListener;
    
    MicrophoneInput mMicInput; // The micInput object provides real time audio.
    
    // The Google ASR input requirements state that audio input sensitivity
    // should be set such that 90 dB SPL at 1000 Hz yields RMS of 2500 for
    // 16-bit samples, i.e. 20 * log_10(2500 / mGain) = 90.
    private double mGain = 2500.0 / Math.pow(10.0, 90.0 / 20.0);
    // For displaying error in calibration.
    private double mRmsSmoothed; // Temporally filtered version of RMS.
    private double mAlpha = 0.9; // Coefficient of IIR smoothing filter for RMS.
    
    private int mSampleRate = 8000; // The audio sampling rate to use.
    private int mAudioSource = MediaRecorder.AudioSource.VOICE_RECOGNITION;
    
    // Variables to monitor UI update and check for slow updates.
    private volatile boolean mDrawing;
    
    private boolean mRecording;
    
    public MicrophoneManager(final Context context, final ProcessedAudioListener listener) {
        mHandler = new Handler(Looper.getMainLooper());
        mListener = listener;
    }
    
    public void start() {
        if (isRecording()) {
            mMicInput.stop();
        }
        mMicInput = new MicrophoneInput(this);
        mMicInput.setSampleRate(mSampleRate);
        mMicInput.setAudioSource(mAudioSource);
        mMicInput.start();
        mRecording = true;

    }
    
    public void stop() {
        if (mMicInput != null) {
            mMicInput.stop();
            mRecording = false;
        }
    }

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
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    
                    DecimalFormat df = new DecimalFormat("##");
                    df.setMinimumFractionDigits(1);
                    if (Double.isInfinite(rmsdB)) {
                        mListener.onAudioReceived(-1);    
                    } else {
                        mListener.onAudioReceived(Double.parseDouble(df.format(20 + rmsdB)));
                    }
                    mDrawing = false;
                }
            });

        }
    }
    
    public boolean isRecording() {
        return mRecording;
    }
}
