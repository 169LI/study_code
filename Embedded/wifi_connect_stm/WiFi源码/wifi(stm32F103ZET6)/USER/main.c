#include "led.h"
#include "delay.h"
#include "key.h"
#include "sys.h"
#include "usart.h"
#include "timer.h"
#include "ws2818.h"
 #include "WS2812B.h"
 #include "beep.h"
 //����Э���
#include "onenet.h"

//�����豸
#include "esp8266.h"
//C��
#include <string.h>

	u8 n=10;	 
	
 int main(void)
 {		
	 const char *devSubTopic[]={"/mysmarhome/sub"};//����
	 const char devPubTopic[]="/mysmarhome/pub";//�鿴
	 unsigned short timeCount = 0;	//���ͼ������	
	 unsigned char *dataPtr = NULL;
		u8 but[128];
   u16 led0pwmval=0;
	 u8 dir=1,i;
	  NVIC_PriorityGroupConfig(NVIC_PriorityGroup_2);  //�жϷ��飻
	 LED_Init();  BEEP_Init();
	 delay_init();	    	 //��ʱ������ʼ��	

	 Usart1_Init(115200);
	 Usart2_Init(115200);	 
	 KEY_EXTI();


	 	ESP8266_Init();					//��ʼ��ESP8266
	 	while(OneNet_DevLink())			//����OneNET
		delay_ms(500);
		
		BEEP=1;
		delay_ms(250);
		BEEP=0;
		
		OneNet_Subscribe(devSubTopic, 1);
   while(1)
	{



		if(++timeCount >= 200)									//���ͼ��5s
		{
			UsartPrintf(USART_DEBUG, "OneNet_Publish\r\n");//����һ
			sprintf(but,"WenDu: %d, ShiDu: %d",20,60);
			OneNet_Publish(devPubTopic, but);//���ĺŷ���MQTT Publish Test����
			
			timeCount = 0;
			ESP8266_Clear();
		}
		
		dataPtr = ESP8266_GetIPD(3);
		
		if(dataPtr != NULL)
			OneNet_RevPro(dataPtr);
		
		delay_ms(10);
		
 }
}
