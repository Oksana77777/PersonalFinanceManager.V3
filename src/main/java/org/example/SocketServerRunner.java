package org.example;

import java.io.*;
import java.net.ServerSocket;

public class SocketServerRunner implements Runnable {
    @Override
    public void run() {
        try (var serverSocket = new ServerSocket(8989)) {
            DataHandler dh = DataHandlerImpl.getInstance();

            while (true) {
                try (
                        var socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                    String response;
                    var request = in.readLine();
                    dh.addSale(request);
                    response = dh.generateAnalysisResults();
                    out.println(response);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }
    }
}
