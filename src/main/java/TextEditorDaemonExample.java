public class TextEditorDaemonExample {

    public static void main(String[] args) {
        // 1. Create the Background Auto-Save Task
        Thread autoSaver = getAutoSaver();
        autoSaver.start();

        // 3. The Main Thread (The User)
        System.out.println("[Main] User started typing...");
        try {
            for (int i = 0; i < 3; i++) {
                System.out.println("[Main] User is typing sentence " + (i + 1));
                Thread.sleep(800); // User types for a bit
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("[Main] User closed the editor. Goodbye!");
        // 4. JVM exits here. The Auto-Saver is killed instantly.
    }

    private static Thread getAutoSaver() {
        Thread autoSaver = new Thread(() -> {
            while (true) {
                try {
                    System.out.println("[Auto-Save] Saving your work to disk...");
                    Thread.sleep(1000); // Saves every 1 second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // 2. CRITICAL: Make it a Daemon
        // If you comment this line out, the program will NEVER end!
        autoSaver.setDaemon(true);
        return autoSaver;
    }
}