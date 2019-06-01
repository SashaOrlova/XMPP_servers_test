public class Main {
    static final int N = 10;
    static final String FOLDER = "";
    private static final int START = 1;
    private static final int FINISH = 10;

    public static void main (String[]args) throws Exception {
        for (int i = START; i <= FINISH; i++) {
            Thread sender = new MessagesSender(i);
            sender.start();
        }
    }
}