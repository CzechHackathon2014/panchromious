#include <Wire.h>
#include "Adafruit_TCS34725.h"

/* Example code for the Adafruit TCS34725 breakout library */

/* Connect SCL    to analog 5
   Connect SDA    to analog 4
   Connect VDD    to 3.3V DC
   Connect GROUND to common ground */
   
/* Initialise with default values (int time = 2.4ms, gain = 1x) */
// Adafruit_TCS34725 tcs = Adafruit_TCS34725();

/* Initialise with specific int time and gain values */
Adafruit_TCS34725 tcs = Adafruit_TCS34725(TCS34725_INTEGRATIONTIME_700MS, TCS34725_GAIN_1X);


int led = 13;
int button = 2;
int light = 3;

void setup(void) {
  
  Serial.begin(9600);
  
  pinMode(led, OUTPUT);
  digitalWrite(led, LOW);
  
  pinMode(button, INPUT);  
  digitalWrite(button, HIGH);  
  
  pinMode(light, OUTPUT);
  digitalWrite(light, LOW);
  
  if (!tcs.begin()) {
    while(1) {
      digitalWrite(led, HIGH);
      delay(50);
      digitalWrite(led, LOW);
      delay(100);
    }
  } 
 
// tcs.setIntegrationTime(TCS34725_INTEGRATIONTIME_101MS); 
}

int seq;

void loop(void) {
  if (Serial.available()) {
    Serial.read();
  }

    // look for the next valid integer in the incoming serial stream:
    
  if (digitalRead(button) == LOW) {
    digitalWrite(light, LOW);
    digitalWrite(led, HIGH);
    delay(2);
  
    uint16_t r, g, b, c, colorTemp, lux;
  
    tcs.getRawData(&r, &g, &b, &c);
    //colorTemp = tcs.calculateColorTemperature(r, g, b);
    //lux = tcs.calculateLux(r, g, b);
    digitalWrite(light, LOW);
  
    uint32_t sum = c;
    float red = r; red = red / sum * 256;
    float green = g; green = green / sum * 256;
    float blue = b; blue = blue / sum * 256;     
        
    
//    Serial.print(seq++,DEC);Serial.print(" ");
//    Serial.print(r);Serial.print(" ");
//    Serial.print(g);Serial.print(" ");
//    Serial.print(b);Serial.print(" ");
//    Serial.print(c);Serial.print(" --- ");    
    Serial.print((int)red,DEC);Serial.print(" ");
    Serial.print((int)green,DEC);Serial.print(" ");
    Serial.print((int)blue,DEC);Serial.print(" ");
    Serial.println("");
    digitalWrite(led, LOW);
    long t0 = millis();
    while(millis() - t0 < 750 && digitalRead(button) == LOW) {
      delay(10);
    }
  }
  
//  Serial.print("Color Temp: "); Serial.print(colorTemp, DEC); Serial.print(" K - ");
//  Serial.print("Lux: "); Serial.print(lux, DEC); Serial.print(" - ");
//  Serial.print("R: "); Serial.print(r, DEC); Serial.print(" ");
//  Serial.print("G: "); Serial.print(g, DEC); Serial.print(" ");
//  Serial.print("B: "); Serial.print(b, DEC); Serial.print(" ");
//  Serial.print("C: "); Serial.print(c, DEC); Serial.print(" ");
//  Serial.println(" ");
}
