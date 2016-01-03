package org.spectrum3847.RIOdroid;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import edu.wpi.first.wpilibj.Timer;

public class RIOdroid {

	private RIOdroid() {
		//STATIC CLASS CAN'T BE CALLED
	}
	
	public static void init(){
		RIOdroid.initUSB(); //Start LibUsb
        RIOadb.init();      //Start up ADB deamon and get an instance of jadb
        Timer.delay(1);
        System.out.println(RIOadb.clearNetworkPorts());
	}
	
	public static String executeCommand(String command) {

		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = 
                            new BufferedReader(new InputStreamReader(p.getInputStream()));

                        String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return output.toString();

	}
	
	/**
	 * Start a command in it's own thread
	 * @param command
	 * @return
	 */
	public static String executeCommandThread(String command){
		new Thread(new CommandThread(command)).start();
		return "";
	}
	
	/**
	 * initUSB()
	 * Initialize LibUSB
	 * throw LibUSBException if it fails
	 */
	public static void initUSB(){
		Context context = new Context();
        int result = LibUsb.init(context);
        System.out.println("LibUSB Init Complete");
        if (result != LibUsb.SUCCESS) throw new LibUsbException("Unable to initialize libusb.", result);
	}
	
	/**
	 * Find Device based on vendorID and productID
	 * @param vendorId
	 * @param productId
	 * @return Device
	 */
	public static Device findDevice(short vendorId, short productId)
	{
	    // Read the USB device list
	    DeviceList list = new DeviceList();
	    int result = LibUsb.getDeviceList(null, list);
	    if (result < 0) throw new LibUsbException("Unable to get device list", result);

	    System.out.println("USB DEVICE LIST COMPLETE");
	   
	    try
	    {
	        // Iterate over all devices and scan for the right one
	        for (Device device: list)
	        {
	            DeviceDescriptor descriptor = new DeviceDescriptor();
	            result = LibUsb.getDeviceDescriptor(device, descriptor);
	            if (result != LibUsb.SUCCESS) throw new LibUsbException("Unable to read device descriptor", result);
	            if (descriptor.idVendor() == vendorId && descriptor.idProduct() == productId) return device;
	        }
	    }
	    finally
	    {
	        // Ensure the allocated device list is freed
	        LibUsb.freeDeviceList(list, true);
	    }

	    // Device not found
	    return null;
	}
	
	public static DeviceHandle openDevice(int VID, int PID){
		DeviceHandle handle = new DeviceHandle();
			if((handle = LibUsb.openDeviceWithVidPid(null, (short)VID, (short)PID)) == null){
				throw new LibUsbException("Problem acquireing handle", -1);
			}
			LibUsb.claimInterface(handle, 0);
			return handle;
	}
	
	public static DeviceHandle openDevice(Device device){
		DeviceHandle handle = new DeviceHandle();
		int result = LibUsb.open(device, handle);
		if (result != LibUsb.SUCCESS) throw new LibUsbException("Unable to open USB device", result);
		LibUsb.claimInterface(handle, 0);
		return handle;
	}
	
	
	public static ByteBuffer stringToByteBuffer(String s){
		ByteBuffer buffer = ByteBuffer.allocateDirect(s.getBytes().length);
	    buffer.put(s.getBytes());
	    return buffer;
	}
	
	
	
}

class CommandThread implements Runnable {
	private String out ="";
	public CommandThread(String command){
		out = command;
	}
	
	public void run() {
		RIOdroid.executeCommand(out);
	}
}
