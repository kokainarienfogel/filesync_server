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

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class FileSync {
	public static void main(String[] args) {

	    // Change this to your directory of choice
	    final String watchPath = "/home/aimless/cekt/";

        Watcher w = new Watcher();
        final Folder f;
        final String json;

        try {
            Option<Folder> optf = w.getTree(watchPath);
            if (optf.isEmpty()) {
                System.out.println("Oh noes! Check watchPath? Currently: " + watchPath + "\n");
                return;
            }
            f = optf.get();
            json = JSON.std.asString(f);
        } catch (Exception e) {
            e.printStackTrace();

            return;
        }

        try {
            // Set up a resource manager in order to serve static files
            Path basePath = Paths.get(new java.io.File(".").getCanonicalPath());
            ResourceManager res = PathResourceManager.builder().setBase(basePath).build();

            Undertow server = Undertow.builder()
                .addHttpListener(8090, "0.0.0.0") //change to 0.0.0.0 for external access
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
                                Option<File> file = f.getFileByHash(hash);
                                if (!file.isEmpty()) {
                                    java.io.File actualFile = file.get().getActualFile();
                                    String mimetype = Files.probeContentType(Paths.get(actualFile.getAbsolutePath()));
                                    ex.getResponseHeaders().put(Headers.CONTENT_TYPE, mimetype);

                                    FileInputStream fIn = new FileInputStream(actualFile);
                                    FileChannel ch = fIn.getChannel();
                                    List<ByteBuffer> buffers = new LinkedList<>();

                                    while (ch.position() < ch.size()) {
                                        ByteBuffer buffer = ByteBuffer.allocate(4096);
                                        ch.read(buffer);
                                        buffer.flip();
                                        buffers.add(buffer);
                                    }
                                    ex.getResponseSender().send(buffers.toArray(new ByteBuffer[]{}));
                                }
                            })
                            .add("/tree", ex -> {
                                ex.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), "*");
                                ex.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                                ex.getResponseSender().send(json);
                            }))
                        // Relative path / for static files
                        .addPrefixPath("/", Handlers.resource(res).setDirectoryListingEnabled(true))
                )
                .build();
            server.start();

        } catch (IOException e) {
            e.printStackTrace();
        }





    }

}
