package br.cin.ufpe.soundtracker.microphone;

import br.cin.ufpe.soundtracker.utils.Configurations;


public class SoundMeasure {
    public static final int RADIUS = 20;
    public float value;
    public float x;
    public float y;

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!getClass().equals(obj.getClass())) {
            return false;
        }
        SoundMeasure other = (SoundMeasure) obj;
        if (!distanceSmallerThanOffset(x, other.x, Configurations.SOUND_AREA_MATCH_RADIUS)) {
            return false;
        }
        if (!distanceSmallerThanOffset(y, other.y, Configurations.SOUND_AREA_MATCH_RADIUS)) {
            return false;
        }
        return true;
    }

    public boolean distanceSmallerThanOffset(float first, float second, int offset) {
        return Math.abs(first - second) <= offset;
    }
}
