package com.studyboard.dto;

public class TagDTO {
    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String toTag() {
        return this.tag;
    }

    public static TagDTO of(String tag, long documentId) {
        TagDTO tagDTO = new TagDTO();
        tagDTO.setTag(tag);
        return tagDTO;
    }


    @Override
    public String toString() {
        return "TagDTO{" +
                "tag='" + tag + '\'' +
                '}';
    }
}
