package com.example.common.fileoperation;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

public class ID3v2Information implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// ID3v1 & ID3v2
	private String strTitle;
	private String strArtist;
	private String strAlbum;
	private String strYear;
	private int intVersion;
	private int intExHeaderSize;
	private boolean boolID3v2Footer;
	private int bitRate;
	// TEXT_ENCODING[0]应由 "ISO-8859-1" 改为 "GBK". ??
	private static String[] TEXT_ENCODING = { "GBK", "UTF-16", "UTF-16BE",
			"UTF-8" };

	public int checkID3V2(byte[] b, int off) {
		if (b.length - off < 10)
			return 0;
		if (b[off] != 'I' || b[off + 1] != 'D' || b[off + 2] != '3')
			return 0;

		intVersion = b[off + 3] & 0xff;

		if (intVersion > 2 && (b[off + 5] & 0x40) != 0)
			intExHeaderSize = 1; // 设置为1表示有扩展头

		boolID3v2Footer = (b[off + 5] & 0x10) != 0;
		int size = synchSafeInt(b, off + 6);
		size += 10; // ID3 header:10bytes
		return size;
	}

	// b[off..]不含ID3v2 头(10 bytes)
	public void parseID3V2(byte[] b, int off) {
		int max_size = b.length;
		int pos = off;
		if (intExHeaderSize == 1) {
			intExHeaderSize = synchSafeInt(b, off);
			pos += intExHeaderSize;
		}
		max_size -= 10; // 1 frame header: 10 bytes
		if (boolID3v2Footer)
			max_size -= 10;

		// System.out.println("ID3 v2." + intVersion);
		while (pos < max_size)
			pos += getText(b, pos, max_size);
	}

	private int synchSafeInt(byte[] b, int off) {
		int i = (b[off] & 0x7f) << 21;
		i |= (b[off + 1] & 0x7f) << 14;
		i |= (b[off + 2] & 0x7f) << 7;
		i |= b[off + 3] & 0x7f;
		return i;
	}

	private int makeInt(byte[] b, int off, int len) {
		int i, ret = b[off] & 0xff;
		for (i = 1; i < len; i++) {
			ret <<= 8;
			ret |= b[off + i] & 0xff;
		}
		return ret;
	}

	private int getText(byte[] b, int off, int max_size) {
		int id_part = 4, frame_header = 10;
		if (intVersion == 2) {
			id_part = 3;
			frame_header = 6;
		}
		String id = new String(b, off, id_part);
		off += id_part;

		int fsize, len;
		fsize = len = makeInt(b, off, id_part);
		off += id_part; // frame size = frame id bytes
		if (intVersion > 2)
			off += 2; // flag: 2 bytes

		int en = b[off];
		len--; // Text encoding: 1 byte
		off++; // Text encoding: 1 byte
		if (len <= 0 || off + len > max_size || en < 0
				|| en >= TEXT_ENCODING.length)
			return fsize + frame_header;

		try {
			switch (id.hashCode()) {
			case 83378: // TT2 v2.2
			case 2575251: // TIT2 标题
				if (strTitle == null)
					strTitle = new String(b, off, len, TEXT_ENCODING[en])
							.trim();
				break;
			case 83552:
			case 2590194: // TYER 发行年
				if (strYear == null)
					strYear = new String(b, off, len, TEXT_ENCODING[en]).trim();
				break;
			case 2569358: // TCON 流派
				break;
			case 82815:
			case 2567331: // TALB 唱片集
				if (strAlbum == null)
					strAlbum = new String(b, off, len, TEXT_ENCODING[en])
							.trim();
				break;
			case 83253:
			case 2581512: // TPE1 艺术家
				if (strArtist == null)
					strArtist = new String(b, off, len, TEXT_ENCODING[en])
							.trim();
				break;
			case 2583398: // TRCK 音轨
				break;
			}
		} catch (UnsupportedEncodingException e) {
			return fsize + frame_header;
		} finally {
			id = null;
		}
		return fsize + frame_header;
	}
	private int getBitRateByBit(byte b){
		b=(byte) ((b&240)>>>4);//b&11110000
		if(b==0){
		return 0;
		}
		else if(b==1) return 8000;
		else if(b==2) return 16000;
		else if(b==3) return 24000;
		else if(b==4) return 32000;
		else if(b==5) return 40000;
		else if(b==6) return 48000;
		else if(b==7) return 56000;
		else if(b==8) return 64000;
		else if(b==9) return 80000;
		else if(b==10) return 96000;
		else if(b==11) return 112000;
		else if(b==12) return 128000;
		else if(b==13) return 144000;
		else if(b==14) return 160000;
		else if(b==15) return -1;
		else return -1;


		}


	public String getStrTitle() {
		return strTitle;
	}

	public void setStrTitle(String strTitle) {
		this.strTitle = strTitle;
	}

	public String getStrArtist() {
		return strArtist;
	}

	public void setStrArtist(String strArtist) {
		this.strArtist = strArtist;
	}

	public String getStrAlbum() {
		return strAlbum;
	}

	public void setStrAlbum(String strAlbum) {
		this.strAlbum = strAlbum;
	}

	public String getStrYear() {
		return strYear;
	}

	public void setStrYear(String strYear) {
		this.strYear = strYear;
	}

	@Override
	public String toString() {
		return "ID3v2Information [strTitle=" + strTitle + ", strArtist="
				+ strArtist + ", strAlbum=" + strAlbum + ", strYear=" + strYear
				+ ", intVersion=" + intVersion + ", intExHeaderSize="
				+ intExHeaderSize + ", boolID3v2Footer=" + boolID3v2Footer
				+ ", bitRate=" + bitRate + "]";
	}
}
