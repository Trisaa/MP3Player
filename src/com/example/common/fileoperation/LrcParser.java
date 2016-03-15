package com.example.common.fileoperation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * ������������LRC�ļ� ������������LRC�ļ�����һ��LrcInfo������ ���ҷ������LrcInfo����s author:java_mzd
 */
public class LrcParser {
	private Lyric lrcinfo = new Lyric();
	private long currentTime = 0;//�����ʱʱ��
	private String currentContent = null;//�����ʱ���
	//private Map<Long, String> maps = new HashMap<Long, String>();//�û��������еĸ�ʺ�ʱ�����Ϣ���ӳ���ϵ��Map
	
		
	/**
	 * �����ļ�·������ȡ�ļ�������һ��������
	 * 
	 * @param path
	 *          
	 * @return ������
	 * @throws FileNotFoundException
	 */
	private InputStream readLrcFile(String path) throws FileNotFoundException {
		File f = new File(path);
		InputStream ins = new FileInputStream(f);
		return ins;
	}

	public Lyric parser(String path) throws Exception {
		InputStream in = readLrcFile(path);
		lrcinfo = parser(in);
		System.out.println("1-------->"+lrcinfo.getlist().get(1).getSentence());
		return lrcinfo;

	}
	
	/**
	 * ���������е���Ϣ����������һ��LrcInfo����
	 * 
	 * @param inputStream
	 *            ������
	 * @return �����õ�LrcInfo����
	 * @throws IOException
	 */
	public Lyric parser(InputStream inputStream) throws IOException {
		// �����װ
		InputStreamReader inr = new InputStreamReader(inputStream,"GB2312");
		BufferedReader reader = new BufferedReader(inr);
		// һ��һ�еĶ���ÿ��һ�У�����һ��
		String line = null;
		while ((line = reader.readLine()) != null) {
			parserLine(line);
		}
		// ȫ�������������info
		//lrcinfo.setInfos((HashMap<Long, String>)maps);
		System.out.println("2-------->"+lrcinfo.getlist().get(1).getSentence());
		return lrcinfo;
	}

	/**
	 * �����������ʽ����ÿ�о������
	 * ���ڽ���������󣬽�������������Ϣ������LrcInfo������
	 * 
	 * @param str
	 */
	private void parserLine(String str) {
		// ȡ�ø�������Ϣ
		if (str.startsWith("[ti:")) {
			String title = str.substring(4, str.length() - 1);
			//System.out.println("title--->" + title);
			lrcinfo.setTitle(title);

		}// ȡ�ø�����Ϣ
		else if (str.startsWith("[ar:")) {
			String singer = str.substring(4, str.length() - 1);
			//System.out.println("singer--->" + singer);
			lrcinfo.setSinger(singer);

		}// ȡ��ר����Ϣ
		else if (str.startsWith("[al:")) {
			String album = str.substring(4, str.length() - 1);
			//System.out.println("album--->" + album);
			lrcinfo.setAlbum(album);

		}// ͨ������ȡ��ÿ������Ϣ
		else {
			// �����������
			String reg = "\\[(\\d{2}:\\d{2}\\.\\d{2})\\]";
			// ����
			Pattern pattern = Pattern.compile(reg);
			Matcher matcher = pattern.matcher(str);

			// �������ƥ�����ִ�����²���
			while (matcher.find()) {
				// �õ�ƥ�����������
				String msg = matcher.group();
				// �õ����ƥ���ʼ������
				int start = matcher.start();
				// �õ����ƥ�������������
				int end = matcher.end();

				// �õ����ƥ�����е�����
				int groupCount = matcher.groupCount();
				// �õ�ÿ����������
				for (int i = 0; i <= groupCount; i++) {
					String timeStr = matcher.group(i);
					if (i == 1) {
						// ���ڶ����е���������Ϊ��ǰ��һ��ʱ���
						currentTime = strToLong(timeStr);
					}
				}

				// �õ�ʱ���������
				String[] content = pattern.split(str);
				// �����������
				for (int i = 0; i < content.length; i++) {
					if (i == content.length - 1) {
						// ����������Ϊ��ǰ����
						currentContent = content[i];
					}
				}
				// ����ʱ�������ݵ�ӳ��
				//maps.put(currentTime, currentContent);
				lrcinfo.add(currentTime, currentContent);
				//System.out.println("put---currentTime--->" + currentTime
				//		+ "----currentContent---->" + currentContent);

			}
		}
	}

	/**
	 * �������õ��ı�ʾʱ����ַ�ת��ΪLong��
	 * 
	 * @param group
	 *            �ַ���ʽ��ʱ���
	 * @return Long��ʽ��ʱ��
	 */
	private long strToLong(String timeStr) {
		// ��Ϊ������ַ�����ʱ���ʽΪXX:XX.XX,���ص�longҪ�����Ժ���Ϊ��λ
		// 1:ʹ�ã��ָ� 2��ʹ��.�ָ�
		String[] s = timeStr.split(":");
		int min = Integer.parseInt(s[0]);
		String[] ss = s[1].split("\\.");
		int sec = Integer.parseInt(ss[0]);
		int mill = Integer.parseInt(ss[1]);
		return min * 60 * 1000 + sec * 1000 + mill * 10;
	}

}