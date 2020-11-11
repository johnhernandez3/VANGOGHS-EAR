package io_devices;

import java.util.List;

import utils.Controller;
import utils.Device;
import utils.Manager;

public interface IODeviceManager extends Manager
{

    boolean addController(Controller control);

    boolean isValid(Controller control);
}
