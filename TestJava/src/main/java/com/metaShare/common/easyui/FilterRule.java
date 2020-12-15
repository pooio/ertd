package com.metaShare.common.easyui;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.baomidou.mybatisplus.mapper.Condition;
import com.google.common.base.Strings;
import com.metaShare.common.exception.BusinessException;
import com.metaShare.common.utils.JSONUtils;

/**
 * FilterRule 表示 easyui-datagrid 自动查询组件查询条件
 *
 * @author Ling
 * @email qinjinlingx@gmail.com
 * @date 2018/4/16 20:39
 */
public class FilterRule implements Serializable {

    private static final long serialVersionUID = 1L;

    private String field;
    private String op;
    private String value;
    private String type;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String compile() {
        String value = this.value.trim();
        if (this.type.equals("datebox")) {
            String dateFmt = StringUtils.isNotBlank(this.value) && value.length() > 7 ? "%Y-%m-%d" : "%Y-%m";
            switch (this.op) {
                case "equal":
                    return String.format("date_format(`%s`,'%s') = '%s'", field, dateFmt, value);
                case "notequal":
                    return String.format("date_format(`%s`,'%s') != '%s'", field, dateFmt, value);
                case "less":
                    return String.format("date_format(`%s`,'%s') < '%s'", field, dateFmt, value);
                case "lessorequal":
                    return String.format("date_format(`%s`,'%s') <= '%s'", field, dateFmt, value);
                case "greater":
                    return String.format("date_format(`%s`,'%s') > '%s'", field, dateFmt, value);
                case "greaterorequal":
                    return String.format("date_format(`%s`,'%s') >= '%s'", field, dateFmt, value);
            }
            return String.format("date_format(`%s`,'%s') = '%s'", field, dateFmt, value);
        }

        switch (this.op) {
            case "contains":
                return String.format("lower(`%s`) like '%%%s%%'", field, value.toLowerCase());
            case "equal":
                if (value.equals("null")) {
                    return String.format("`%s` is null", field);
                }
                return String.format("`%s` = '%s'", field, value);
            case "notequal":
                return String.format("`%s` != '%s'", field, value);
            case "beginwith":
                return String.format("lower(`%s`) like '%%s%%'", field, value.toLowerCase());
            case "endwith":
                return String.format("lower(`%s`) like '%%%s'", field, value.toLowerCase());
            case "less":
                return String.format("`%s` < '%s'", field, value);
            case "lessorequal":
                return String.format("`%s` <= '%s'", field, value);
            case "greater":
                return String.format("`%s` > '%s'", field, value);
            case "greaterorequal":
                return String.format("`%s` >= '%s'", field, value);
        }
        return String.format("lower(`%s`) like '%%%s%%'", field, value.toLowerCase());
    }

    public static Condition createCondition(String filterRules) {
        Condition condition = Condition.create();
        if (Strings.isNullOrEmpty(filterRules))
            return condition;

        List<FilterRule> rules;
        try {
            rules = JSONUtils.jsonToList(filterRules, FilterRule.class);
            for (FilterRule rule : rules) {
                condition.where(rule.compile());
            }
            return condition;
        } catch (Exception e) {
            throw new BusinessException("创建搜索条件失败");
        }
    }
}
