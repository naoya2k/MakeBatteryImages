import java.io.*;
import javax.imageio.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.GraphicsEnvironment;

public class MakeBatteryImage {
    static void err(String mes) {
        System.err.println(mes);
        System.exit(1);
    }
    static void usage() {
        System.err.println("java MakeBatteryImage org-dir [font-name] [font-size]");
        System.err.println("\t creates 202 images from org-dir/sys_battery_%d.png and org-dir/stat_sys_battery_charge_anim%d.png");
	System.err.println("\t requires:");
	System.err.println("\t stat_sys_battery_0.png   stat_sys_battery_charge_anim0.png");
	System.err.println("\t stat_sys_battery_100.png stat_sys_battery_charge_anim100.png");
	System.err.println("\t stat_sys_battery_15.png  stat_sys_battery_charge_anim15.png");
	System.err.println("\t stat_sys_battery_28.png  stat_sys_battery_charge_anim28.png");
	System.err.println("\t stat_sys_battery_43.png  stat_sys_battery_charge_anim43.png");
	System.err.println("\t stat_sys_battery_57.png  stat_sys_battery_charge_anim57.png");
	System.err.println("\t stat_sys_battery_71.png  stat_sys_battery_charge_anim71.png");
	System.err.println("\t stat_sys_battery_85.png  stat_sys_battery_charge_anim85.png");
        System.err.println("\n\njava MakeBatteryImage -h");
        System.err.println("\t shows available font names.");
        System.exit(1);
    }
    static BufferedImage readImage(String filename) {
        try {
            File fn = new File(filename);
            return ImageIO.read(fn);
        } catch (Exception e) {
            err("can't read " + filename + "\n" + e);
            return null;
        }
    }
    static void writeImage(RenderedImage im, String filename) {
        try {
            File fn = new File(filename);
            ImageIO.write(im, "PNG", fn);
        } catch (Exception e) {
            err("can't write " + filename + "\n" + e);
        }
    }

    // フォント一覧を表示する。    
    static void showFonts() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fs = ge.getAvailableFontFamilyNames();
        for (String name : fs) {
            System.out.println(name);
        }
    }

    /**
     * stat_sys_battery_0.png, stat_sys_battery_charge_anim0.png
     */
    static String orgPath = "orgimage";
    static final int maxLevels[] = new int[]{4, 15, 35, 49, 60, 75, 90, 100}; 
    static final int maxLevelName[] = new int[]{0, 15, 28, 43, 57, 71, 85, 100}; 
    static Font font;
    static int fontMaxWidth;
    public static void calcFontMaxWidth() {
        BufferedImage out = new BufferedImage(48, 36, BufferedImage.TYPE_INT_ARGB);
        Graphics g = out.getGraphics();
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(font);
        FontMetrics fm= g2.getFontMetrics();
        int width = fm.stringWidth("Fl");
        if (width > fontMaxWidth) fontMaxWidth = width;
        width = fm.stringWidth("99");
        if (width > fontMaxWidth) fontMaxWidth = width;
        width = fm.stringWidth("00");
        if (width > fontMaxWidth) fontMaxWidth = width;
    }


    public static BufferedImage createImage(BufferedImage src, int level) {
            int w = src.getWidth();
            int h = src.getHeight();
            BufferedImage out = new BufferedImage(w + fontMaxWidth + 2, h, BufferedImage.TYPE_INT_ARGB);
            Graphics g = out.getGraphics();
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setFont(font);
            g2.setColor(Color.WHITE);
            g2.drawImage(src, 0, 0, null);
            if (level == 100) {
                g2.drawString("Fl", w, h - 6);
                g2.drawString("Fl", w, h - 6);
            } else {
                g2.drawString(String.format("%02d", level), w, h - 6);
                g2.drawString(String.format("%02d", level), w, h - 6);
            }
            return out;
    }

    public static void main(String args[]) {
        int fontsize = 16;
        font = new Font(Font.MONOSPACED, Font.BOLD, fontsize);
        if (args.length < 1 || args.length >= 4) { usage(); return; }
	orgPath = args[0];

        if (orgPath.equals("-h")) { showFonts(); return; }
        if (args.length >= 3) {
            fontsize = Integer.parseInt(args[2]);
            System.out.println("fontsize= "+fontsize);
        }
        if (args.length >= 2) {
            font = new Font(args[1], 0, fontsize);
        }

        calcFontMaxWidth();

        for (int level = 0; level <= 100; level++) {
            int maxLevelId = maxLevels.length - 1;
            for (int i = 0; i < maxLevels.length; i++) {
                if (maxLevels[i] >= level) {
                    maxLevelId = i; 
                    break;
                }
            }
            BufferedImage org1 = readImage(String.format("%s/stat_sys_battery_%d.png", orgPath, maxLevelName[maxLevelId]));
            BufferedImage org2 = readImage(String.format("%s/stat_sys_battery_charge_anim%d.png", orgPath, maxLevelName[maxLevelId]));
            BufferedImage out1 = createImage(org1, level);
            BufferedImage out2 = createImage(org2, level);
            writeImage(out1, String.format("stat_sys_battery_%d.png", level));
            writeImage(out2, String.format("stat_sys_battery_charge_anim%d.png", level));
        }
    }
}
