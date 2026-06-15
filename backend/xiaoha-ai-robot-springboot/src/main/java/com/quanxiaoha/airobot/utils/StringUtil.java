package com.quanxiaoha.airobot.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 字符串工具类
 */
public class StringUtil {

    /**
     * 静态方法，不用创建 StringUtil 对象，直接通过 StringUtil.truncate(...) 调用。
     * message：用户输入的原始消息（比如用户发的问题文本）。
     * maxLength：你想截取的最大长度（比如设置成 20，就只取前 20 个字符）。
     * 返回值：截取后的摘要字符串。
     * @param message
     * @param maxLength
     * @return
     */
    public static String truncate(String message,int maxLength) {
        // 判空
        if (StringUtils.isBlank(message)) {
            //原生写法 message == null || message.isEmpty() 只能判断 null 和空串，无法判断全空格字符串。
            //StringUtils.isBlank 会同时处理 null、""、" " 三种情况，更安全。
            return "";
        }
        //去掉首尾空格
        //避免用户输入大量空格，导致摘要变成一堆空白字符。
        //让后续的长度判断更准确。
        String trimmed = message.trim();

        // 如果文本长度小于等于最大长度，直接返回
        if (trimmed.length() <= maxLength) {
            return trimmed;
        }

        // 截取指定长度
        //这里表示从第 0 个字符开始，截取到第 maxLength 个字符（不包含第 maxLength 个）。
        return trimmed.substring(0, maxLength);
    }
}
