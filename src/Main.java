import java.util.ArrayList;
import java.util.Scanner;

/**
 * Main class for managing a smart home system.
 * Initializes and controls different types of smart devices (Lights, Cameras, Heaters).
 */
public class Main {

    public static final int MAX_LIGHT = 3;
    public static final int MIN_LIGHT = 0;
    public static final int MIN_CAMERA = 4;
    public static final int MAX_CAMERA = 5;
    public static final int ANGLE = 45;
    public static final int MIN_HEATER = 6;
    public static final int MAX_HEATER = 9;
    public static final int TEMPERATURE = 20;

    /**
     * Main method to initialize the smart devices and start the event loop for command handling.
     *
     * @param args Command-line arguments (not used in this implementation).
     */
    public static void main(String[] args) {

        // Initialize devices
        ArrayList<SmartDevice> devices = new ArrayList<>();
        for (int i = MIN_LIGHT; i <= MAX_LIGHT; i++) { // Lights
            devices.add(new Light(Status.ON, false, BrightnessLevel.LOW, LightColor.YELLOW));
            devices.get(i).setDeviceId(i);
        }

        for (int i = MIN_CAMERA; i <= MAX_CAMERA; i++) { // Cameras
            devices.add(new Camera(Status.ON, false, false, ANGLE));
            devices.get(i).setDeviceId(i);
        }

        for (int i = MIN_HEATER; i <= MAX_HEATER; i++) { // Heaters
            devices.add(new Heater(Status.ON, TEMPERATURE));
            devices.get(i).setDeviceId(i);
        }

        eventLoop(devices);
    }

