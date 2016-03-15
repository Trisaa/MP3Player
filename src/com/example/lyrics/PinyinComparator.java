package com.example.lyrics;

import java.util.Comparator;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
/**
 * 汉字按照拼音排序的比较器
 * @author 何涌  2014-3-20 
 * 
 */
public class PinyinComparator implements Comparator<Object> {
	public int compare(Object o1, Object o2) {
		/*char c1 = ((String) o1).charAt(0);
		char c2 = ((String) o2).charAt(0);
		System.out.println(concatPinyinStringArray(PinyinHelper.toHanyuPinyinStringArray(c1)));
		return concatPinyinStringArray(
				PinyinHelper.toHanyuPinyinStringArray(c1)).compareTo(
				concatPinyinStringArray(PinyinHelper
						.toHanyuPinyinStringArray(c2)));*/
			String str1 = PingYinUtil.getFirstPingYin((String) o1);
			String str2 = PingYinUtil.getFirstPingYin((String) o2);
			//System.out.println(str1);
			return str1.compareTo(str2);
	}
	
	private String concatPinyinStringArray(String[] pinyinArray) {
		StringBuffer pinyinSbf = new StringBuffer();
		if ((pinyinArray != null) && (pinyinArray.length > 0)) {
			for (int i = 0; i < pinyinArray.length; i++) {
				pinyinSbf.append(pinyinArray[i]);
			}
		}
		return pinyinSbf.toString();
	}

	public static class PingYinUtil {
		/**
		 * 将字符串中的中文转化为拼音,其他字符不变
		 * 
		 * @param inputString
		 * @return
		 */
		public static String getPingYin(String inputString) {
			HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
			format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
			format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
			format.setVCharType(HanyuPinyinVCharType.WITH_V);

			char[] input = inputString.trim().toCharArray();
			String output = "";

			try {
				for (int i = 0; i < input.length; i++) {
					if (java.lang.Character.toString(input[i]).matches(
							"[\\u4E00-\\u9FA5]+")) {
						String[] temp = PinyinHelper.toHanyuPinyinStringArray(
								input[i], format);
						output += temp[0];
					} else
						output += java.lang.Character.toString(input[i]);
				}
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				e.printStackTrace();
			}
			return output;
		}
		
		
		/**
		 * 返回首字的拼音
		 * @param inputString
		 * @return
		 */
		public static String getFirstPingYin(String inputString) {
			HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
			format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
			format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
			format.setVCharType(HanyuPinyinVCharType.WITH_V);

			char[] input = inputString.trim().toCharArray();
			String output = "";

			try {
					if (java.lang.Character.toString(input[0]).matches(
							"[\\u4E00-\\u9FA5]+")) {
						String[] temp = PinyinHelper.toHanyuPinyinStringArray(
								input[0], format);
						output += temp[0];
					} else
						output += java.lang.Character.toString(input[0]);
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				e.printStackTrace();
			}
			return output;
		}
	}

}