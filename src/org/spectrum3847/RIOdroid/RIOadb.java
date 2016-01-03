package org.spectrum3847.RIOdroid;

import java.io.IOException;
import java.util.List;

import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;

public class RIOadb {

	private static JadbConnection m_jadb = null;
    private static List<JadbDevice> m_devices = null;
    private static JadbDevice m_currentDevice = null;
    private static int m_nextLocalHostPort = 3800;
	
    private RIOadb() {
		//STATIC CLASS CAN'T BE CALLED
	}
	
	/**
	 * Attempt to make a connection to the ADB server running on the roboRIO
	 * Uses an adb start script that RIOdroid copies to the /etc/init.d folder
	 */
	public static void init(){
		System.out.println(RIOdroid.executeCommand("/etc/init.d/adb.sh start")); //Start the deamon
		//Might need to wait here for deamon to start
		try {
			m_jadb = new JadbConnection();
		} catch (IOException e) {
	        System.out.println("Failed at connection");
			e.printStackTrace();
		}
		setCurrentDevice();
	}
	
	/**
	 * Get a device list
	 * @return
	 */
	public static List<JadbDevice> getDevicesList(){
		if (m_jadb != null){
			try {
				m_devices = m_jadb.getDevices();
				return m_devices;
			} catch (IOException | JadbException e) {
		        System.out.println("Failed at device list");
				e.printStackTrace();
			}
		} else {
			System.out.println("Failed to get device list");
		}
		return null;
	}
	
	/**
	 * Set the current device to use
	 * @param device
	 */
	public static void setCurrentDevice(JadbDevice device){
		m_currentDevice = device;
	}
	
	/**
	 * If no device passed, just use the first device from the list
	 */
	public static void setCurrentDevice(){
		getDevicesList();
		if (m_devices != null){
			setCurrentDevice(m_devices.get(0));
		}
	}
	
	/**
	 * Capture the screen
	 * @param filePath
	 */
	public static void screencap(String filePath){
		
			try {
				m_currentDevice.executeShell("screencap", filePath);
			} catch (IOException | JadbException e) {
		        System.out.println("Failed to take screencap");
				e.printStackTrace();
			}
		
	}
	
	/**
	 * Execute a shell command on the current device
	 * @param command
	 * @param args
	 * @return
	 */
	public static String executeShell(String command, String ... args){
		if (m_currentDevice != null){
			try {
				m_currentDevice.executeShell(command, args);
			}
			catch (IOException | JadbException e) {
				String out = "Failed: " + command;
		        System.out.println(out);
				e.printStackTrace();
				return out;
			}	
		} else {
			return "Current Device is null";
		}
		return command + " " + args + " COMPLETE";
	}
	
	/**
	 * Forward a local host port from the roborio to the android device connected to the roborio
	 * @param roboRioPort
	 * @param devicePort
	 * @return
	 */
	public static String ForwardAdb(int roboRioPort, int devicePort){
		if (m_jadb != null){
				return RIOdroid.executeCommand("adb forward tcp:" + roboRioPort + " tcp:" + devicePort);
		} else {
			System.out.println("Current adb connection is null");
		}
		return null;
	}
	
	/**
	 * Forward an external port of the roborio to roborio local port
	 * @param roboRioPort
	 * @param devicePort
	 * @return
	 */
	public static String forwardToLocal(int roboRioExternalPort, int localhostPort){
		String out = "socat TCP4-LISTEN:" + roboRioExternalPort + ",fork TCP4:127.0.0.1:" + localhostPort;
		RIOdroid.executeCommandThread(out);
		return out;
	}
	
	/**
	 * Combine the two other forward commands to allow you to get to a device port from the roborio external port
	 * This can be used to forward a port that is running a video stream, webserver, etc on the android device
	 * @param roboRioExternalPort
	 * @param devicePort
	 * @return
	 */
	public static String forward(int roboRioExternalPort, int devicePort){
		String out = "";
		int localhostPort = m_nextLocalHostPort++;
		out += ForwardAdb(localhostPort, devicePort);
		return out + ", " + forwardToLocal(roboRioExternalPort, localhostPort);
	}
	
	/**
	 * Reset any port forwarding
	 * @return
	 */
	public static String clearNetworkPorts(){
		String out = "Kill socat processes: " + RIOdroid.executeCommand("killall socat") + "\n CLEAR NETWORK PORTS" + RIOdroid.executeCommand("/etc/init.d/networking restart") 
				+ "ADB CLEAR: " + RIOdroid.executeCommand("adb forward --remove-all");
		return out;
	}
}

