package com.example.lyrics;

public interface MacroDefination {
	
	
	//播放状态部分
	public final static int STATUS_PLAY  = 0x0;      //播放
	public final static int STATUS_PAUSE = 0x1;      //暂停
	public final static int STATUS_STOP  = 0x2;      //停止
	
	//顺序模式
	public final static int MODE_SEQUENCE = 0x0;     //顺序
	public final static int MODE_RANDOM   = 0x1;     //随机
	public final static int MODE_SINGLE   = 0x2;     //单曲循环

	//Activity中的多选模式
	public final static int SINGLE_CHOICE   = 0x0;           //单选模式
	public final static int MULTIPLE_CHOICE = 0x1;           //多选模式
	
	//广播命令    由Activity向Service发送
	public final static String ACTIVITY_CMD_INITIALIZE = "CMD_INITIALIZE";      //初始化服务
	
	public final static String ACTIVITY_CMD_START = "CMD_START";    //开始
	public final static String ACTIVITY_CMD_PLAY = "CMD_PLAY";      //播放
	public final static String ACTIVITY_CMD_PAUSE = "CMD_PAUSE";    //暂停
	public final static String ACTIVITY_CMD_NEXT = "CMD_NEXT";      //下一首
	public final static String ACTIVITY_CMD_PREV = "CMD_PREV";      //上一首
	
	public final static String ACTIVITY_CMD_MODE = "CMD_MODE";      //设置循环模式
	public final static String ACTIVITY_CMD_STOP = "CMD_STOP";      //停止服务
	
	public final static String ACTIVITY_REQUEST_MP3_INFO = "REQUEST_MP3_INFO";       //请求正在播放的Mp3信息
	public final static String ACTIVITY_REQUEST_PROGRESS = "REQUEST_PROGRESS";       //请求进度条
	public final static String ACTIVITY_REQUEST_PLAY_LIST = "REQUEST_PLAY_LIST";     //请求播放列表
	public final static String ACTIVITY_REQUEST_INITIALIZE = "REQUEST_INITIALIZE";   //请求Mp3列表和艺术家
	
	public final static String ACTIVITY_CMD_PROGRESS = "ACTIVITY_CMD_PROGRESS";
	public final static String ACTIVITY_CMD_CURRENT = "ACTIVITY_CMD_CURRENTS";
	
	//广播命令 由Service向Activity发送
	public final static String SERVICE_RETURN_MP3_INFO = "RETURN_MP3_INFO";     //返回当前播放的状态
	public final static String SERVICE_RETURN_PROGRESS = "RETURN_PROGRESS";      //返回当前进度条
	public final static String SERVICE_RETURN_PLAY_LIST = "RETURN_PLAY_LIST";    //返回正在播放列表
	public final static String SERVICE_REQUEST_MP3_LIST = "REQUEST_MP3_LIST";    //请求所有Mp3列表
	public final static String SERVICE_RETURN_INITIALIZE = "RETURN_INITIALIZE";  //返回初始化信息
	
	//Intent中放置的Extra数据
	public final static String DATA_PLAY_LIST    = "DATA_PLAY_LIST";          //播放列表
	public final static String DATA_PLAY_STATUS  = "DATA_PLAY_STATUS";        //播放状态
	public final static String DATA_MP3_INFO     = "DATA_MP3_INFO";           //正在播放的Mp3信息
	public final static String DATA_MP3_DURATION = "DATA_MP3_DURATION";       //Mp3长度
	public final static String DATA_PROGRESS     = "DATA_PROGRESS";           //Mp3进度
	public final static String DATA_PLAY_MODE    = "DATA_PLAY_MODE";          //播放循环模式
	public final static String DATA_CURRENT      = "DATA_CURRENT";            //立即播放
	
	public final static String DATA_MP3_LIST     = "DATA_MP3_LIST";
	public final static String DATA_ARTIST_LIST  = "DATA_ARTIST_LIST";
	public final static String DATA_ARTIST_MAP   = "DATA_ARTIST_MAP";
	
}
