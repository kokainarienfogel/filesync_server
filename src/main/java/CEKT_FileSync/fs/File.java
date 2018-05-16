package CEKT_FileSync.fs;

import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

// Basic file object, wrapping around the java.io.File object and providing a hash for the file

public class File extends Object {

    public java.io.File getActualFile() {
        return actualFile;
    }

    private java.io.File actualFile;
    private byte[] hash;
    private long size;

    protected File(java.io.File file) {
        super(file.getName());
        this.actualFile = file;
        this.hash = generateHash();
        this.size = this.actualFile.length();
    }

    private byte[] generateHash() {
        MessageDigest crypt = null;
        try {
            crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();

            FileInputStream fStream = new FileInputStream(this.actualFile);
            DigestInputStream dStream = new DigestInputStream(fStream, crypt);
            while (dStream.read() != -1) {}
            return crypt.digest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

    @Override
    public String toString() {
        return super.getName();
    }

    public String getHash() {
        StringBuilder sb = new StringBuilder();
        for (byte b : this.hash) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public long getSize() {
        return size;
    }
}