    /**
     * Handles user input commands for controlling the smart devices.
     * Valid commands include turning devices on/off, changing their attributes, and displaying their statuses.
     *
     * @param devices List of smart devices to be controlled.
     */
    static void eventLoop(ArrayList<SmartDevice> devices) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Separated arguments and number of them to check validity
            String[] cmd = scanner.nextLine().split(" ");
            int length = cmd.length;
            String dName = "";
            int dId = MIN_LIGHT;
            if (length >= MAX_LIGHT && checkInt(cmd[2])) {
                dName = cmd[1];
                dId = Integer.parseInt(cmd[2]);
            }
            // Router for different requests, handles unknown command by default case
            switch (cmd[MIN_LIGHT]) {
                // In each case there is validation for arguments count
                case "DisplayAllStatus":
                    if (length != 1) {
                        System.out.println("Invalid command");
                        break;
                    }

                    for (SmartDevice device : devices) {
                        System.out.println(device.displayStatus());
                    }

                    break;
                case "TurnOn":
                    if (length != MAX_LIGHT || !checkInt(cmd[2])) {
                        System.out.println("Invalid command");
                        break;
                    }

                    if (checkType(dId, dName)) {
                        System.out.println("The smart device was not found");
                        break;
                    }
                    if (devices.get(dId).isOn()) {
                        System.out.printf("%s %d is already on%n", dName, dId);
                        break;
                    }
                    devices.get(dId).turnOn();
                    System.out.printf("%s %d is on%n", dName, dId);
                    break;

                case "TurnOff":
                    if (length != MAX_LIGHT || !checkInt(cmd[2])) {
                        System.out.println("Invalid command");
                        break;
                    }
                    if (checkType(dId, dName)) {
                        System.out.println("The smart device was not found");
                        break;
                    }
                    if (!devices.get(dId).isOn()) {
                        System.out.printf("%s %d is already off%n", dName, dId);
                        break;
                    }
                    devices.get(dId).turnOff();
                    System.out.printf("%s %d is off%n", dName, dId);
                    break;

                case "StartCharging":
                    if (length != MAX_LIGHT || !checkInt(cmd[2])) {
                        System.out.println("Invalid command");
                        break;
                    }
                    if (checkType(dId, dName)) {
                        System.out.println("The smart device was not found");
                        break;
                    }
                    if (!(devices.get(dId) instanceof Chargeable)) {
                        System.out.printf("%s %d is not chargeable%n", getType(dId), dId);
                        break;
                    }
                    Chargeable chDevice = (Chargeable) devices.get(dId);
                    if (chDevice.isCharging()) {
                        System.out.printf("%s %d is already charging%n", dName, dId);
                        break;
                    }
                    chDevice.startCharging();
                    System.out.printf("%s %d is charging%n", dName, dId);
                    break;

                case "StopCharging":
                    if (length != MAX_LIGHT || !checkInt(cmd[2])) {
                        System.out.println("Invalid command");
                        break;
                    }
                    if (checkType(dId, dName)) {
                        System.out.println("The smart device was not found");
                        break;
                    }
                    if (!(devices.get(dId) instanceof Chargeable)) {
                        System.out.printf("%s %d is not chargeable%n", getType(dId), dId);
                        break;
                    }
                    chDevice = (Chargeable) devices.get(dId);
                    if (!chDevice.isCharging()) {
                        System.out.printf("%s %d is not charging%n", getType(dId), dId);
                        break;
                    }
                    chDevice.stopCharging();
                    System.out.printf("%s %d stopped charging%n", dName, dId);
                    break;

                case "SetTemperature":
                    if (length != MIN_CAMERA || !checkInt(cmd[2]) || !checkInt(cmd[MAX_LIGHT])) {
                        System.out.println("Invalid command");
                        break;
                    }
                    if (checkType(dId, dName)) {
                        System.out.println("The smart device was not found");
                        break;
                    }
                    if (!devices.get(dId).isOn()) {
                        System.out.printf("You can't change the status of the %s %d while it is off%n", dName, dId);
                        break;
                    }
                    if (!(devices.get(dId) instanceof Heater)) {
                        System.out.printf("%s %d is not a heater%n", getType(dId), dId);
                        break;
                    }
                    int dTemp = Integer.parseInt(cmd[MAX_LIGHT]);
                    if (dTemp < Heater.MIN_HEATER_TEMP || dTemp > Heater.MAX_HEATER_TEMP) {
                        System.out.printf("Heater %d temperature should be in the range [15, 30]%n", dId);
                        break;
                    }
                    ((Heater) devices.get(dId)).setTemperature(dTemp);
                    System.out.printf("%s %d temperature is set to %d%n", dName, dId, dTemp);
                    break;

                case "SetBrightness":
                    if (length != MIN_CAMERA || !checkInt(cmd[2])) {
                        System.out.println("Invalid command");
                        break;
                    }
                    if (checkType(dId, dName)) {
                        System.out.println("The smart device was not found");
                        break;
                    }
                    if (!devices.get(dId).isOn()) {
                        System.out.printf("You can't change the status of the %s %d while it is off%n", dName, dId);
                        break;
                    }
                    if (!(devices.get(dId) instanceof Light)) {
                        System.out.printf("%s %d is not a light%n", getType(dId), dId);
                        break;
                    }

                    BrightnessLevel dBright;
                    try {
                        dBright = BrightnessLevel.valueOf(cmd[MAX_LIGHT]);
                    } catch (IllegalArgumentException e) {
                        System.out.println("The brightness can only be one of \"LOW\", \"MEDIUM\", or \"HIGH\"");
                        break;
                    }

                    ((Light) devices.get(dId)).setBrightnessLevel(dBright);
                    System.out.printf("%s %d brightness level is set to %s%n", dName, dId, dBright);

                    break;

                case "SetColor":
                    if (length != MIN_CAMERA || !checkInt(cmd[2])) {
                        System.out.println("Invalid command");
                        break;
                    }
                    if (checkType(dId, dName)) {
                        System.out.println("The smart device was not found");
                        break;
                    }
                    if (!devices.get(dId).isOn()) {
                        System.out.printf("You can't change the status of the %s %d while it is off%n", dName, dId);
                        break;
                    }
                    if (!(devices.get(dId) instanceof Light)) {
                        System.out.printf("%s %d is not a light%n", getType(dId), dId);
                        break;
                    }

                    LightColor dColor;
                    try {
                        dColor = LightColor.valueOf(cmd[MAX_LIGHT]);
                    } catch (IllegalArgumentException e) {
                        System.out.println("The light color can only be \"YELLOW\" or \"WHITE\"");
                        break;
                    }

                    ((Light) devices.get(dId)).setLightColor(dColor);
                    System.out.printf("%s %d color is set to %s%n", dName, dId, dColor);
                    break;

                case "SetAngle":
                    if (length != MIN_CAMERA || !checkInt(cmd[2]) || !checkInt(cmd[MAX_LIGHT])) {
                        System.out.println("Invalid command");
                        break;
                    }
                    if (checkType(dId, dName)) {
                        System.out.println("The smart device was not found");
                        break;
                    }
                    if (!devices.get(dId).isOn()) {
                        System.out.printf("You can't change the status of the %s %d while it is off%n", dName, dId);
                        break;
                    }
                    if (!(devices.get(dId) instanceof Camera)) {
                        System.out.printf("%s %d is not a camera%n", getType(dId), dId);
                        break;
                    }

                    int dAngle = Integer.parseInt(cmd[MAX_LIGHT]);
                    if (dAngle < Camera.MIN_CAMERA_ANGLE || dAngle > Camera.MAX_CAMERA_ANGLE) {
                        System.out.printf("Camera %d angle should be in the range [-60, 60]%n", dId);
                        break;
                    }

                    ((Camera) devices.get(dId)).setCameraAngle(dAngle);
                    System.out.printf("%s %d angle is set to %d%n", dName, dId, dAngle);

                    break;

                case "StartRecording":
                    if (length != MAX_LIGHT || !checkInt(cmd[2])) {
                        System.out.println("Invalid command");
                        break;
                    }
                    if (checkType(dId, dName)) {
                        System.out.println("The smart device was not found");
                        break;
                    }
                    if (!devices.get(dId).isOn()) {
                        System.out.printf("You can't change the status of the %s %d while it is off%n", dName, dId);
                        break;
                    }
                    if (!(devices.get(dId) instanceof Camera)) {
                        System.out.printf("%s %d is not a camera%n", getType(dId), dId);
                        break;
                    }
                    Camera cDevice = (Camera) devices.get(dId);
                    if (cDevice.isRecording()) {
                        System.out.printf("%s %d is already recording%n", dName, dId);
                        break;
                    }
                    cDevice.startRecording();
                    System.out.printf("%s %d started recording%n", dName, dId);
                    break;

                case "StopRecording":
                    if (length != MAX_LIGHT || !checkInt(cmd[2])) {
                        System.out.println("Invalid command");
                        break;
                    }
                    if (dId < MIN_LIGHT || dId > MAX_HEATER) {
                        System.out.println("The smart device was not found");
                        break;
                    }
                    if (!devices.get(dId).isOn()) {
                        System.out.printf("You can't change the status of the %s %d while it is off%n", dName, dId);
                        break;
                    }
                    if (!(devices.get(dId) instanceof Camera)) {
                        System.out.printf("%s %d is not a camera%n", getType(dId), dId);
                        break;
                    }
                    cDevice = (Camera) devices.get(dId);
                    if (!cDevice.isRecording()) {
                        System.out.printf("%s %d is not recording%n", getType(dId), dId);
                        break;
                    }
                    cDevice.stopRecording();
                    System.out.printf("%s %d stopped recording%n", dName, dId);
                    break;
                case "end":
                    if (length != 1) {
                        System.out.println("Invalid command");
                        break;
                    }
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid command");
            }

        }


    }

    /**
     * Validates if a given string represents an integer.
     *
     * @param text The string to be validated.
     * @return {@code true} if the string is a valid integer, {@code false} otherwise.
     */
    static boolean checkInt(String text) {
        return text.matches("-?\\d+");
    }

    /**
     * Checks whether a device ID and type match the expected range and type.
     *
     * @param id   The device ID.
     * @param type The expected type of the device (e.g., "Light", "Camera", "Heater").
     * @return {@code true} if the type and ID do not match, {@code false} otherwise.
     */
    static boolean checkType(int id, String type) {
        return (id < MIN_LIGHT || id > MAX_LIGHT || !type.equals("Light"))
                && (id < MIN_CAMERA || id > MAX_CAMERA || !type.equals("Camera"))
                && (id < MIN_HEATER || id > MAX_HEATER || !type.equals("Heater"));

    }

    /**
     * Retrieves the type of device based on its ID.
     *
     * @param id The device ID.
     * @return The type of the device as a string ("Light", "Camera", "Heater", or "Invalid").
     */
    static String getType(int id) {
        if (id >= MIN_LIGHT && id <= MAX_LIGHT) {
            return "Light";
        }
        if (id >= MIN_CAMERA && id <= MAX_CAMERA) {
            return "Camera";
        }
        if (id >= MIN_HEATER && id <= MAX_HEATER) {
            return "Heater";
        }
        return "Invalid";
    }

}

