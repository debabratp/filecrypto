package org.example;

import picocli.CommandLine;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * Hello world!
 */
public class App implements Callable<Integer> {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    @CommandLine.Option(names = {"-k", "--key"}, description = "The Key", required = true)
    private static String key;

    @CommandLine.Option(names = {"-p", "--path"}, description = "The directory path containing files to encrypt.", required = true)
    private static String path;

    @CommandLine.Option(names = {"-m", "--mode"}, description = "Provide the mode Encrypt or Decrypt", required = true, type = Integer.class)
    private static Integer mode;

    public static void main(String[] args) {
        System.out.println("Hello World!");
        System.out.println("args = " + args);

        int exitCode = new CommandLine(new App()).execute(args);
        System.out.println("exitCode = " + exitCode);
        System.exit(exitCode);
    }

    /*@Override
    public Integer call() throws Exception {
        File dir = new File(path);
        System.out.println("key = " + key);
        System.out.println("path = " + path);
        if(dir.isDirectory()){
            for (File f : Objects.requireNonNull(dir.listFiles())) {
                File outputFile = new File(f.getAbsolutePath()+".enc");
                encrypt(key, f, outputFile);
                Files.deleteIfExists(Paths.get(f.getAbsolutePath()));
            }
        }
        return 0;
    }*/

    @Override
    public Integer call() {

        if (mode == 1) {
            return encrypt();
        }else {
            return decrypt();
        }

    }

    public static Integer encrypt() {
        try {
            File dir = new File(path);
            System.out.println("key = " + key);
            System.out.println("path = " + path);
            if (dir.isDirectory()) {
                for (File f : Objects.requireNonNull(dir.listFiles())) {
                    File outputFile = new File(f.getAbsolutePath() + ".enc");
                    doCrypto(mode, key, f, outputFile);
                    Files.deleteIfExists(Paths.get(f.getAbsolutePath()));
                }
            }
            return 0;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return -1;
        }


    }


    public static Integer decrypt() {
        try {
            File dir = new File(path);
            System.out.println("key = " + key);
            System.out.println("path = " + path);
            if (dir.isDirectory()) {
                for (File f : Objects.requireNonNull(dir.listFiles())) {
                    String outFile = f.getName().substring(0, f.getName().lastIndexOf("."));
                    File outputFile = new File(path + "//" + outFile);
                    doCrypto(Cipher.DECRYPT_MODE, key, f, outputFile);
                    Files.deleteIfExists(Paths.get(f.getAbsolutePath()));
                }
            }
            return 0;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return -1;
        }
    }

    private static void doCrypto(int cryptoMode, String key, File inputFile, File outputFile) throws CryptoException {
        System.out.println("cryptoMode = " + cryptoMode);
        System.out.println("inputFile = " + inputFile);
        System.out.println("outputFile = " + outputFile);

        try (FileInputStream inputStream = new FileInputStream(inputFile);
             FileOutputStream outputStream = new FileOutputStream(outputFile)) {

            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cryptoMode, secretKey);


            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);

            byte[] outputBytes = cipher.doFinal(inputBytes);


            outputStream.write(outputBytes);

        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                 | InvalidKeyException | BadPaddingException
                 | IllegalBlockSizeException | IOException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
    }
}
