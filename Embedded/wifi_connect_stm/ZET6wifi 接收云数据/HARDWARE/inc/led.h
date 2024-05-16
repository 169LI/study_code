#ifndef _LED_H_
#define _LED_H_

#include "sys.h"
#define LED0 PBout(5)// PB5


typedef struct
{

	_Bool Led_Status;

} LED_INFO;

#define LED_ON		1

#define LED_OFF	0

extern LED_INFO led_info;


void Led_Init(void);

void Led_Set(_Bool status);


#endif
