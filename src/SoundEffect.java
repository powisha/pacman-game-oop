import javax.sound.sampled.*;
import java.io.InputStream;
import java.io.BufferedInputStream;

public class SoundEffect {
    private static Clip loopingClip;

    public static void play(String soundFile) {
        try {
            InputStream is = SoundEffect.class.getResourceAsStream("/sounds/" + soundFile);

            if (is == null) {
                System.out.println("Ses dosyası bulunamadı: " + soundFile);
                return;
            }

            BufferedInputStream bis = new BufferedInputStream(is);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(bis);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playLoop(String soundFile) {
        try {
            stopLoop();
            InputStream is = SoundEffect.class.getResourceAsStream("/sounds/" + soundFile);

            if (is == null) {
                System.out.println("Ses dosyası bulunamadı: " + soundFile);
                return;
            }

            BufferedInputStream bis = new BufferedInputStream(is);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(bis);
            loopingClip = AudioSystem.getClip();
            loopingClip.open(audioIn);
            loopingClip.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopLoop() {
        if (loopingClip != null && loopingClip.isRunning()) {
            loopingClip.stop();
            loopingClip.close();
        }
    }
}