#include <Arduino.h>
#include "ESP8266WiFi.h"
#include <PubSubClient.h>
#include "Ticker.h"
#include <SoftwareSerial.h>
String ssid= "2024412";
String password = "2024412!!";
const char* mqtt_server = "mqtts.heclouds.com";
const int mqtt_port = 1883;
const char* deviceID = "Arduino";  // 您的设备ID
const char* productID = "0w6evrQh6e";  // 您的产品ID
const char* apiKey = "version=2018-10-31&res=products%2F0w6evrQh6e%2Fdevices%2FArduino&et=1744446629&method=md5&sign=xSq6x%2BYN4VvfpykYYMrEOg%3D%3D";  // 您的APIKey
char receivedChars[20]; // 存储接收到的字符串
const String identifiers[] = {"Hum","Tem","PM"};

String commandTopic = "$sys/" + String(productID) + "/" + String(deviceID) + "/thing/property/set";

Ticker tim1;      //定时器,用来循环上传数据
WiFiClient espClient;
PubSubClient OneNET(espClient);

void setup_wifi(String ssid,String password){
  WiFi.begin(ssid, password);
  static uint8_t count = 0;
  Serial.print("WiFi connecting");
   while (WiFi.status() != WL_CONNECTED) {
    if(++count >= 25) break;
    delay(500);
    Serial.print(".");
  }
  if(WiFi.status() == WL_CONNECTED){
    Serial.println("");
    Serial.println("WiFi connected!");
    Serial.println("IP: ");
    Serial.println(WiFi.localIP());
  }else if(WiFi.status() != WL_CONNECTED){
    Serial.println("");
    Serial.println("WiFi connected fail!");
  }
}
void reconnect() {
    while (!OneNET.connected()) {
    Serial.print("Attempting MQTT connection...");
    // 尝试连接
    if (OneNET.connect(deviceID, productID, apiKey)) {
      Serial.println("connected");
      // 一旦连接上，订阅相应的主题
      OneNET.subscribe(commandTopic.c_str());
    } else {
      Serial.print("failed, rc=");
      Serial.print(OneNET.state());
      Serial.println(" try again in 5 seconds");
      delay(5000);
    }
  }
}
void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.println("] ");
  for (size_t i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
  }
  Serial.println();
}
void publishSensorData() {
  String jsonData = "{\"id\":\"123\",\"params\":{";
  // 首先处理数字类型的数据
  for(int i = 0; i < 6; i+=2) {
    jsonData += "\"" + identifiers[i/2] + "\":{\"value\":" + receivedChars[i]+receivedChars[i+1] + "}";
    if (i < 3) { // 如果后面还有数据，就添加一个逗号
      jsonData += ",";
    }
  }

  jsonData += "}}";
  // MQTT主题，假设已经定义
  String topic = "$sys/0w6evrQh6e/Arduino/thing/property/post";
  
  Serial.println(jsonData); // 输出验证
  
  // 将String类型的JSON数据转换为字符数组
  char jsonBuffer[jsonData.length() + 1];
  jsonData.toCharArray(jsonBuffer, sizeof(jsonBuffer));

  // 假设OneNET.publish函数和Serial对象已经定义
  if (!OneNET.publish(topic.c_str(), jsonBuffer)) {
    Serial.println("数据发布失败");
  } else {
    Serial.println("数据发布成功");
  }
}
void setup() {
  Serial.begin(9600);
  
  setup_wifi(ssid,password);
  OneNET.setServer(mqtt_server, mqtt_port);
  OneNET.setCallback(callback);
  // 连接MQTT
  if (OneNET.connect(deviceID, productID, apiKey)) {
    Serial.println("MQTT Connected");

    // 订阅命令下发Topic
    if(OneNET.subscribe(commandTopic.c_str())) {
      Serial.println("Subscribed to command topic");
    } else {
      Serial.println("Subscription failed");
    }
  }

  tim1.attach(2, publishSensorData); //定时每20秒调用一次发送数据函数publishSensorData

}
void loop() {

  receivedChars[20]={};
  static int charIndex = 0; // 数组索引初始化
  while (Serial.available() > 0 && charIndex < sizeof(receivedChars) - 1) {
    char incomingChar = Serial.read(); // 读取一个字符
    if (incomingChar == '\n') { // 如果读取到换行符，假设这是字符串的结束
      receivedChars[charIndex] = '\0'; // 在字符串末尾添加 null 字符
      charIndex = 0; // 重置索引，准备下一次接收
    } else {
      receivedChars[charIndex++] = incomingChar; // 将字符存储在数组中
    }
  }

  if (!OneNET.connected()) {
      reconnect();
 }
  OneNET.loop(); // 处理接收到的消息和保持MQTT连接
       // 模拟数据
 
}

