package com.example.socketloversclient.cipher;

public class Cipher {
        // Encrypts text using shift
        public static String encrypt(String text, int shift) {
            StringBuffer result = new StringBuffer();

            for (int i = 0; i < text.length(); i++) {
                if(text.charAt(i) == ' ' || !Character.isLetter(text.charAt(i))){
                    result.append(text.charAt(i));
                    continue;
                }
                if (Character.isUpperCase(text.charAt(i))) {
                    char ch = (char) (((int) text.charAt(i) +
                            shift - 65) % 26 + 65);
                    result.append(ch);
                } else {
                    char ch = (char) (((int) text.charAt(i) +
                            shift - 97) % 26 + 97);
                    result.append(ch);
                }
            }
            return result.toString();
        }

        // Decrypts cipher using shift
        public static String decrypt(String cipher, int shift) {
            StringBuffer result = new StringBuffer();

            for (int i = 0; i < cipher.length(); i++) {
                if(cipher.charAt(i) == ' ' || !Character.isLetter(cipher.charAt(i))){
                    result.append(cipher.charAt(i));
                    continue;
                }
                if (Character.isUpperCase(cipher.charAt(i))) {
                    char ch = (char) (((int) cipher.charAt(i) +
                            shift - 65) % 26 + 65);
                    result.append(ch);
                } else {
                    char ch = (char) (((int) cipher.charAt(i) +
                            shift - 97) % 26 + 97);
                    result.append(ch);
                }
            }
            return result.toString();
        }

}
