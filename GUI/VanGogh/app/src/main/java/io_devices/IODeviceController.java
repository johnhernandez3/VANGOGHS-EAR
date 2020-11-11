package io_devices;

import utils.Controller;
import utils.Device;

public class IODeviceController implements Controller
{

    Device dev;

    public IODeviceController(Device dev)
    {
        this.dev = dev;
    }

}
