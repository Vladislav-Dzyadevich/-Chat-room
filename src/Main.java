
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static Scanner scannerToInt = new Scanner(System.in);

    public static void main(String[] args) {

        try {
            while (true) {
                System.out.println("1. Registration");
                System.out.println("2. Login and send message");
                System.out.println("3. Logout");
                System.out.println("4. Show users");
                int choice = scannerToInt.nextInt();

                switch (choice) {
                    case 1:
                        userRegistration();
                        break;
                    case 2:
                        User user = login();
                        if (user != null) {
                            sendMessage(user);
                        }
                        break;
                    case 3:
                        logout();
                        break;
                    case 4:
                        showUsers();
                        break;
                    case 5:
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            scanner.close();
        }

    }

    public static void showUsers() {
        try {
            URL obj = new URL(Utils.getURL() + "/getting-users");
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);

            InputStream is = conn.getInputStream();

            String strBuf = new String(GetThread.requestBodyToArray(is), StandardCharsets.UTF_8);
            Gson gson = new GsonBuilder().create();
            String[] users = gson.fromJson(strBuf, String.class).split("next");

            for (String user : users)
                System.out.println(user);
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }



    private static void userRegistration() throws IOException {
        System.out.println("Enter your login:");
        String login = scanner.nextLine();
        System.out.println("Enter your password:");
        String password = scanner.nextLine();
        User user = new User(login, password);
        int resultCode = user.send(Utils.getURL() + "/registration");

        if (resultCode != 200) {
            System.out.println("HTTP error:" + resultCode);
        }
    }

    private static User login() throws IOException {
        System.out.println("Enter your login:");
        String login = scanner.nextLine();
        System.out.println("Enter your password:");
        String password = scanner.nextLine();
        User user = new User(login, password);
        int resultCode = user.send(Utils.getURL() + "/login");

        if (resultCode != 200) {
            System.out.println("HTTP error:" + resultCode);
            return null;
        } else {
            System.out.println("User is online");
            return user;
        }
    }

    private static void sendMessage(User user) throws IOException {
        Thread th = new Thread(new GetThread());
        th.setDaemon(true);
        th.start();

        System.out.println("Enter your message:");
        while (true) {
            String text = scanner.nextLine();
            if (text.isEmpty()) {
                break;
            }
            Message message = new Message(user.getLogin(), text);
            int res = message.send(Utils.getURL() + "/add");

            if (res != 200) {
                System.out.println("HTTP error occured: " + res);
                return;
            }
        }
    }

    private static User logout() throws IOException {
        System.out.println("Enter your login:");
        String login = scanner.nextLine();
        System.out.println("Enter your password:");
        String password = scanner.nextLine();
        User user = new User(login, password);
        int resultCode = user.send(Utils.getURL() + "/logout");

        if (resultCode != 200) {
            System.out.println("HTTP error:" + resultCode);
            return null;
        } else {
            System.out.println("User is offline");
            return user;
        }
    }
}
