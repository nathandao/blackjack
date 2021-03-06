package blackjack;

import blackjack.io.Port;
import blackjack.io.PortReaderThread;
import blackjack.utils.Config;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            String filename = args[0];
            System.out.println("Reading config from " + filename);

            Config config = Config.readFromFile(filename);
            if (args.length > 1 && config != null) {
                String com = args[1];
                System.out.println("Overriding config with com port: " + com);
                config.comPort = com;
            }
            PortReaderThread t = new PortReaderThread();
            t.initialize(config);
            t.start();

            System.out.println("Press Enter to quit...");
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }

            t.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
