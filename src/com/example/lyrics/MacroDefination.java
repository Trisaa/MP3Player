package com.example.lyrics;

public interface MacroDefination {
	
	
	//����״̬����
	public final static int STATUS_PLAY  = 0x0;      //����
	public final static int STATUS_PAUSE = 0x1;      //��ͣ
	public final static int STATUS_STOP  = 0x2;      //ֹͣ
	
	//˳��ģʽ
	public final static int MODE_SEQUENCE = 0x0;     //˳��
	public final static int MODE_RANDOM   = 0x1;     //���
	public final static int MODE_SINGLE   = 0x2;     //����ѭ��

	//Activity�еĶ�ѡģʽ
	public final static int SINGLE_CHOICE   = 0x0;           //��ѡģʽ
	public final static int MULTIPLE_CHOICE = 0x1;           //��ѡģʽ
	
	//�㲥����    ��Activity��Service����
	public final static String ACTIVITY_CMD_INITIALIZE = "CMD_INITIALIZE";      //��ʼ������
	
	public final static String ACTIVITY_CMD_START = "CMD_START";    //��ʼ
	public final static String ACTIVITY_CMD_PLAY = "CMD_PLAY";      //����
	public final static String ACTIVITY_CMD_PAUSE = "CMD_PAUSE";    //��ͣ
	public final static String ACTIVITY_CMD_NEXT = "CMD_NEXT";      //��һ��
	public final static String ACTIVITY_CMD_PREV = "CMD_PREV";      //��һ��
	
	public final static String ACTIVITY_CMD_MODE = "CMD_MODE";      //����ѭ��ģʽ
	public final static String ACTIVITY_CMD_STOP = "CMD_STOP";      //ֹͣ����
	
	public final static String ACTIVITY_REQUEST_MP3_INFO = "REQUEST_MP3_INFO";       //�������ڲ��ŵ�Mp3��Ϣ
	public final static String ACTIVITY_REQUEST_PROGRESS = "REQUEST_PROGRESS";       //���������
	public final static String ACTIVITY_REQUEST_PLAY_LIST = "REQUEST_PLAY_LIST";     //���󲥷��б�
	public final static String ACTIVITY_REQUEST_INITIALIZE = "REQUEST_INITIALIZE";   //����Mp3�б��������
	
	public final static String ACTIVITY_CMD_PROGRESS = "ACTIVITY_CMD_PROGRESS";
	public final static String ACTIVITY_CMD_CURRENT = "ACTIVITY_CMD_CURRENTS";
	
	//�㲥���� ��Service��Activity����
	public final static String SERVICE_RETURN_MP3_INFO = "RETURN_MP3_INFO";     //���ص�ǰ���ŵ�״̬
	public final static String SERVICE_RETURN_PROGRESS = "RETURN_PROGRESS";      //���ص�ǰ������
	public final static String SERVICE_RETURN_PLAY_LIST = "RETURN_PLAY_LIST";    //�������ڲ����б�
	public final static String SERVICE_REQUEST_MP3_LIST = "REQUEST_MP3_LIST";    //��������Mp3�б�
	public final static String SERVICE_RETURN_INITIALIZE = "RETURN_INITIALIZE";  //���س�ʼ����Ϣ
	
	//Intent�з��õ�Extra����
	public final static String DATA_PLAY_LIST    = "DATA_PLAY_LIST";          //�����б�
	public final static String DATA_PLAY_STATUS  = "DATA_PLAY_STATUS";        //����״̬
	public final static String DATA_MP3_INFO     = "DATA_MP3_INFO";           //���ڲ��ŵ�Mp3��Ϣ
	public final static String DATA_MP3_DURATION = "DATA_MP3_DURATION";       //Mp3����
	public final static String DATA_PROGRESS     = "DATA_PROGRESS";           //Mp3����
	public final static String DATA_PLAY_MODE    = "DATA_PLAY_MODE";          //����ѭ��ģʽ
	public final static String DATA_CURRENT      = "DATA_CURRENT";            //��������
	
	public final static String DATA_MP3_LIST     = "DATA_MP3_LIST";
	public final static String DATA_ARTIST_LIST  = "DATA_ARTIST_LIST";
	public final static String DATA_ARTIST_MAP   = "DATA_ARTIST_MAP";
	
}
