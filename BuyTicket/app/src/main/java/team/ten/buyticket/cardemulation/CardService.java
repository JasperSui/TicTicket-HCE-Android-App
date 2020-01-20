/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package team.ten.buyticket.cardemulation;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import team.ten.buyticket.common.logger.Log;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;


public class CardService extends HostApduService {


    private static final String TAG = "CardService";

    // AID for our loyalty card service.
    private static final String SAMPLE_LOYALTY_CARD_AID = "F222222222";

    private static final String SELECT_APDU_HEADER = "00A40400";
    private static final String BALANCE_GET_APDU_HEADER = "805A0000";
    private static final String BALANCE_WITHDRAW_APDU_HEADER = "80540000";
    private static final String BALANCE_DEPOSIT_APDU_HEADER = "80550000";
    private static final String KEY_GET_APDU_HEADER = "80560000";
    private static final String KEY_SET_APDU_HEADER = "80570000";
    private static final String USER_GET_APDU_HEADER = "80580000";

    private static final String TEST_GET_KEY_HEADER = "80590000";

    private static final byte[] SELECT_OK_SW = HexStringToByteArray("9000");
    private static final byte[] UNKNOWN_ERROR_SW = HexStringToByteArray("91A1");
    private static final byte[] UNKNOWN_CMD_SW = HexStringToByteArray("0000");
    private static final byte[] SELECT_APDU = BuildSelectApdu(SAMPLE_LOYALTY_CARD_AID);
    private static final byte[] BALANCE_GET_APDU = BuildSelectApdu2(SAMPLE_LOYALTY_CARD_AID);


    @Override
    public void onDeactivated(int reason) { }

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        Log.i(TAG, "Received APDU: " + ByteArrayToHexString(commandApdu));

