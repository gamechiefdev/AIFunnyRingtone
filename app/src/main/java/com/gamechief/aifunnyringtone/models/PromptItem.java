package com.gamechief.aifunnyringtone.models;

public class PromptItem {
    private String mainPrompt;
    private String prefix;

    public PromptItem(String prefix2, String mainPrompt2) {
        this.prefix = prefix2;
        this.mainPrompt = mainPrompt2;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getMainPrompt() {
        return this.mainPrompt;
    }

    public String getFullText(String name) {
        if (name == null || name.isEmpty()) {
            return this.prefix + " " + this.mainPrompt;
        }
        return this.prefix + " " + name + ", " + this.mainPrompt;
    }

    public String getDisplayText(String name) {
        if (name == null || name.isEmpty()) {
            return this.prefix;
        }
        return this.prefix + " " + name;
    }
}
