/**
	************************************************************
	************************************************************
	************************************************************
	*	�ļ����� 	main.c
	*
	*	���ߣ� 		�ż���
	*
	*	���ڣ� 		2017-05-08
	*
	*	�汾�� 		V1.0
	*
	*	˵���� 		����onenet���ϴ����ݺ��������
	*
	*	�޸ļ�¼��	
	************************************************************
	************************************************************
	************************************************************
**/

//��Ƭ��ͷ�ļ�
#include "stm32f10x.h"

//����Э���
#include "onenet.h"

//�����豸
#include "esp8266.h"

//Ӳ������
#include "delay.h"
#include "usart.h"
#include "led.h"
#include "key.h"
#include "dht11.h"

//C��
#include <string.h>

#define ESP8266_ONENET_INFO		"AT+CIPSTART=\"TCP\",\"mqtts.heclouds.com\",1883\r\n"


/*
************************************************************
*	�������ƣ�	Hardware_Init
*
*	�������ܣ�	Ӳ����ʼ��
*
*	��ڲ�����	��
*
*	���ز�����	��
*
*	˵����		��ʼ����Ƭ�������Լ�����豸
************************************************************
*/
void Hardware_Init(void)
{
	
	NVIC_PriorityGroupConfig(NVIC_PriorityGroup_2);	//�жϿ�������������

	Delay_Init();									//systick��ʼ��
	
	Usart1_Init(115200);							//����1����ӡ��Ϣ��
	
	Usart2_Init(115200);							//����2������ESP8266��
	UsartPrintf(USART_DEBUG, " Hardware init OK\r\n");
  Key_Init();
	
	Led_Init();									//��������ʼ��
	
//	while(DHT11_Init())
//	{
//		UsartPrintf(USART_DEBUG, "DHT11 Error \r\n");
//		DelayMs(1000);
//	}
	
	
	
}

/*
************************************************************
*	�������ƣ�	main
*
*	�������ܣ�	
*
*	��ڲ�����	��
*
*	���ز�����	0
*
*	˵����		
************************************************************
*/
u8 temp;
u8 humi;
int main(void)
{
	
	unsigned short timeCount = 0;	//���ͼ������
	
	unsigned char *dataPtr = NULL;
	
	Hardware_Init();				//��ʼ����ΧӲ��
	

	ESP8266_Init();					//��ʼ��ESP8266

	UsartPrintf(USART_DEBUG, "Connect MQTTs Server...\r\n");
	while(ESP8266_SendCmd(ESP8266_ONENET_INFO, "CONNECT"))
		DelayXms(500);
	UsartPrintf(USART_DEBUG, "Connect MQTT Server Success\r\n");
//	
	while(OneNet_DevLink())			//����OneNET
		DelayXms(500);

	OneNET_Subscribe();//��������  ���ܽ��յ�ƽ̨����Ϣ��
	while(1)
	{
		//DHT11_Read_Data(&temp,&humi);//
		//UsartPrintf(USART_DEBUG, "P4****temp %d ,humi %d\r\n",temp,humi);
		
		if(++timeCount >= 500)									//���ͼ��5s
		{
//			SHT20_GetValue();
			
			UsartPrintf(USART_DEBUG, "OneNet_SendData\r\n");
			OneNet_SendData();									//��������
			
			timeCount = 0;
			ESP8266_Clear();
		}
		
		dataPtr = ESP8266_GetIPD(0);
		if(dataPtr != NULL)
			OneNet_RevPro(dataPtr);//��������
		DelayMs(10);
	
	}

}
