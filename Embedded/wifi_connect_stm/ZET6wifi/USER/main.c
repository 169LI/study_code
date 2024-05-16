#include "led.h"
#include "delay.h"
#include "key.h"
#include "sys.h"
#include "usart.h"
#include "timer.h"
#include "ws2818.h"
 #include "WS2812B.h"
 #include "beep.h"
 //网络协议层
#include "onenet.h"

//网络设备
#include "esp8266.h"
//C库
#include <string.h>

	u8 n=10;	 
	
 int main(void)
 {		
	 const char *devSubTopic[]={"/mysmarhome/sub"};//订阅
	 const char devPubTopic[]="/mysmarhome/pub";//查看
	 unsigned short timeCount = 0;	//发送间隔变量	
	 unsigned char *dataPtr = NULL;
		u8 but[128];
   u16 led0pwmval=0;
	 u8 dir=1,i;
	  NVIC_PriorityGroupConfig(NVIC_PriorityGroup_2);  //中断分组；
	 LED_Init();  BEEP_Init();
	 delay_init();	    	 //延时函数初始化	

	 Usart1_Init(115200);
	 Usart2_Init(115200);	 
	 KEY_EXTI();


	 	ESP8266_Init();					//初始化ESP8266
	 	while(OneNet_DevLink())			//接入OneNET
		delay_ms(500);
		
		BEEP=1;
		delay_ms(250);
		BEEP=0;
		
		OneNet_Subscribe(devSubTopic, 1);
   while(1)
	{



		if(++timeCount >= 200)									//发送间隔5s
		{
			UsartPrintf(USART_DEBUG, "OneNet_Publish\r\n");//串口一
			sprintf(but,"WenDu: %d, ShiDu: %d",20,60);
			OneNet_Publish(devPubTopic, but);//向订阅号发送MQTT Publish Test数据
			
			timeCount = 0;
			ESP8266_Clear();
		}
		
		dataPtr = ESP8266_GetIPD(3);
		
		if(dataPtr != NULL)
			OneNet_RevPro(dataPtr);
		
		delay_ms(10);
		
 }
}
