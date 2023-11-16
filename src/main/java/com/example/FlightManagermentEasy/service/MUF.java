package com.example.FlightManagermentEasy.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class MUF {
    //Separate String
    public ArrayList<String> toArrayString1(String s) {
        if (s.isEmpty()) {
            return null;
        }
        String[] list = s.split("");
        ArrayList<String> sl = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            sl.add(list[i].trim());
        }
        return sl;
    }

    public ArrayList<String> toArrayString(String s) {
        if (s.isEmpty()) {
            return null;
        }
        String[] list = s.split(",");
        ArrayList<String> sl = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            sl.add(list[i].trim());
        }
        return sl;
    }

    public List<String> getAlphabetList() {
        List<String> list = toArrayString1("QWERTYUIOPASDFGHJKLZXCVBNM1234567890");
        return list;
    }

    //Convert String And Time
    public List<String> localDateTimeFormatterList() {
        List<String> fomartterList = new ArrayList<>();
        fomartterList.add("yyyy-MM-dd'T'HH:mm");
        fomartterList.add("yyyy/MM/dd'T'HH:mm");
        fomartterList.add("dd/MM/yyyy HH:mm");
        fomartterList.add("dd-MM-yyyy HH:mm");
        fomartterList.add("dd/MM/yyyy' At: 'HH:mm");
        return fomartterList;
    }

    public List<String> localDateFormatterList() {
        List<String> formaterList = new ArrayList<>();
        formaterList.add("yyyy-MM-dd");
        formaterList.add("yyyy/MM/dd");
        formaterList.add("dd/MM/yyyy");
        formaterList.add("dd-MM-yyyy");
        return formaterList;
    }

    public LocalDate localDateTimeToLocalDate(LocalDateTime localDateTime) {
        try {
            LocalDate localDate = localDateTime.toLocalDate();
            return localDate;
        } catch (Exception e) {
            return null;
        }
    }

    public LocalDateTime localDateToLocalDateTime(LocalDate localDate) {
        try {
            LocalDateTime localDateTime = localDate.atStartOfDay();
            return localDateTime;
        } catch (Exception e) {
            return null;
        }
    }

    public String localDateTimeToString(LocalDateTime inputLocalDateTime) {
        if (inputLocalDateTime == null) {
            return null;
        } else {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                return inputLocalDateTime.format(formatter);
            } catch (Exception e) {
                return null;
            }
        }
    }

    public LocalDateTime stringToLocalDateTime(String inputString) {
        if (inputString == null) {
            return null;
        } else if (inputString.isEmpty()) {
            return null;
        } else {
            for (String formatString : localDateTimeFormatterList()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatString);
                try {
                    LocalDateTime result = LocalDateTime.parse(inputString, formatter);
                    return result;
                } catch (Exception e) {
                }
            }
            for (String formatString : localDateFormatterList()) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatString);
                    LocalDate tempResult = LocalDate.parse(inputString, formatter);
                    return localDateToLocalDateTime(tempResult);
                } catch (Exception e) {
                }
            }
            return null;
        }
    }

    public String localDateToString(LocalDate inputLocalDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return inputLocalDate.format(formatter);
        } catch (Exception e) {
            return null;
        }
    }

    public LocalDate stringToLocalDate(String inputString) {
        if (inputString == null) {
            return null;
        } else if (inputString.isEmpty()) {
            return null;
        } else {
            for (String formatString : localDateFormatterList()) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatString);
                    return LocalDate.parse(inputString, formatter);
                } catch (Exception e) {
                }
            }
            for (String formatString : localDateTimeFormatterList()) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatString);
                    LocalDateTime tempResult = LocalDateTime.parse(inputString, formatter);
                    return localDateTimeToLocalDate(tempResult);
                } catch (Exception e) {
                }
            }
            return null;
        }
    }

    public String viewStringOfLocalDateTime(String inputLocalDateTimeString, String formatter) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(formatter);
        try {
            Date date = inputDateFormat.parse(inputLocalDateTimeString);
            String outputLocalDateTimeString = outputDateFormat.format(date);
            return outputLocalDateTimeString;
        } catch (DateTimeParseException e) {
            return null;
        } catch (ParseException e) {
            return null;
        }
    }

    public String viewStringOfLocalDate(String inputLocalDateString, String formatter) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(formatter);
        try {
            Date date = inputDateFormat.parse(inputLocalDateString);
            String outputLocalDateString = outputDateFormat.format(date);
            return outputLocalDateString;
        } catch (ParseException e) {
            return null;
        }
    }

    public String viewDayMonthYear(LocalDateTime input) {
        if (input == null) {
            return null;
        } else {
            int year = input.getYear();
            int month = input.getMonthValue();
            int day = input.getDayOfMonth();
            String MM = new String();
            String dd = new String();
            if (month < 10) {
                MM = "0" + month;
            } else {
                MM = String.valueOf(month);
            }
            if (day < 10) {
                dd = "0" + day;
            } else {
                dd = String.valueOf(day);
            }
            return dd + "/" + MM + "/" + year;
        }
    }

    public String viewHourMinute(LocalDateTime input) {
        if (input == null) {
            return null;
        } else {
            int hour = input.getHour();
            int minute = input.getMinute();
            String HH = new String();
            String mm = new String();
            if (hour < 10) {
                HH = "0" + hour;
            } else {
                HH = String.valueOf(hour);
            }
            if (minute < 10) {
                mm = "0" + minute;
            } else {
                mm = String.valueOf(minute);
            }
            return HH + ":" + mm;
        }
    }

    public String viewDayMonthYear(String input) {
        LocalDateTime localDateTime = stringToLocalDateTime(input);
        return viewDayMonthYear(localDateTime);
    }

    public String viewHourMinute(String input) {
        LocalDateTime localDateTime = stringToLocalDateTime(input);
        return viewHourMinute(localDateTime);
    }

    //Generate Unique Key
    public String generateRandomKeyInSet(Set<String> list, int stringLength) {
        String s = "";
        List<String> alphabet = getAlphabetList();
        do {
            for (int j = 1; j <= stringLength; j++) {
                Collections.shuffle(alphabet);
                s = s + alphabet.get(0);
            }
        } while (list.add(s) == false);
        return s;
    }

    //Create Number List From start To end
    public List<Integer> createIntegerListFromNumberToNumber(int start, int end) {
        List<Integer> result = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            result.add(i);
        }
        return result;
    }

    //image Checker
    public boolean isImage(MultipartFile multipartFile) {
        if (!multipartFile.isEmpty()) {
            String content = multipartFile.getContentType();
            if (content.contains("image")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isImage(Blob blob) {
        try {
            byte[] byteData = blob.getBytes(1L, (int) blob.length());
            ByteArrayInputStream inputStream = new ByteArrayInputStream(byteData);
            return ImageIO.read(inputStream) != null;
        } catch (IOException e) {
            return false;
        } catch (SQLException e) {
            return false;
        }
    }

    //Auto Generate AlphabetList By Number Input
    public List<String> autoGenerateSeatRow(int numberOfSeatRow) {
        List<String> alphabet = toArrayString1("ABCDEFGHIJKLMNOPQRSTRUVWXYZ");
        List<String> result = new ArrayList<>();
        int limit = alphabet.size() - 1;
        List<Integer> loop = loop(numberOfSeatRow, limit + 1);
        for (int i = 0; i < loop.size(); i++) {
            int[][] array = transfer(loop.get(i), i + 1, limit);
            for (int j = 0; j < array.length; j++) {
                String s = "";
                for (int k = 0; k < array[i].length; k++) {
                    s = s + alphabet.get(array[j][k]);
                }
                result.add(s);
            }
        }
        return result;
    }

    public int[][] transfer(int row, int col, int limit) {
        int[][] result = new int[row][col];
        for (int i = 0; i < result.length; i++) {
            if (i != 0) {
                int[] array = result[i - 1];
                int[] arrayNew = array.clone();
                arrayNew[arrayNew.length - 1] = array[array.length - 1] + 1;
                result[i] = resolveOnLimit(arrayNew, limit);
            }
        }
        return result;
    }

    public List<Integer> loop(int input, int size) {
        List<Integer> result = new ArrayList<>();
        double power = 1;
        do {
            if (input > Math.pow(size, power)) {
                input = input - Integer.valueOf((int) Math.pow(size, power));
                result.add((int) Math.pow(size, power));
                power++;
            } else {
                result.add(input);
                break;
            }
        } while (true);
        return result;
    }

    public int[] resolveOnLimit(int[] array, int limit) {
        int[] result = array.clone();
        for (int i = 0; i < result.length; i++) {
            int k = 0;
            if (result[i] > limit && i > 0) {
                for (int j = i; j < result.length; j++) {
                    result[j] = 0;
                }
                for (int j = i - 1; j >= 0; j--) {
                    if (result[j] + 1 <= limit) {
                        result[j] = result[j] + 1;
                        k = j;
                        break;
                    }
                }
                if (k < result.length - 1) {
                    for (int j = k + 1; j < result.length; j++) {
                        result[j] = 0;
                    }
                }
            }
        }
        return result;
    }

    //Guarantee Transform String to String Of Number
    public String ultimateNumberFormatter(String input) {
        input = input.replaceAll("[^0-9\\-]", "")
                .replaceAll("^0+(?!$)", "")
                .replaceAll("[^0-9\\-]|(?<!^)([\\-])", "")
                .replaceAll("^(-?)0+(\\d+)$", "$1$2");
        if (input.equals("-") | input.equals("")) {
            input = "0";
        }
        return input;
    }

    //    MyToken Encode And Decode
    public List<List<String>> getCodeList() {
        String s = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890`~!@#$%^&*()-_+={[]}|\\:;\"'<,>.?/ ";
        String[] array = s.split("");
        List<List<String>> objectList = new ArrayList<>();
        List<String> baseString = new ArrayList<>();
        List<String> codeList = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            baseString.add(array[i]);
            codeList.add(String.valueOf(i));
        }
        objectList.add(baseString);
        objectList.add(codeList);
        return objectList;
    }

    public String encodeString(String input) {
        if (input == null) {
            return String.valueOf(101);
        }
        if (input.isEmpty()) {
            return String.valueOf(100);
        }
        String result = new String();
        String[] array = input.split("");
        List<List<String>> objects = getCodeList();
        List<String> stringValueList = objects.get(0);
        List<String> integerValueList = objects.get(1);
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < stringValueList.size(); j++) {
                if (array[i].equals(stringValueList.get(j))) {
                    result = result + integerValueList.get(j) + ".";
                }
            }
        }
        return result;
    }

    public String decodeString(String input) {
        if (input.equals("101")) {
            return null;
        }
        if (input.equals("")) {
            return "";
        }
        String result = new String();
        String[] array = input.split("\\.");

        List<List<String>> objects = getCodeList();
        List<String> stringValueList = objects.get(0);
        List<String> integerValueList = objects.get(1);
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < integerValueList.size(); j++) {
                if (array[i].equals(integerValueList.get(j))) {
                    result = result + stringValueList.get(j);
                }
            }
        }
        return result;
    }

    public String encodeListString(List<String> inputList) {
        if (inputList == null) {
            return "Sange";
        }
        if (inputList.isEmpty()) {
            return "Yasha";
        }
        String result = new String();
        for (int i = 0; i < inputList.size(); i++) {
            result = result + encodeString(inputList.get(i)) + ";";
        }
        return result;
    }

    public List<String> decodeListString(String input) {
        if (input.equals("Sange")) {
            return null;
        }
        if (input.equals("Yasha")) {
            return new ArrayList<>();
        }
        String[] array = input.split("\\;");
        List<String> result = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            result.add(decodeString(array[i]));
        }
        return result;
    }

}
