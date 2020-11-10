package io_devices;

import java.util.List;

import utils.Controller;
import utils.Device;
import utils.Manager;

public class IODeviceManager extends Manager
{
    private List<Controller> controllers;

    public IODeviceManager()
    {

    }

    public boolean addController(Controller control)
    {
        return false;
    }

    protected boolean isValid(Controller control)
    {
        return false;
    }
}
