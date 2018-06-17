package CEKT_FileSync;

import CEKT_FileSync.fs.File;
import CEKT_FileSync.fs.Folder;
import CEKT_FileSync.fs.Watcher;

import com.fasterxml.jackson.jr.ob.JSON;
import io.atlassian.fugue.Option;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class FileSync {

    public static final String PWA_PATH = "./pwa";
    public static final int JA_BIND_PORT = 8090;
    public static final int JA_BIND_PORT_TLS = 8444;
    public static final String JA_BIND_ADDRESS = "0.0.0.0";

    private static final char[] STORE_PASSWORD = "password".toCharArray();

    public static Watcher watcher;
    public static final Folder emptyFolder = new Folder("empty_root");

    // Change this to your directory of choice
    public static final String WATCH_PATH = "/home/aimless/Desktop/Tomato_1_28/";

    public static Option<Folder> process(Watcher w) {
        try {
            w.watchPath(WATCH_PATH); // Option from Atlassian's fugue functional programming library
            Option<Folder> rootFolderOpt = w.retrieve();
            if (rootFolderOpt.isEmpty()) {
                System.out.println("Oh noes! Check watchPath? Currently: " + WATCH_PATH + "\n");
                return Option.none();
            }
            return Option.some(rootFolderOpt.get());
        } catch (Exception e) {
            e.printStackTrace();
            return Option.none();
        }
    }

    public static void main(String[] args) {
        watcher = new Watcher();

        // On startup, generate a tree structure containing the directory meta data
        // and marshal this to JSON (we use jackson.jr, which is a stripped down version of Jackson)
        AtomicReference<Folder> f = new AtomicReference<>(process(watcher).getOrElse(emptyFolder));

        try {
            // Set up a resource manager in order to serve static files
            Path basePath = Paths.get(new java.io.File(PWA_PATH).getCanonicalPath());
            ResourceManager res = PathResourceManager.builder().setBase(basePath).build();

            // Undertow is our http server, it's basically one of the first things you see on Google
            Undertow server = Undertow.builder()
                .addHttpListener(JA_BIND_PORT, JA_BIND_ADDRESS) //change to 0.0.0.0 for external access
                .addHttpsListener(
                        JA_BIND_PORT_TLS,
                        JA_BIND_ADDRESS,
                        getKeyManagers("keystore"),
                        getTrustManagers("truststore")
                )
                .setHandler(
                    // Set up a path handler that enables us to route prefixes to specific handlers
                    Handlers.path()
                        // Relative path /api for api endpoints:
                        // /file/{hash} returns file contents
                        // /tree returns the tree structure of the watched directory
                        .addPrefixPath("/api", Handlers.pathTemplate()
                            .add("/file/{hash}", ex -> {
                                ex.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), "*");
                                String hash = ex.getQueryParameters().get("hash").getFirst();
                                System.out.println("File requested, hash: " + hash);
                                Option<File> file = f.get().getFileByHash(hash);
                                if (!file.isEmpty()) {
                                    java.io.File actualFile = file.get().getActualFile();
                                    String mimetype = Files.probeContentType(Paths.get(actualFile.getAbsolutePath()));
                                    ex.getResponseHeaders().put(Headers.CONTENT_TYPE, mimetype);

                                    // Undertow can only serve arbitrary binary data through one or multiple ByteBuffer
                                    // objects, for which we need a FileChannel object
                                    // Buffers are size-limited, so we need to split things up
                                    FileInputStream fIn = new FileInputStream(actualFile);
                                    FileChannel ch = fIn.getChannel();
                                    List<ByteBuffer> buffers = new LinkedList<>();

                                    while (ch.position() < ch.size()) {
                                        ByteBuffer buffer = ByteBuffer.allocate(4096);
                                        ch.read(buffer);
                                        buffer.flip(); // for read access, flip buffer 'direction'
                                        buffers.add(buffer);
                                    }
                                    ex.getResponseSender().send(buffers.toArray(new ByteBuffer[]{}));
                                }
                            })
                            .add("/tree", ex -> {
                                System.out.println("Tree requested");
                                try {
                                    if (watcher.hasChanged()) f.set(watcher.retrieve().getOrElse(emptyFolder));
                                    String json = JSON.std.asString(f.get());
                                    ex.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), "*");
                                    ex.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                                    ex.getResponseSender().send(json);
                                } catch (Exception e) {
                                    System.out.println("JSON generation failed!");
                                }
                            }))
                        // Relative path / for static files
                        .addPrefixPath("/", Handlers.resource(res).setDirectoryListingEnabled(true))
                )
                .build();
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static KeyStore loadKeyStore(String name) throws Exception {

        System.out.println("Loading key store: " + name);
        Path path = Paths.get(new java.io.File(name).getCanonicalPath());
        final InputStream stream = Files.newInputStream(path);
        if(stream == null) {
            throw new RuntimeException("Could not load keystore");
        }
        try(InputStream is = stream) {
            KeyStore loadedKeystore = KeyStore.getInstance("JKS");
            loadedKeystore.load(is, STORE_PASSWORD);
            return loadedKeystore;
        }
    }

    private static KeyManager[] getKeyManagers(String name) throws Exception {
        KeyStore keystore = loadKeyStore(name);
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keystore, STORE_PASSWORD);
        return keyManagerFactory.getKeyManagers();
    }

    private static TrustManager[] getTrustManagers(String name) throws Exception {
        KeyStore keystore = loadKeyStore(name);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keystore);
        return trustManagerFactory.getTrustManagers();
    }


}
