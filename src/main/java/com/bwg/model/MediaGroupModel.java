package com.bwg.model;

import java.util.List;

public class MediaGroupModel {
    private List<MediaModel> media;

    public MediaGroupModel(List<MediaModel> media) {
        this.media = media;
    }

    public List<MediaModel> getMedia() {
        return media;
    }

    public void setMedia(List<MediaModel> media) {
        this.media = media;
    }
}
