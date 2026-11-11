package com.gamechief.aifunnyringtone.models;

public class SoundHistoryItem {
    private String audioUrl;
    private String prompt;

    public SoundHistoryItem() {
    }

    public SoundHistoryItem(String prompt2, String audioUrl2) {
        this.prompt = prompt2;
        this.audioUrl = audioUrl2;
    }

    public String getPrompt() {
        return this.prompt;
    }

    public String getAudioUrl() {
        return this.audioUrl;
    }

    public void setPrompt(String prompt2) {
        this.prompt = prompt2;
    }

    public void setAudioUrl(String audioUrl2) {
        this.audioUrl = audioUrl2;
    }
}
