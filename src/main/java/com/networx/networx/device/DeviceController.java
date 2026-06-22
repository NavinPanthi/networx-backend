package com.networx.networx.device;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/device")
@RequiredArgsConstructor

public class DeviceController {
    private final DeviceService deviceService;
}