package scripting;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import server.Randomizer;
import tools.HexTool;
import tools.Pair;

public class LieDetectorScript {

    private static final String IMG_DIRECTORY = "scripts/lieDetector";
    private static final String CAPTCHA_VERIFIER = "3ec48eab7c5f53e299fb9c2be9cb9038ac2784e5";
    private static final String CAPTCHA_SERVER = "http://localhost/captcha/captcha.php?verify=" + CAPTCHA_VERIFIER;

    public static final Pair<String, String> getImageBytes() {
        try {
            final URL url = new URL(CAPTCHA_SERVER);
            ByteArrayOutputStream output;
            try (InputStream inputStream = url.openStream()) {
                output = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int n = 0;
                while (-1 != (n = inputStream.read(buffer))) {
                    output.write(buffer, 0, n);
                }
            }
            final String imgByte = HexTool.toString(output.toByteArray());
            return new Pair<>(imgByte.substring(39, imgByte.length()), output.toString().split("CAPTCHA")[0]);
        } catch (IOException ex) {
            final File directory = new File(IMG_DIRECTORY);
            if (!directory.exists()) {
                System.err.println("lieDetector folder does not exist!");
                return null;
            }
            final String filename[] = directory.list();
            String answer = filename[Randomizer.nextInt(filename.length)];
            answer = answer.substring(0, answer.length() - 4); // .jpg 
            try {
                return new Pair<>(HexTool.toString(getBytesFromFile(new File(IMG_DIRECTORY + "/" + answer + ".jpg"))), answer);
            } catch (IOException e) {
                return null;
            }
        }
    }

    public static byte[] getBytesFromFile(final File file) throws IOException {
        byte[] bytes = null;
        try (InputStream is = new FileInputStream(file)) {
            long length = file.length();
            if (length > Integer.MAX_VALUE) {
                return null;
            }
            bytes = new byte[(int) length];
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
            if (offset < bytes.length) {
                System.err.println("[Lie Detector Script] Could not completely read file " + file.getName());
                return null;
            }
        }
        return bytes;
    }
}
