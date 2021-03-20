/*---------------------------------------------------------------------------*/
/**
 * \file
 * Project specific configuration defines for the MQTT demo
 */
/*---------------------------------------------------------------------------*/
#ifndef PROJECT_CONF_H_
#define PROJECT_CONF_H_
/*---------------------------------------------------------------------------*/
/* Enable TCP */
#define UIP_CONF_TCP 1
/*---------------------------------------------------------------------------*/
/* User configuration */
/*---------------------------------------------------------------------------*/
#define MQTT_DEMO_STATUS_LED  LEDS_GREEN
#define MQTT_DEMO_TRIGGER_LED LEDS_RED
#define MQTT_DEMO_PUBLISH_TRIGGER &button_left_sensor

#define MQTT_DEMO_PUBLISH_TOPIC   "AccordiBurattiMotta-Topic/contact/region2/json"
#define MQTT_DEMO_SUB_TOPIC       "AccordiBurattiMotta-Topic/notif/sensor"
#define MQTT_DEMO_SUB_TOPIC_END   "/json" 

#define MQTT_DEMO_BROKER_IP_ADDR "fd00::1"
//*---------------------------------------------------------------------------*/
#define IEEE802154_CONF_DEFAULT_CHANNEL      21
//*---------------------------------------------------------------------------*/
#endif /* PROJECT_CONF_H_ */
/*---------------------------------------------------------------------------*/
/** @} */
