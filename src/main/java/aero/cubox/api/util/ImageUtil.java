package aero.cubox.api.util;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageUtil {

    static final int MAX_IMAGE_FILE_SIZE = 1048576; // 1024 * 1024
    static final int RESIZE_WIDTH = 800;

    public static Boolean isTooBig(String filepath) throws Exception
    {
        Path path = Paths.get(filepath);
        long bytes = Files.size(path);

        if ( bytes > ImageUtil.MAX_IMAGE_FILE_SIZE )
        {
            return true;
        }

        return false;
    }

    public static byte[] getByteFromResizeImage(String filepath) throws Exception
    {
        File file = new File(filepath);

        BufferedImage bimg = ImageIO.read(file);
        int width          = bimg.getWidth();
        int height         = bimg.getHeight();

        int newHeight = (int)(1.0 *  height / width * RESIZE_WIDTH);

        BufferedImage resizedImage =
                ImageUtil.resize(new FileInputStream(file), RESIZE_WIDTH, newHeight);


        String formatName = filepath.substring(filepath.lastIndexOf(".") + 1);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, formatName, baos);
        return baos.toByteArray();
    }

    public static BufferedImage resize(InputStream inputStream, int width, int height)
            throws IOException {
        BufferedImage inputImage = ImageIO.read(inputStream);

        BufferedImage outputImage =
                new BufferedImage(width, height, inputImage.getType());

        Graphics2D graphics2D = outputImage.createGraphics();
        graphics2D.drawImage(inputImage, 0, 0, width, height, null);
        graphics2D.dispose();

        return outputImage;
    }


}