        if (Arrays.equals(SELECT_APDU, commandApdu)) {
            Log.i(TAG, "Select account");
            return ConcatArrays( SELECT_OK_SW);

        } else if (Arrays.equals(BALANCE_GET_APDU, commandApdu)) {

            String balance = String.valueOf(AccountStorage.GetBalance(this));
            byte[] accountBytes = balance.getBytes();
            Log.i(TAG, "Sending account balnce: " + balance);
            return ConcatArrays(accountBytes, SELECT_OK_SW);

        }  else if (checkWithdraw(commandApdu)) {


            int dataLength = commandApdu[4];
            byte[] data = new byte[dataLength];
            for(int i = 5; i < 5+dataLength; i++){
                data[i-5] = commandApdu[i];
            }
            String dataStr = new String(data, StandardCharsets.UTF_8);
            int moneyNum = Integer.valueOf(dataStr);

            Log.i(TAG, "Sending account balnce decrease: " + dataStr);

            int balance = AccountStorage.GetBalance(this);

            if(balance < moneyNum){
                return ConcatArrays(UNKNOWN_ERROR_SW);
            }else{
                AccountStorage.decreaseBalance(this, moneyNum);
                return ConcatArrays(SELECT_OK_SW);
            }

        }else if (checkDeposit(commandApdu)) {


            int dataLength = commandApdu[4];
            byte[] data = new byte[dataLength];
            for(int i = 5; i < 5+dataLength; i++){
                data[i-5] = commandApdu[i];
            }
            String dataStr = new String(data, StandardCharsets.UTF_8);
            int moneyNum = Integer.valueOf(dataStr);

            Log.i(TAG, "Sending account balnce increase: " + dataStr);

            AccountStorage.increaseBalance(this, moneyNum);

            return ConcatArrays(SELECT_OK_SW);

        }else if (checkGetKey(commandApdu)) {

            int dataLength = commandApdu[4];
            byte[] data = new byte[dataLength];
            for(int i = 5; i < 5+dataLength; i++){
                data[i-5] = commandApdu[i];
            }
            String dataStr = new String(data, StandardCharsets.UTF_8);
            int wanntGet = Integer.valueOf(dataStr);

            String key = AccountStorage.takeoutKey_byID(this,wanntGet);
            byte[] keyBytes = key.getBytes();
            Log.i(TAG, "Sending the key ("+ wanntGet+"): " + keyBytes);
            return ConcatArrays(keyBytes, SELECT_OK_SW);


        }else if (checkSetKey(commandApdu)) {


            int dataLength = commandApdu[4];
            byte[] data = new byte[dataLength];
            for(int i = 5; i < 5+dataLength; i++){
                int tempInt = commandApdu[i];
                data[i-5] = (byte)tempInt;
            }
            String dataStr = new String(data, StandardCharsets.UTF_8);

            Log.i(TAG, "Sending account key increase: " + dataStr);

            AccountStorage.insertKey(this, dataStr);

            return ConcatArrays(SELECT_OK_SW);

        }else if(checkGetUser(commandApdu)){
            String user = String.valueOf(AccountStorage.getUser(this));
            byte[] userBytes = user.getBytes();
            Log.i(TAG, "Sending user id: " + user);
            return ConcatArrays(userBytes, SELECT_OK_SW);
        }else if (checkAllKey(commandApdu)) {
            AccountStorage.getAllKey(this);
            return ConcatArrays(SELECT_OK_SW);
        } else {
            return UNKNOWN_CMD_SW;
        }
    }


    //============================ The boolean of Judging the Command  ========================
    public static byte[] BuildSelectApdu(String aid) {
        // Format: [CLASS | INSTRUCTION | PARAMETER 1 | PARAMETER 2 | LENGTH | DATA]
        return HexStringToByteArray(SELECT_APDU_HEADER + String.format("%02X",
                aid.length() / 2) + aid);
    }

    public static byte[] BuildSelectApdu2(String aid) {
        // Format: [CLASS | INSTRUCTION | PARAMETER 1 | PARAMETER 2 | LENGTH | DATA]
        return HexStringToByteArray(BALANCE_GET_APDU_HEADER + String.format("%02X",
                aid.length() / 2) + aid);
    }

    public static boolean checkWithdraw(byte[] apdu) {
        //80540000
        byte[] withdraw = HexStringToByteArray(BALANCE_WITHDRAW_APDU_HEADER);
        return apdu[0] == withdraw[0] && apdu[1] == withdraw[1] && apdu[2] == withdraw[2] && apdu[3] == withdraw[3];
    }

    public static boolean checkDeposit(byte[] apdu) {
        //80550000
        byte[] withdraw = HexStringToByteArray(BALANCE_DEPOSIT_APDU_HEADER);
        return apdu[0] == withdraw[0] && apdu[1] == withdraw[1] && apdu[2] == withdraw[2] && apdu[3] == withdraw[3];
    }
    public static boolean checkGetKey(byte[] apdu) {
        //80560000
        byte[] withdraw = HexStringToByteArray(KEY_GET_APDU_HEADER);
        return apdu[0] == withdraw[0] && apdu[1] == withdraw[1] && apdu[2] == withdraw[2] && apdu[3] == withdraw[3];
    }
    public static boolean checkSetKey(byte[] apdu) {
        //80570000
        byte[] withdraw = HexStringToByteArray(KEY_SET_APDU_HEADER);
        return apdu[0] == withdraw[0] && apdu[1] == withdraw[1] && apdu[2] == withdraw[2] && apdu[3] == withdraw[3];
    }
    public static boolean checkGetUser(byte[] apdu) {
        //80580000
        byte[] withdraw = HexStringToByteArray(USER_GET_APDU_HEADER);
        return apdu[0] == withdraw[0] && apdu[1] == withdraw[1] && apdu[2] == withdraw[2] && apdu[3] == withdraw[3];
    }


    public static boolean checkAllKey(byte[] apdu) {
        //80590000
        byte[] withdraw = HexStringToByteArray(TEST_GET_KEY_HEADER);
        return apdu[0] == withdraw[0] && apdu[1] == withdraw[1] && apdu[2] == withdraw[2] && apdu[3] == withdraw[3];
    }
    //============================ The boolean of Judging the Command  ========================



    public static String ByteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2]; // Each byte has two hex characters (nibbles)
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF; // Cast bytes[j] to int, treating as unsigned value
            hexChars[j * 2] = hexArray[v >>> 4]; // Select hex character from upper nibble
            hexChars[j * 2 + 1] = hexArray[v & 0x0F]; // Select hex character from lower nibble
        }
        return new String(hexChars);
    }


    public static byte[] HexStringToByteArray(String s) throws IllegalArgumentException {
        int len = s.length();
        if (len % 2 == 1) {
            throw new IllegalArgumentException("Hex string must have even number of characters");
        }
        byte[] data = new byte[len / 2]; // Allocate 1 byte per 2 hex characters
        for (int i = 0; i < len; i += 2) {
            // Convert each character into a integer (base-16), then bit-shift into place
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }


    public static byte[] ConcatArrays(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }
}
