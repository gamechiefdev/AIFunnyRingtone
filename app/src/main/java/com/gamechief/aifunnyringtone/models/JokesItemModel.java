package com.gamechief.aifunnyringtone.models;

public class JokesItemModel {
    private int background;
    private String id;
    private String imgBackground;
    private boolean isLiked;
    private boolean isSelected;
    private String text;

    public JokesItemModel() {
    }

    public JokesItemModel(String id2, String text2, boolean isLiked2, int background2) {
        this.id = id2;
        this.text = text2;
        this.isLiked = isLiked2;
        this.background = background2;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id2) {
        this.id = id2;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text2) {
        this.text = text2;
    }

    public boolean isLiked() {
        return this.isLiked;
    }

    public void setLiked(boolean liked) {
        this.isLiked = liked;
    }

    public int getBackground() {
        return this.background;
    }

    public void setBackground(int background2) {
        this.background = background2;
    }

    public String getImgBackground() {
        return this.imgBackground;
    }

    public void setImgBackground(String imgBackground2) {
        this.imgBackground = imgBackground2;
    }

    public boolean isSelected() {
        return this.isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }
}
