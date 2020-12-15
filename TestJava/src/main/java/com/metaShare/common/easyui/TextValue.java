package com.metaShare.common.easyui;

import java.io.Serializable;

/**
 * @author Ling
 * @email qinjinlingx@gmail.com
 * @date 2018/4/19 14:17
 */
public class TextValue implements Serializable {

    private static final long serialVersionUID = 1L;

    public TextValue(String text, String value) {
        this.text = text;
        this.value = value;
    }

    private String text;
    private String value;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
