package com.metaShare.common.tool.state;

public enum ResultCode {
	/* 成功状态码 */
    SUCCESS(0, "成功"),
    FAILURE(999999999,"失败"),

    /* 参数错误：10001-19999 */
    PARAMS_IS_NULL(10001, "参数为空"),
    PARAMS_NOT_COMPLETE(10002, "参数不全"),
    VALIDEXCEPTION(10003, "数据校验失败"),
    PARAMS_IS_INVALID(10004, "参数无效"),
    PARAMS_TYPE_ERROR(10005,"参数类型错误"),
    PARAMS_IS_EXISTENCE(10006,"参数已存在"),
    
    /* 用户错误：20001-29999*/
    USER_NOT_EXIST(20001, "用户不存在"),
    USER_NOT_LOGGED_IN(20002, "用户未登陆"),
    USER_ACCOUNT_ERROR(20003, "用户名或密码错误"),
    USER_ACCOUNT_FORBIDDEN(20004, "用户账户已被禁用"),
    USER_HAS_EXIST(20005, " 用户已存在"),
    PASSWORD_NOT_CHANGE(20006, "旧密码与原密码相同"),
    OLD_PASSWORD_ERROR(20007, "旧密码错误"),
    EMAIL_HAS_EXIST(20008, " 邮箱已存在"),
    PHONE_HAS_EXIST(20009, "  电话号码已存在"),
    EMAIL_OR_PHONE_HAS_EXIST(20010, " 用户登录名或者手机号或者邮箱已被占用，无法完成修改"),
    
    /* 业务错误：30001-39999 */
    BUSINESS_ERROR(30001, "系统业务出现问题"),
    NOT_DELETE(30002, "不允许删除"),
    MODEL_DATA_NULL(30003, "模型数据为空"),
    MODEL_MODELKEY_NULL(30004, "模型标识为空"),
    MENU_ALREADY_EXIST(30005, "相同节点下已有相同的菜单名称"),
    DICT_CODE_SAME(30006, "字典编码相同"),
    MODEL_KEY_REPEAT(30007, "模型标识重复"),
    MODEL_PUBLISH_ERROR(30008, "流程模型发布失败"),
    NODE_HAVE_NODE(30009,"节点下有节点"),
    BUSINESS_REPEAT(30010,"重复"),
    VAIL_ERROR(30011,"验证未通过"),
    
    /* 系统错误：40001-49999 */
    SYSTEM_INNER_ERROR(40001, "系统内部错误"),

    /* 数据错误：50001-599999 */
    DATA_NOT_FOUND(50001, "数据未找到"),
    DATA_IS_WRONG(50002, "数据有误"),
    DATA_ALREADY_EXISTED(50003, "数据已存在"),

    /* 接口错误：60001-69999 */
    INTERFACE_INNER_INVOKE_ERROR(60001, "内部系统接口调用异常"),
    INTERFACE_OUTER_INVOKE_ERROR(60002, "外部系统接口调用异常"),
    INTERFACE_FORBIDDEN(60003, "接口禁止访问"),
    INTERFACE_ADDRESS_INVALID(60004, "接口地址无效"),
    INTERFACE_REQUEST_TIMEOUT(60005, "接口请求超时"),
    INTERFACE_EXCEED_LOAD(60006, "接口负载过高"),

    /* 权限错误：70001-79999 */
    PERMISSION_NO_ACCESS(70001, "无访问权限"),
	/* 设备参数：80001-89999 */
	Device_Token(80001, "Token参数无效"),
	Device_DeviceNum(80002, "设备已存在"),
	Device_DeviceNum20010(20010, "设备验证码错误"),
	Device_DeviceNum20011(20011, "设备添加失败"),
	Device_DeviceNum20014(20014, "序列号不合法"),
	Device_DeviceNum20017(20017, "设备已被添加"),
	POBUDGET1(90001, "项目工期未维护，请先维护项目工期"),
	SESSION_EXPIRED(11001,"session过期"),
	SESSION_ILLEGAL(11002,"令牌来路非法"),
	LOGIN_NAME_NULL(11003,"未获取到登录人员信息");
	
	private Integer code;

    private String message;
    
    public Integer code() {
		return code;
	}

	public String message() {
		return message;
	}

	ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

	public static String getMessage(String name) {
        for (ResultCode item : ResultCode.values()) {
            if (item.name().equals(name)) {
                return item.message;
            }
        }
        return name;
    }

    public static Integer getCode(String name) {
        for (ResultCode item : ResultCode.values()) {
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
    public static ResultCode findResultCode(ResultCode statueCode){
        for (ResultCode item : ResultCode.values()) {
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
