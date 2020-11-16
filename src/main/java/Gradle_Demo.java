import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import org.patriques.AlphaVantageConnector;
import org.patriques.TimeSeries;
import org.patriques.input.timeseries.Interval;
import org.patriques.input.timeseries.OutputSize;
import org.patriques.output.AlphaVantageException;
import org.patriques.output.timeseries.IntraDay;
import org.patriques.output.timeseries.data.StockData;

import java.io.ByteArrayOutputStream;

import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;



public class Gradle_Demo {


    public static void main(String[] args) throws Exception {
        String username = "interviewee";
        String password = "hunter2";
        String host = "exam.decg.io";
        int port = 22;
        String command = "java Gradle_Demo";



        String apiKey = "BC424EHYLXT5UDWU";

        int timeout = 3000;

        AlphaVantageConnector apiConnector = new AlphaVantageConnector(apiKey, timeout);
        TimeSeries stockTimeSeries = new TimeSeries(apiConnector);

        Scanner myScanner = new Scanner(System.in);
        System.out.println("Please enter stock symbol: ");
        String stockName = "";
        Double guessedPrice = 0.0;
        if (myScanner.hasNext()) {
            stockName = myScanner.nextLine();
        }

        System.out.println("Please enter your guessed price: ");
        if (myScanner.hasNextDouble()) {
            guessedPrice = myScanner.nextDouble();
        }
        myScanner.close();

        String answer = guessPrice(stockName, stockTimeSeries, guessedPrice);

        System.out.println(answer);
        listFolderStructure(username, password, host, port, command);
    }

    public static String guessPrice(String stockName, TimeSeries stockTimeSeries, Double guessedPrice) {
        if (stockName == null || stockName.length() == 0) {
            return "Invalid Stock Symbol";
        }
        try {
            IntraDay response = stockTimeSeries.intraDay(stockName, Interval.ONE_MIN, OutputSize.COMPACT);
            List<StockData> stockData = response.getStockData();
            StockData target = stockData.get(0);
            int targetPrice = (int)Math.round(target.getClose());
            int roundGuessPrice = (int) Math.round(guessedPrice);

            if (targetPrice == roundGuessPrice) {
                return "Correct";
            } else if (targetPrice > roundGuessPrice) {
                return "Too low";
            } else {
                return "Too high";
            }

        } catch (AlphaVantageException e) {
            return "something went wrong";
        }
    }

    public static void listFolderStructure(String username, String password, String host, int port, String command) throws Exception {
        Session session = null;
        ChannelExec channel = null;

        try {
            session = new JSch().getSession(username, host, port);
            session.setPassword(password);
            Hashtable<String, String> table = new Hashtable<>();
            table.put("StrictHostKeyChecking", "no");
            session.setConfig(table);
            session.connect();

            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            channel.setOutputStream(responseStream);
            channel.connect();


            while (channel.isConnected()) {
                Thread.sleep(100);
            }

            String responseString = new String(responseStream.toByteArray());
            System.out.println(responseString);
        } finally {
            if (session != null) {
                session.disconnect();
            }
            if (channel != null) {
                channel.disconnect();
            }
        }
    }


}
