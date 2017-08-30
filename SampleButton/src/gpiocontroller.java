
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.*;
import com.pi4j.io.gpio.trigger.*;
import com.pi4j.component.motor.impl.*;
import java.util.concurrent.*;

/**
 * This example code demonstrates how to perform simple state
 * control of a GPIO pin on the Raspberry Pi.  
 * Generated by pi4j Code Generator developed by ANCIT Consulting
 * @author Robert Savage
 */

public class gpiocontroller{
	
	public static void main(String[] args) throws InterruptedException {
		// create gpio controller
        final GpioController gpio = GpioFactory.getInstance();
        
            // provision gpio pin #02 as an input pin with its internal pull down resistor enabled
        final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_24, PinPullResistance.PULL_DOWN);
         
         
        
             // create and register gpio pin listener
        myButton.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {  
             
            } 
        });
				
		
		
          // keep program running until user aborts (CTRL-C)
        for (;;) {
            Thread.sleep(500);	
        }
	}   
}		
