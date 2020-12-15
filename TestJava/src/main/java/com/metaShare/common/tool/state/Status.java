package com.metaShare.common.tool.state;

public enum  Status {

    /**
     * 模块名称
     */
    MODULE_PRODUCTION(20001,"生产运行管理"),
    MODULE_BUDGET(20002,"预算管理"),
    MODULE_CONTRACT(20003,"合同管理"),
    MODULE_VIDEO(20004,"视频监控"),
    MODULE_SYSTEM(20005,"系统管理"),
    MODULE_PROCESS(20006,"流程管理"),

    /**
     * 预算表名
     */

    BUDGET_PROJECT_MANAGE(30001,"项目管理(含监理)项目预算表"),
    BUDGET_DESIGN_PROJECT(30002,"设计项目预算表"),
    BUDGET_EPC_PROJECT(30003,"EPC项目预算表");


    private Integer code;

    private String message;

    public Integer code() {
        return code;
    }

    public String message() {
        return message;
    }

    Status(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessage(String name) {
        for (Status item : Status.values()) {
            if (item.name().equals(name)) {
                return item.message;
            }
        }
        return name;
    }

    public static Integer getCode(String name) {
        for (Status item : Status.values()) {
            if (item.name().equals(name)) {
                return item.code;
            }
        }
        return null;
    }

    /**
     * 根据code返回信息
     * @param statueCode
     * @return
     */
    public static Status findResultCode(Status statueCode){
        for (Status item : Status.values()) {
            if (item.code.equals(statueCode.code)) {
                return item;
            }
        }
        return null;

    }

    @Override
    public String toString() {
        return this.name();
    }
}
