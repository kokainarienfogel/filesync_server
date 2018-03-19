package ce_kt_file_tool;


import ce_kt_file_tool.Filesystem.FileSearcher;

import java.io.IOException;

public class FileSearchRunner {

    public static void main(String[] args) throws IOException {

        GetPropertyValues properties = new GetPropertyValues();
        properties.getPropValues();

        FileSearcher fs = new FileSearcher(new String[]{properties.getPath()},
                "[a-zA-Z]*.[a-zA-Z]*", properties.getLog());
        fs.start();


    }
}
