package com.spyro.messenger.security.deviceinfoparsing.service.impl;

import com.spyro.messenger.exceptionhandling.exception.BaseException;
import com.spyro.messenger.exceptionhandling.exception.UnableToCalculateHeaderChecksumException;
import com.spyro.messenger.security.deviceinfoparsing.service.DeviceInfoService;
import com.spyro.messenger.security.deviceinfoparsing.service.GeolocationService;
import com.spyro.messenger.security.service.HttpServletUtilsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

@Service
@RequiredArgsConstructor
public class DeviceInfoServiceImpl implements DeviceInfoService {
    private final GeolocationService geolocationService;
    @Override
    public String getDeviceInfo(HttpServletRequest request) {
        return new StringBuilder(HttpServletUtilsService.getUAInformation(request))
                .append(", ")
                .append(geolocationService.getCountryCity(request))
                .toString();
    }

    @Override
    public long calculateHeadersChecksum(HttpServletRequest request) {
        var headers = HttpServletUtilsService.getChecksumHeaders(request);
        var byteOut = new ByteArrayOutputStream();
        try {
            var objectOut = new ObjectOutputStream(byteOut);
            objectOut.writeObject(headers);
            byteOut.flush();
        } catch (IOException e) {
            throw new UnableToCalculateHeaderChecksumException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return getCRC32Checksum(byteOut.toByteArray());
    }

    private long getCRC32Checksum(byte[] bytes) {
        Checksum crc32 = new CRC32();
        crc32.update(bytes, 0, bytes.length);
        return crc32.getValue();
    }
}
