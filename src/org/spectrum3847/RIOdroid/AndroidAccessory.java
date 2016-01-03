package org.spectrum3847.RIOdroid;

/**
* BASED ON CODE FORM  Android.servbox.ch
* http://android.serverbox.ch/?p=262
* 
 * simplectrl.c
 * This file is part of OsciPrime
 *
 * Copyright (C) 2011 - Manuel Di Cerbo
 *
 * OsciPrime is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * OsciPrime is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OsciPrime; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, 
 * Boston, MA  02110-1301  USA
 *
* 
* Modified by Spectrum 3847
* www.spectrum3847.org
**/

import java.nio.ByteBuffer;

import org.usb4java.BufferUtils;
import org.usb4java.DeviceHandle;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

public class AndroidAccessory {
	

private static int IN = 0x85;
private static int OUT = 0x07;

//These are determined per device
//Should be a way to get them from libusb or adb
private int m_VID = 0x22b8;
private int m_PID = 0x2e76;

private static int LEN = 2;

private String m_manufacturer;
private String m_modelName;
private String m_description;
private String m_version;
private String m_uri;
private String m_serialNumber;

/**
* BASED ON CODE FORM  Android.servbox.ch
* http://android.serverbox.ch/?p=262
**/

//static
static DeviceHandle m_handle;
static char stop;
static char success = 0;

public AndroidAccessory(
		String manufacturer,
		String modelName,
		String description,
		String version,
		String uri,
		String serialNumber){

	m_manufacturer = manufacturer;
	m_modelName = modelName;
	m_description = description;
	m_version = version;
	m_uri = uri;
	m_serialNumber= serialNumber;
	}

public void setupAccessory(DeviceHandle handle) {
	ByteBuffer ioBuffer = BufferUtils.allocateByteBuffer(2); //unsigned char ioBuffer[2];
	int devVersion;
	int response;
	int tries = 5;

	response = LibUsb.controlTransfer(
		handle, //handle
		(byte) 0xC0, //bmRequestType
		(byte) 51, //bRequest
		(short) 0, //wValue
		(short) 0, //wIndex
		ioBuffer, //data
        (long) 0 //timeout
	);

	if(response < 0){error(response);}
	devVersion = ioBuffer.get(1) << 8 | ioBuffer.get(0);
	System.out.printf("Verion Code Device: %d \n", devVersion);
	
	//May have to put back in a short pause
	//usleep(1000);//sometimes hangs on the next transfer :(
    
	response = LibUsb.controlTransfer(handle,(byte) 0x40,(byte) 52, (short) 0, (short) 0,RIOdroid.stringToByteBuffer(m_manufacturer),0);
	if(response < 0){error(response);}
	response = LibUsb.controlTransfer(handle,(byte) 0x40,(byte) 52, (short) 0,(short) 1, RIOdroid.stringToByteBuffer(m_modelName),0);
	if(response < 0){error(response);}
	response = LibUsb.controlTransfer(handle,(byte) 0x40,(byte) 52,(short) 0,(short) 2, RIOdroid.stringToByteBuffer(m_description),0);
	if(response < 0){error(response);}
	response = LibUsb.controlTransfer(handle,(byte) 0x40,(byte) 52,(short) 0,(short) 3, RIOdroid.stringToByteBuffer(m_version),0);
	if(response < 0){error(response);}
	response = LibUsb.controlTransfer(handle,(byte) 0x40,(byte) 52,(short) 0,(short) 4, RIOdroid.stringToByteBuffer(m_uri),0);
	if(response < 0){error(response);}
	response = LibUsb.controlTransfer(handle,(byte) 0x40,(byte) 52,(short) 0,(short) 5, RIOdroid.stringToByteBuffer(m_serialNumber),0);
	if(response < 0){error(response);}

	System.out.println("Accessory Identification sent\n");

	response = LibUsb.controlTransfer(handle,(byte) 0x40,(byte) 53,(short)0,(short)0, RIOdroid.stringToByteBuffer(" "),0);
	if(response < 0){error(response);}

	System.out.println("Attempted to put device into accessory mode\n");

	if(handle != null)
		LibUsb.releaseInterface (handle, 0);


	/*for(;;){
		tries--;
		if((handle = LibUsb.openDeviceWithVidPid(null, (short) m_VID, (short) m_PID)) == null){
			if(tries < 0){
				throw new LibUsbException("Unable to Open Device \n", 1);
			}
		}else{
			break;
		}
	}*/
	m_handle = handle;
	LibUsb.claimInterface(m_handle, 0);
	System.out.println("Interface claimed, ready to transfer data\n");
}

/*public static int main (){
	System.out.println("STARTING ANDROID MAIN");
	if(init() < 0){
		System.out.println("Failed to Init USB");
		return 0;
	}
	System.out.println("ANDROID FINISHED INIT");
	
	//doTransfer();
	if(setupAccessory(
			"Manufacturer",
			"Model",
			"Description",
			"VersionName",
			"http://Spectrum3847.org/RIOdroid",
			"3847SerialNo.") < 0){
		System.out.println("Error setting up accessory");
		deInit();
		return -1;
	};
	
	System.out.println("ANDROID FINISHED SETUP");
	
	if(mainPhase() < 0){
		System.out.println("Error during main phase");
		deInit();
		return -1;
	}	
	deInit();
	System.out.println("Done, no errors");
	return 0;
}*/

/*static int mainPhase(){
	ByteBuffer buffer = ByteBuffer.allocate(5000);
	int response = 0;
	IntBuffer trans = IntBuffer.allocate(5000);

	response = LibUsb.bulkTransfer(handle,(byte) IN,buffer, trans, 0);
	if(response < 0){error(response);return -1;}

	response = LibUsb.bulkTransfer(handle,(byte) IN,buffer, trans,0);
	if(response < 0){error(response);return -1;}
	return 0;
}*/

private int init(){
	LibUsb.init(null);
	if((m_handle = LibUsb.openDeviceWithVidPid(null, (short)m_VID, (short)m_PID)) == null){
		System.out.println("Problem acquireing handle");
		return -1;
	}
	LibUsb.claimInterface(m_handle, 0);
	return 0;
}

private int deInit(){
	//TODO free all transfers individually...
	//if(ctrlTransfer != NULL)
	//	libusb_free_transfer(ctrlTransfer);
	if(m_handle != null)
		LibUsb.releaseInterface (m_handle, 0);
	LibUsb.exit(null);
	return 0;
}


static void error(int code){
	switch(code){
	case LibUsb.ERROR_IO:
		throw new LibUsbException("Error: LIBUSB_ERROR_IO\nInput/output error.\n", code);
	case LibUsb.ERROR_INVALID_PARAM:
		throw new LibUsbException("Error: LIBUSB_ERROR_INVALID_PARAM\nInvalid parameter.\n", code);
	case LibUsb.ERROR_ACCESS:
		throw new LibUsbException("Error: LIBUSB_ERROR_ACCESS\nAccess denied (insufficient permissions).\n", code);
	case LibUsb.ERROR_NO_DEVICE:
		throw new LibUsbException("Error: LIBUSB_ERROR_NO_DEVICE\nNo such device (it may have been disconnected).\n", code);
	case LibUsb.ERROR_NOT_FOUND:
		throw new LibUsbException("Error: LIBUSB_ERROR_NOT_FOUND\nEntity not found.\n", code);
	case LibUsb.ERROR_BUSY:
		throw new LibUsbException("Error: LIBUSB_ERROR_BUSY\nResource busy.\n", code);
	case LibUsb.ERROR_TIMEOUT:
		throw new LibUsbException("Error: LIBUSB_ERROR_TIMEOUT\nOperation timed out.\n", code);
	case LibUsb.ERROR_OVERFLOW:
		throw new LibUsbException("Error: LIBUSB_ERROR_OVERFLOW\nOverflow.\n", code);
	case LibUsb.ERROR_PIPE:
		throw new LibUsbException("Error: LIBUSB_ERROR_PIPE\nPipe error.\n", code);
	case LibUsb.ERROR_INTERRUPTED:
		throw new LibUsbException("Error:LIBUSB_ERROR_INTERRUPTED\nSystem call interrupted (perhaps due to signal).\n", code);
	case LibUsb.ERROR_NO_MEM:
		throw new LibUsbException("Error: LIBUSB_ERROR_NO_MEM\nInsufficient memory.\n", code);
	case LibUsb.ERROR_NOT_SUPPORTED:
		throw new LibUsbException("Error: LIBUSB_ERROR_NOT_SUPPORTED\nOperation not supported or unimplemented on this platform.\n", code);
	case LibUsb.ERROR_OTHER:
		throw new LibUsbException("Error: LIBUSB_ERROR_OTHER\nOther error.\n", code);
	default:
		throw new LibUsbException("Error: unkown error\n", code);
	}
}

static void status(int code){
	switch(code){
		case LibUsb.TRANSFER_COMPLETED:
			System.out.println("Success: LIBUSB_TRANSFER_COMPLETED\nTransfer completed.\n");
			break;
		case LibUsb.TRANSFER_ERROR:
			System.out.println("Error: LIBUSB_TRANSFER_ERROR\nTransfer failed.\n");
			break;
		case LibUsb.TRANSFER_TIMED_OUT:
			System.out.println("Error: LIBUSB_TRANSFER_TIMED_OUT\nTransfer timed out.\n");
			break;
		case LibUsb.TRANSFER_CANCELLED:
			System.out.println("Error: LIBUSB_TRANSFER_CANCELLED\nTransfer was cancelled.\n");
			break;
		case LibUsb.TRANSFER_STALL:
			System.out.println("Error: LIBUSB_TRANSFER_STALL\nFor bulk/interrupt endpoints: halt condition detected (endpoint stalled).\nFor control endpoints: control request not supported.\n");
			break;
		case LibUsb.TRANSFER_NO_DEVICE:
			System.out.println("Error: LIBUSB_TRANSFER_NO_DEVICE\nDevice was disconnected.\n");
			break;
		case LibUsb.TRANSFER_OVERFLOW:
			System.out.println("Error: LIBUSB_TRANSFER_OVERFLOW\nDevice sent more data than requested.\n");
			break;
		default:
			System.out.println("Error: unknown error\nTry again(?)\n");
			break;
	}
}

}
