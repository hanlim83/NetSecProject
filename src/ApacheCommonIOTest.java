import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class ApacheCommonIOTest {
    public static void main(String[] args) {
        try {
            if (!new File("PcapExport").exists()) {
                FileUtils.forceMkdir(new File("PcapExport"));
            }
            FileUtils.cleanDirectory(new File("PcapExport"));
        } catch (IOException e) {
            System.err.println("Failed!");
        }
    }
}