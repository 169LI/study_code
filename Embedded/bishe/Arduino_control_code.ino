
#include "DHT.h"
#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>

#define OLED_RESET     4
static const unsigned char PROGMEM cnWord[] = 
{
 0x00,0x00,0x18,0x00,0x24,0x00,0x24,0x00,0x18,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
};


#define DHTPIN 5       //定义DHT传感器连接的引脚。它连接到数字引脚2
#define DHTTYPE DHT11  //定义正在使用的DHT传感器类型

#define        COV_RATIO                       0.2            //ug/mmm / mv
#define        NO_DUST_VOLTAGE                 400            //mv
#define        SYS_VOLTAGE                     5000           

const int iled = 2;                                            //drive the led of sensor
const int vout = A5; 
int h,t,ugm3;
float density, voltage;
int   adcvalue;
DHT dht(DHTPIN, DHTTYPE);

Adafruit_SSD1306 display(128, 64, &Wire,OLED_RESET);
int Filter(int m)
{
  static int flag_first = 0, _buff[10], sum;
  const int _buff_max = 10;
  int i;
  
  if(flag_first == 0)
  {
    flag_first = 1;

    for(i = 0, sum = 0; i < _buff_max; i++)
    {
      _buff[i] = m;
      sum += _buff[i];
    }
    return m;
  }
  else
  {
    sum -= _buff[0];
    for(i = 0; i < (_buff_max - 1); i++)
    {
      _buff[i] = _buff[i + 1];
    }
    _buff[9] = m;
    sum += _buff[9];
    
    i = sum / 10.0;
    return i;
  }
}

void setdisplay(){
  display.clearDisplay();//清屏
  display.setTextSize(1); //设置字体大小 

  display.setCursor(20, 5);//设置显示位置
  display.print("Humidity: ");
  display.setCursor(80, 5);//设置显示位置
  display.print(h);
  display.setCursor(95, 5);//设置显示位置
  display.print("%");
  
  display.setCursor(4, 20);//设置显示位置
  display.print("Temperature: ");
  display.setCursor(80, 20);//设置显示位置
  display.print(t);
  display.drawBitmap(94,20, cnWord, 16,16, 1);
  display.setCursor(100, 20);//设置显示位置
  display.print("C");

  display.setCursor(40, 35);//设置显示位置
  display.print("PM2.5:");
  display.setCursor(65, 35);//设置显示位置
  display.print(ugm3);
  display.setCursor(95, 35);//设置显示位置
  display.print("ug/m3");

  display.display(); //启用显示
}
void setup(){
  pinMode(iled, OUTPUT);
  digitalWrite(iled, LOW);                                     //iled default closed
  Serial.begin(9600);
  dht.begin();
  display.begin(SSD1306_SWITCHCAPVCC,0x3C);//打开显示屏
  display.setTextColor(WHITE);//设置输出为白色
  display.clearDisplay();//清屏
}

void loop() {

  delay(1000);
   h = dht.readHumidity();
   Serial.println(h);
   t = dht.readTemperature();
   ugm3 = (int)density;
  Serial.println(ugm3);
  setdisplay();
  char buffer[20]; // 确保缓冲区足够大以存储结果字符串
  // 使用 sprintf 将整数格式化到字符串中
  sprintf(buffer, "%d%d%d", h,ugm3, t);
  // 发送字符串到串口
  Serial.println(buffer);

}
