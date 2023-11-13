package com.example.FlightManagermentEasy.service.service;

import com.example.FlightManagermentEasy.entity.MyToken;
import com.example.FlightManagermentEasy.entity.user.Account;
import com.example.FlightManagermentEasy.repository.MyTokenRepository;
import com.example.FlightManagermentEasy.service.MUF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class TokenService {
    @Autowired
    MUF muf;
    @Autowired
    MyTokenRepository myTokenRepository;

    public MyToken setAccountToken(long accountId, String name, String dob, String address,
                                   String identity, String gender, String username,
                                   String password, String email, String phone,
                                   String image, String createTime) {
        String id = String.valueOf(accountId);
        List<String> informationList = new ArrayList<>();
        informationList.add(id);
        informationList.add(name);
        informationList.add(dob);
        informationList.add(address);
        informationList.add(identity);
        informationList.add(gender);
        informationList.add(username);
        informationList.add(password);
        informationList.add(email);
        informationList.add(phone);
        informationList.add(image);
        informationList.add(createTime);
        String encode = muf.encodeListString(informationList);
        String key = muf.generateRandomKeyInSet(new HashSet<>(), 13);
        MyToken token = new MyToken(key, encode);
        myTokenRepository.save(token);
        return token;
    }

    public MyToken setAccountToken(long accountId, String name, String dob, String address,
                                 String identity, String gender, String username,
                                 String password, String email, String phone,
                                 MultipartFile profileImage, String createTime) {
        String image = new String();
        if (profileImage != null) {
            if (!profileImage.isEmpty()) {
                if (muf.isImage(profileImage)) {
                    try {
                        byte[] imgData = profileImage.getBytes();
                        image = Base64.getEncoder().encodeToString(imgData);
                    } catch (Exception e) {
                        image = null;
                    }
                }
            }
        }

        return setAccountToken(accountId, name, dob, address, identity, gender, username, password, email, phone, image, createTime);
    }

    public MyToken setAccountToke(Account account, String createTime) {
        String image = new String();
        if (account.getImage() != null) {
            try {
                byte[] imgData = account.getImage().getBytes(1L, (int) account.getImage().length());
                image = Base64.getEncoder().encodeToString(imgData);
            } catch (Exception e) {
            }
        }
        String dob = muf.localDateToString(account.getDob());
        return setAccountToken(account.getId(), account.getName(), dob, account.getAddress(), account.getIdentity(), account.getGender(), account.getAccountName(), account.getPassword(), account.getEmail(), account.getPhone(), image, createTime);
    }

    public List<String> getAccountInformationInToken(MyToken token) {
        String encode = token.getEncodeString();
        List<String> accountInformation = muf.decodeListString(encode);
        return accountInformation;
    }

    public Map<String, String> getAccountInformationMapInToken(MyToken token){
        List<String> accountInformation = getAccountInformationInToken(token);
        Map<String, String> accountInformationMap = new HashMap<>();
        accountInformationMap.put("id", accountInformation.get(0));
        accountInformationMap.put("name", accountInformation.get(1));
        accountInformationMap.put("dob", accountInformation.get(2));
        accountInformationMap.put("address", accountInformation.get(3));
        accountInformationMap.put("identity", accountInformation.get(4));
        accountInformationMap.put("gender", accountInformation.get(5));
        accountInformationMap.put("accountName", accountInformation.get(6));
        accountInformationMap.put("password", accountInformation.get(7));
        accountInformationMap.put("email", accountInformation.get(8));
        accountInformationMap.put("phone", accountInformation.get(9));
        accountInformationMap.put("image", accountInformation.get(10));
        accountInformationMap.put("createTime", accountInformation.get(11));
        return accountInformationMap;
    }

    public String getAccountElementInToken(MyToken token, String element) {
        List<String> accountInformation = getAccountInformationInToken(token);
//        informationList.add(id);
        if (element.equals("id")) {
            return accountInformation.get(0);
        }
//        informationList.add(name);
        if (element.equals("name")) {
            return accountInformation.get(1);
        }
//        informationList.add(dob);
        if (element.equals("dob")) {
            return accountInformation.get(2);
        }
//        informationList.add(address);
        if (element.equals("address")) {
            return accountInformation.get(3);
        }
//        informationList.add(identity);
        if (element.equals("identity")) {
            return accountInformation.get(4);
        }
//        informationList.add(gender);
        if (element.equals("gender")) {
            return accountInformation.get(5);
        }
//        informationList.add(username);
        if (element.equals("accountName")) {
            return accountInformation.get(6);
        }
//        informationList.add(password);
        if (element.equals("password")) {
            return accountInformation.get(7);
        }
//        informationList.add(email);
        if (element.equals("email")) {
            return accountInformation.get(8);
        }
//        informationList.add(phone);
        if (element.equals("phone")) {
            return accountInformation.get(9);
        }
//        informationList.add(image);
        if (element.equals("image")) {
            return accountInformation.get(10);
        }
//        informationList.add(createTimeString);
        if (element.equals("createTime")) {
            return accountInformation.get(11);
        }
        return null;
    }
}
