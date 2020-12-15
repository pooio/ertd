package com.metaShare.common.editor;

import java.beans.PropertyEditorSupport;

import com.metaShare.common.utils.DateHelper;

/**
 * @author Ling
 * @email qinjinlingx@gmail.com
 * @date 2018/4/27 14:30
 */
public class DateEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) {
        setValue(DateHelper.parseDate(text));
    }

}
