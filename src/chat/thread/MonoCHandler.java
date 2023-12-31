package chat.thread;

import chat.logger.MyLog;
import chat.logger.ServerLog;
import java.io.*;
import java.net.Socket;

public class MonoCHandler implements Runnable {
    private final MyLog myLogger;
    private static Socket clientDialog;
    ServerLog LOGGER;

    public MonoCHandler(Socket client, ServerLog LOGGER) {
        MonoCHandler.clientDialog = client;
        this.LOGGER = LOGGER;
        myLogger = MyLog.getInstance();
    }

    @Override
    public void run() {
        try {
            PrintWriter out = new PrintWriter(clientDialog.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientDialog.getInputStream()));
            System.out.println("Запись и чтение для приема и вывода создана");

            final String name = in.readLine() + "[" + clientDialog.getPort() + "]";
            LOGGER.log("Новый пользователь", LOGGER.getAnsiGreen(),
                    String.format(">>%s", name));

            while (!clientDialog.isClosed()) {
                final String msg = in.readLine();
                System.out.println("Прочитали сообщение от " + name + ": " + msg);

                if (msg.equalsIgnoreCase("/end")) {
                    LOGGER.log("Покинул чат", LOGGER.getAnsiRed(),
                            String.format(">>%s", name));
                    out.println("Сервер ожидает - " + msg + " - ОК");
                    Thread.sleep(1000);
                    break;
                }
                //не получили выход, значит работаем
                System.out.println("Сервер готов записывать....");
                String msgAndUser = name + ":" + msg;
                out.println(">>>" + msgAndUser + " - ОК");
                myLogger.log(name, msg);
                System.out.println("Сервер записал сообщение");
                out.flush();
            }
            in.close();
            out.close();
            clientDialog.close();
            LOGGER.log("Закрытие канала с пользователем", LOGGER.getAnsiPurple(),
                    String.format(">>[%s]- Выполнено", name));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}