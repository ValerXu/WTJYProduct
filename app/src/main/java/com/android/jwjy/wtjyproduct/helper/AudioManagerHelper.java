package com.android.jwjy.wtjyproduct.helper;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

public class AudioManagerHelper {

    AudioManager audioManager;
    int maxVolume;
    int oldVolume = Integer.MIN_VALUE;
    int curretnStreamType = AudioManager.STREAM_MUSIC;

    public  AudioManagerHelper(Context context){
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

    }

    private void setVolume(int volume){

        audioManager.setStreamVolume(curretnStreamType,volume,AudioManager.FLAG_SHOW_UI);
    }

    public void setVolume(int streamType, int volume){
        audioManager.setStreamVolume(streamType,volume,AudioManager.FLAG_SHOW_UI);
    }

    private int getVolume(){
         return audioManager.getStreamVolume(curretnStreamType);
    }

    public int getVolume(int streamType){
        return audioManager.getStreamVolume(streamType);
    }

    public void startVolumeOffset(){
        startVolumeOffset(AudioManager.STREAM_MUSIC);
    }
    public void startVolumeOffset(int streamType){
        curretnStreamType = streamType;
        maxVolume = audioManager.getStreamMaxVolume(curretnStreamType);
        oldVolume = getVolume();
    }

    public void volumeOffset(float offsetPercentage){
        if(oldVolume == Integer.MIN_VALUE)
            oldVolume = getVolume();
        Log.d("volume","percentage:"+offsetPercentage*100);
        int newVolume = Math.min((int)(offsetPercentage * 2 * maxVolume) + oldVolume,maxVolume);
        setVolume(newVolume);
    }

    public void stopVolumeOffset(){
        oldVolume = Integer.MIN_VALUE;
    }

}
