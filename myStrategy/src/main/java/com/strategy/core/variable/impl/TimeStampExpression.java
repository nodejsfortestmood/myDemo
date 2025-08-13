package com.strategy.core.variable.impl;


import com.strategy.core.variable.Expression;
import com.strategy.helper.validation.Assert;

import java.util.Map;

/**
 * 时间戳定义
 * ${timestamp len=10}
 */
public class TimeStampExpression implements Expression {

    @Override
    public String execute(Map<String, String> args) {
        //默认输出13位
        String type = args.containsKey("len") ? String.valueOf(args.get("len")) : "13";
        Assert.isTrue(type.matches("10|13"), "时间戳只支持10位或者13位！");
        long timeStamp = "13".equals(type) ? System.currentTimeMillis() : System.currentTimeMillis() / 1000;
        return String.valueOf(timeStamp);
    }
}
