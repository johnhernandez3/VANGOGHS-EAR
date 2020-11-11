package utils;

import java.util.List;

public interface Manager {

    List<Device> devices();
    boolean addDevice(Device device) throws  IllegalArgumentException;
    boolean removeDevice(Device device) throws  IllegalArgumentException;
    Controller getDevice(Device device) throws  IllegalArgumentException;
}
