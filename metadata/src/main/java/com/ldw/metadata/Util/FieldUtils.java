package com.ldw.metadata.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @desc 字段转换工具类
 * 
 * @author WangXf
 * @date 2020/6/10 18:02:45
 */
public class FieldUtils {

	/**
	 * @desc db字段名转换成对象字段名
	 * 
	 * @param value
	 * @return
	 */
	public static final String dbFieldNameToObjectName(String value) {
		String[] strs = value.split("_");
		StringBuilder builder = new StringBuilder();
		builder.append(strs[0]);
		for (int i = 1; i < strs.length; i++) {
			builder.append(strs[i].substring(0, 1).toUpperCase()).append(strs[i].substring(1));
		}
		return builder.toString();
	}

	/**
	 * @desc 利用正则表达式判断字符串是否是数字
	 * @param value
	 * @return bool是否
	 */
	public static final boolean isNumeric(String value) {
		Pattern pattern = Pattern.compile("^[0-9]*$");
		Matcher isNum = pattern.matcher(value);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}
	/**
	 * @desc 利用正则表达式判断字符串是否是英文字母
	 * @param value
	 * @return bool是否
	 */
	public static final boolean isEnglishLetter(String value) {
		Pattern pattern = Pattern.compile("^[a-zA-Z_]*$");
		Matcher isEnglishLetter = pattern.matcher(value);
		if (!isEnglishLetter.matches()) {
			return false;
		}
		return true;
	}

}
