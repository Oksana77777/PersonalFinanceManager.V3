import org.example.DataHandler;
import org.example.DataHandlerImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class DataHandlerImplTest {

    @Test
    void responsesToQueriesShouldMeetExpectations() throws IOException, ClassNotFoundException {
        DataHandlerImpl.setLoadAndSaveDataDisabled();
        DataHandler dh = DataHandlerImpl.getInstance();

        String s1 = "{\"title\": \"рулька\", \"date\": \"2022.02.08\", \"sum\": 333}";
        dh.addSale(s1);
        String s2 = "{\"title\": \"булка\", \"date\": \"2022.02.08\", \"sum\": 222}";
        String s3 = "{\"title\": \"курица\", \"date\": \"2022.02.08\", \"sum\": 222}";
        String s4 = "{\"title\": \"шапка\", \"date\": \"2022.03.08\", \"sum\": 555}";
        String r2 = "{\n" +
                "  \"maxCategory\": {\n" +
                "    \"category\": \"одежда\",\n" +
                "    \"sum\": 555\n" +
                "  },\n" +
                "  \"maxYearCategory\": {\n" +
                "    \"category\": \"одежда\",\n" +
                "    \"sum\": 555\n" +
                "  },\n" +
                "  \"maxMonthCategory\": {\n" +
                "    \"category\": \"еда\",\n" +
                "    \"sum\": 444\n" +
                "  },\n" +
                "  \"maxDayCategory\": {\n" +
                "    \"category\": \"еда\",\n" +
                "    \"sum\": 444\n" +
                "  }\n" +
                "}";
        dh.addSale(s2);
        dh.addSale(s4);
        dh.addSale(s3);
        Assertions.assertEquals(r2, dh.generateAnalysisResults());
    }
}
