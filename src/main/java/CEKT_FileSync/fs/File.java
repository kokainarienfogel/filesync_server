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
        this.size = this.actualFile.length();
    }

    public void generateHash() {
        this.hash = genHash();
    }

    private byte[] genHash() {
        try {
            MessageDigest msgDigest = MessageDigest.getInstance("MD5"); //MD5 should fast enough
            msgDigest.reset();

            FileInputStream fStream = new FileInputStream(this.actualFile);
            DigestInputStream dStream = new DigestInputStream(fStream, msgDigest);
            while (dStream.read() != -1) {
            }
            dStream.close();
            fStream.close();
            return msgDigest.digest();
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
