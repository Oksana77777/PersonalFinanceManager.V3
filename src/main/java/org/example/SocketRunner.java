package org.example;

import java.io.*;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.Scanner;
import java.util.stream.Collectors;


public class SocketRunner {
    public static void main(String[] args) throws IOException {

        var inetAddress = Inet4Address.getByName("localhost");

        try (var socket = new Socket(inetAddress, 8989);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            var scanner = new Scanner(System.in);
            var request = scanner.nextLine();
            out.println(request);
            String response;
            while ((response = in.readLine()) != null) {
                System.out.println(response);
            }
        }
    }
}