/**
 * Represents the status of a smart device.
 */
enum Status {
    OFF, ON
}

/**
 * Abstract class representing a smart device with basic controllable features.
 */
abstract class SmartDevice implements Controllable {
    // Fields and methods for SmartDevice
    private Status status;
    private int deviceId;
    private int numberOfDevices;

    public SmartDevice(Status status) {
        this.status = status;
    }

    // Just placeholder in method's body because of return type String
    public String displayStatus() {
        return status.toString();
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean turnOff() {
        this.status = Status.OFF;
        return true;
    }

    public boolean turnOn() {
        this.status = Status.ON;
        return true;
    }

    public boolean isOn() {
        return status == Status.ON;
    }

    public boolean checkStatusAccess() {
        return isOn(); // very sus method
    }
}

/**
 * Interface representing a controllable device.
 */
interface Controllable {
    boolean turnOff();

    boolean turnOn();

    boolean isOn();
}

/**
 * Class representing a heater device.
 */
class Heater extends SmartDevice {
    // Fields and methods for Heater
    private int temperature;
    static final int MAX_HEATER_TEMP = 30;
    static final int MIN_HEATER_TEMP = 15;

    public Heater(Status status, int temperature) {
        super(status);
        this.temperature = temperature;
    }

    public int getTemperature() {
        return temperature;
    }

