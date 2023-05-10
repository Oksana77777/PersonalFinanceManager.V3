package org.example;

import java.io.IOException;

public interface DataHandler {

    void addSale(String newSaleForAdd) throws IOException;
    String generateAnalysisResults();
}