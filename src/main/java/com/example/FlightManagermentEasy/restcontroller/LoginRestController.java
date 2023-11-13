package com.example.FlightManagermentEasy.restcontroller;

import com.example.FlightManagermentEasy.dto.user.AccountDTO;
import com.example.FlightManagermentEasy.repository.user.user.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Base64;

@RestController
public class LoginRestController {
    @Autowired
    AccountRepository accountRepository;

    @PostMapping("/ajax/uploadAccountImage")
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile multipartFile) {
        if (!multipartFile.isEmpty()) {
            try {
                byte[] imgData = multipartFile.getBytes();
                String base64 = Base64.getEncoder().encodeToString(imgData);
                return base64;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