    public boolean setTemperature(int temperature) {
        this.temperature = temperature;
        return true;
    }

    @Override
    public String displayStatus() {
        return "Heater %d is %s and the temperature is %d.".formatted(getDeviceId(), getStatus(), getTemperature());
    }
}

/**
 * Class representing a camera device with recording and charging capabilities.
 */
class Camera extends SmartDevice implements Chargeable {
    // Fields and methods for Camera
    static final int MAX_CAMERA_ANGLE = 60;
    static final int MIN_CAMERA_ANGLE = -60;
    private boolean charging;
    private boolean recording;
    private int angle;

    public Camera(Status status, boolean charging, boolean recording, int angle) {
        super(status);
        this.charging = charging;
        this.recording = recording;
        this.angle = angle;
    }

    public int getAngle() {
        return angle;
    }

    public boolean setCameraAngle(int angle) {
        this.angle = angle;
        return true;
    }

    public boolean startRecording() {
        recording = true;
        return true;
    }

    public boolean stopRecording() {
        recording = false;
        return true;
    }

    public boolean isRecording() {
        return recording;
    }

    public boolean isCharging() {
        return charging;
    }

    public boolean startCharging() {
        charging = true;
        return true;
    }

    public boolean stopCharging() {
        charging = false;
        return true;
    }


    @Override
    public String displayStatus() {
        return ("Camera %d is %s, the angle is %d, the charging status "
                + "is %b, and the recording status is %b.")
                .formatted(getDeviceId(), getStatus(), getAngle(), isCharging(), isRecording());
    }
}

/**
 * Interface representing a chargeable device.
 */
interface Chargeable {
    boolean isCharging();

    boolean startCharging();

    boolean stopCharging();
}

/**
 * Class representing a light device with brightness and color attributes.
 */
class Light extends SmartDevice implements Chargeable {
    // Fields and methods for Light
    private boolean charging;
    private BrightnessLevel brightnessLevel;
    private LightColor lightColor;

    public Light(Status status, boolean charging, BrightnessLevel brightnessLevel, LightColor lightColor) {
        super(status);
        this.charging = charging;
        this.brightnessLevel = brightnessLevel;
        this.lightColor = lightColor;
    }

    public LightColor getLightColor() {
        return lightColor;
    }

    public boolean setLightColor(LightColor lightColor) {
        this.lightColor = lightColor;
        return true;
    }

    public BrightnessLevel getBrightnessLevel() {
        return brightnessLevel;
    }

    public boolean setBrightnessLevel(BrightnessLevel brightnessLevel) {
        this.brightnessLevel = brightnessLevel;
        return true;
    }

    public boolean isCharging() {
        return charging;
    }

    public boolean startCharging() {
        charging = true;
        return true;
    }

    public boolean stopCharging() {
        charging = false;
        return true;
    }

    @SuppressWarnings("checkstyle:OperatorWrap")
    @Override
    public String displayStatus() {
        return ("Light %d is %s, the color is %s, "
                + "the charging status is %b, "
                + "and the brightness level is %s.")
                .formatted(getDeviceId(), getStatus(), getLightColor(), isCharging(), getBrightnessLevel());
    }
}

/**
 * Enumeration for light colors.
 */
enum LightColor {
    WHITE, YELLOW
}

/**
 * Enumeration for brightness levels of a light device.
 */
enum BrightnessLevel {
    HIGH, MEDIUM, LOW
}
