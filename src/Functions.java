/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author APhan
 * 
 */
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Font;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Functions{
    
    private static List<String> coinList;
    private static List<String>coinList2;
    private static JsonArray coin100;
    private static JsonArray coin200;
    private static String currentPath;
    private static int transNum;
    
    public static Map <String, Map<Integer, Map<String, Double>>> connection() throws MalformedURLException, IOException, ClassNotFoundException{
       String sURL = "https://api.coinmarketcap.com/v2/ticker/?limit=100&structure=array";      
        URL url = new URL(sURL);
        URLConnection request = url.openConnection();
        request.connect();      
        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
        JsonObject rootobj = root.getAsJsonObject();
        JsonArray rootarr = rootobj.getAsJsonArray("data");
        coin100 = rootarr;
        
        coinList = new ArrayList<>();
        for (int x = 0; x < 100; x++){
            coinList.add(rootarr.get(x).getAsJsonObject().get("symbol").getAsString());
        }
        
        String sURL200 = "https://api.coinmarketcap.com/v2/ticker/?start=101&limit=100&structure=array";      
        URL url200 = new URL(sURL200);
        URLConnection request200 = url200.openConnection();
        request200.connect();      
        JsonParser jp200 = new JsonParser();
        JsonElement root200 = jp200.parse(new InputStreamReader((InputStream) request200.getContent()));
        JsonObject rootobj200 = root200.getAsJsonObject();
        JsonArray rootarr200 = rootobj200.getAsJsonArray("data");
        coin200 = rootarr200;
        
        coinList2 = new ArrayList<>();
        for (int z = 0; z < 100; z++){
            coinList2.add(rootarr200.get(z).getAsJsonObject().get("symbol").getAsString());
        }
        
        
        File file = new File("");
        currentPath = file.getAbsolutePath() + "\\information.txt";
        File information = new File(currentPath);
        if(!information.exists()){
            information.createNewFile();
            BufferedWriter output = new BufferedWriter(new FileWriter(currentPath, true));
            output.write("//");
            output.newLine();
            output.close();
        }
        
        String transactionPath = file.getAbsolutePath() + "\\transaction";
        File transactionsFile = new File(transactionPath);
        if (!transactionsFile.exists()){
            ObjectOutputStream tranWriter = new ObjectOutputStream(new FileOutputStream(transactionPath) );
            Map <String, Map<Integer, Map<String, Double>>> transactions = new HashMap<>();
            tranWriter.writeObject(transactions);
            tranWriter.close();
        }
        ObjectInputStream tranOpen = new ObjectInputStream(new FileInputStream(transactionPath));
        Map <String, Map<Integer, Map<String, Double>>> transaction = (Map <String, Map<Integer, Map<String, Double>>>) tranOpen.readObject();
        tranOpen.close();
        
        String transactionNumPath = file.getAbsolutePath() + "\\TransactionNumber";
        File transactionNumFile = new File(transactionNumPath);
        if(!transactionNumFile.exists()){
            ObjectOutputStream numWriter = new ObjectOutputStream(new FileOutputStream(transactionNumFile));
            numWriter.writeInt(0);
            numWriter.close();
        }
            ObjectInputStream numOpen = new ObjectInputStream(new FileInputStream(transactionNumFile));
            transNum = numOpen.readInt();
            numOpen.close();
        return transaction;
    }
    
   public static boolean USDCoin(String coin1, String coin2, String coin1amount, String coin2amount) throws IOException, MalformedURLException, ClassNotFoundException {
       Map <String, Map<Integer, Map<String, Double>>> transactions = connection();
       BufferedWriter output = new BufferedWriter(new FileWriter(currentPath, true));
       
       LocalDate localdate = LocalDate.now();
       DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("yyyy LLLL dd");
       String date = localdate.format(dateformat);
       output.write(date);
       output.newLine();
       
       output.write("Transaction: " + transNum);
       output.newLine();
       
       output.write("$" + coin1amount + " to " + coin2amount + " " + coin2);
       output.newLine();
       
       Double coinprice;
       if(coinList.contains(coin2)){
           coinprice = (coin100.get(coinList.indexOf(coin2)).getAsJsonObject().get("quotes").getAsJsonObject().get("USD").getAsJsonObject().get("price")).getAsDouble();
           output.write(coin2 + ": " + coinprice );
           output.newLine();
       }
       else{
           coinprice = (coin200.get(coinList2.indexOf(coin2)).getAsJsonObject().get("quotes").getAsJsonObject().get("USD").getAsJsonObject().get("price")).getAsDouble();
           output.write(coin2 + ": " + coinprice );
           output.newLine();
       }
       
       output.write("//");
       output.newLine();
       output.close();
       
       if (transactions.get(coin2)==null){
           Map<Integer, Map<String, Double>> placeholderOutside = new HashMap<>();
           Map<String, Double> placeholderInside = new HashMap<>();
           placeholderInside.put("price", coinprice);
           placeholderInside.put("available", Double.parseDouble(coin2amount));
           placeholderOutside.put(transNum, placeholderInside);
           transactions.put(coin2, placeholderOutside);
           ObjectOutputStream tranWrite = new ObjectOutputStream(new FileOutputStream("transaction"));
           tranWrite.writeObject(transactions);
           tranWrite.close();
       }
       else{
           Map<String, Double> placeholderInside = new HashMap<>();
           placeholderInside.put("price", coinprice);
           placeholderInside.put("available", Double.parseDouble(coin2amount));
           transactions.get(coin2).put(transNum, placeholderInside);
           ObjectOutputStream tranWrite = new ObjectOutputStream(new FileOutputStream("transaction"));
           tranWrite.writeObject(transactions);
           tranWrite.close();
       }
       
       ObjectOutputStream numWriter = new ObjectOutputStream(new FileOutputStream("TransactionNumber"));
       numWriter.writeInt(transNum+1);
       numWriter.close();
       return true;
                }
   
    public static boolean CoinCoin(String coin1, String coin2, String coin1amount, String coin2amount, JFrame frame) throws IOException, MalformedURLException, ClassNotFoundException {
        Map <String, Map<Integer, Map<String, Double>>> transactions = connection();
       BufferedWriter output = new BufferedWriter(new FileWriter(currentPath, true));
       
       LocalDate localdate = LocalDate.now();
       DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("yyyy LLLL dd");
       String date = localdate.format(dateformat);
       output.write(date);
       output.newLine();
       
       output.write("Transaction: " + transNum);
       output.newLine();
       
       output.write(coin1amount + " " + coin1 + " to " + coin2amount + " " + coin2);
       output.newLine();
       
       Double coinprice;
       Double coin2price;
       
       if(coinList.contains(coin1)){
           coinprice = (coin100.get(coinList.indexOf(coin1)).getAsJsonObject().get("quotes").getAsJsonObject().get("USD").getAsJsonObject().get("price")).getAsDouble();
           output.write(coin1 + ": " + coinprice );
           output.newLine();
       }
       else{
           coinprice = (coin200.get(coinList2.indexOf(coin1)).getAsJsonObject().get("quotes").getAsJsonObject().get("USD").getAsJsonObject().get("price")).getAsDouble();
           output.write(coin1 + ": " + coinprice );
           output.newLine();
       }
       
       if(coinList.contains(coin2)){
           coin2price = (coin100.get(coinList.indexOf(coin2)).getAsJsonObject().get("quotes").getAsJsonObject().get("USD").getAsJsonObject().get("price")).getAsDouble();
           output.write(coin2 + ": " + coin2price );
           output.newLine();
       }
       else{
           coin2price = (coin200.get(coinList2.indexOf(coin2)).getAsJsonObject().get("quotes").getAsJsonObject().get("USD").getAsJsonObject().get("price")).getAsDouble();
           output.write(coin2 + ": " + coin2price );
           output.newLine();
       }
       
       if (transactions.get(coin2)==null){
           Map<Integer, Map<String, Double>> placeholderOutside = new HashMap<>();
           Map<String, Double> placeholderInside = new HashMap<>();
           placeholderInside.put("price", coin2price);
           placeholderInside.put("available", Double.parseDouble(coin2amount));
           placeholderOutside.put(transNum, placeholderInside);
           transactions.put(coin2, placeholderOutside);
           ObjectOutputStream tranWrite = new ObjectOutputStream(new FileOutputStream("transaction"));
           tranWrite.writeObject(transactions);
           tranWrite.close();
       }
       else{
           Map<String, Double> placeholderInside = new HashMap<>();
           placeholderInside.put("price", coin2price);
           placeholderInside.put("available", Double.parseDouble(coin2amount));
           transactions.get(coin2).put(transNum, placeholderInside);
           ObjectOutputStream tranWrite = new ObjectOutputStream(new FileOutputStream("transaction"));
           tranWrite.writeObject(transactions);
           tranWrite.close();
       }
          
       Double coin1amountd = Double.parseDouble(coin1amount);
       Map<Integer, Map<String, Double>> coinMap = transactions.get(coin1);
       if (coinMap == null){
           popUpErrorOverLimit(frame);
           return false;
       }
       ArrayList<Integer> transactionList = new ArrayList<>(coinMap.keySet());
       Collections.sort(transactionList);
       
       Double totalCoins = 0.0;
       for(int num : transactionList){
           totalCoins += coinMap.get(num).get("available");
       }
       
       if(Double.parseDouble(coin1amount) > totalCoins){
           popUpErrorOverLimit(frame);
           return false;
       }
       
       for (int num : transactionList){
           if (coin1amountd > 0.0){
               if (coinMap.get(num).get("available") > 0.0){
                     if (coinMap.get(num).get("available") <= coin1amountd){              
                         coin1amountd -= coinMap.get(num).get("available");
                         Double profit = ((coinMap.get(num).get("available") * coinprice) - (coinMap.get(num).get("available") * coinMap.get(num).get("price")));
                         coinMap.get(num).put("available", 0.0);
                         output.write("Gain from transaction " + num + ": " + profit.toString());
                         output.newLine();
                     }
                     else{
                         Double profit = ((coin1amountd * coinprice) - (coin1amountd * coinMap.get(num).get("price")));
                         coinMap.get(num).put("available", coinMap.get(num).get("available") - coin1amountd);
                         coin1amountd = 0.0;
                         output.write("Gain from transaction " + num + ": " + profit.toString());
                         output.newLine();
                     }
               }
           }
       }
       
       output.write("//");
       output.newLine();
       output.close();
       
       ObjectOutputStream numWriter = new ObjectOutputStream(new FileOutputStream("TransactionNumber"));
       numWriter.writeInt(transNum+1);
       numWriter.close();
       
       ObjectOutputStream tranWrite = new ObjectOutputStream(new FileOutputStream("transaction"));
       tranWrite.writeObject(transactions);
       tranWrite.close();
       return true;
                }
    
     public static boolean CoinUSD(String coin1, String coin2, String coin1amount, String coin2amount, JFrame frame) throws IOException, MalformedURLException, ClassNotFoundException {
       Map <String, Map<Integer, Map<String, Double>>> transactions = connection();
       BufferedWriter output = new BufferedWriter(new FileWriter(currentPath, true));
       
       LocalDate localdate = LocalDate.now();
       DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("yyyy LLLL dd");
       String date = localdate.format(dateformat);
       output.write(date);
       output.newLine();
       
       output.write("Transaction: " + transNum);
       output.newLine();
       
       output.write(coin1amount + " " + coin1 + " to $" + coin2amount);
       output.newLine();
       
       Double coinprice;
       if(coinList.contains(coin1)){
           coinprice = (coin100.get(coinList.indexOf(coin1)).getAsJsonObject().get("quotes").getAsJsonObject().get("USD").getAsJsonObject().get("price")).getAsDouble();
           output.write(coin1 + ": " + coinprice );
           output.newLine();
       }
       else{
           coinprice = (coin200.get(coinList2.indexOf(coin1)).getAsJsonObject().get("quotes").getAsJsonObject().get("USD").getAsJsonObject().get("price")).getAsDouble();
           output.write(coin1 + ": " + coinprice );
           output.newLine();
       }
       
       Double coin1amountd = Double.parseDouble(coin1amount);
       Map<Integer, Map<String, Double>> coinMap = transactions.get(coin1);
       if (coinMap == null){
           popUpErrorOwn(frame);
           return false;
       }
       ArrayList<Integer> transactionList = new ArrayList<>(coinMap.keySet());
       Collections.sort(transactionList);
       
       Double totalCoins = 0.0;
       for(int num : transactionList){
           totalCoins += coinMap.get(num).get("available");
       }
       
       if(Double.parseDouble(coin1amount) > totalCoins){
           popUpErrorOverLimit(frame);
           return false;
       }
       
       for (int num : transactionList){
           if (coin1amountd > 0.0){
               if (coinMap.get(num).get("available") > 0.0){
                     if (coinMap.get(num).get("available") <= coin1amountd){              
                         coin1amountd -= coinMap.get(num).get("available");
                         Double profit = ((coinMap.get(num).get("available") * coinprice) - (coinMap.get(num).get("available") * coinMap.get(num).get("price")));
                         coinMap.get(num).put("available", 0.0);
                         output.write("Gain from transation " + num + ": " + profit.toString());
                         output.newLine();
                     }
                     else{
                         Double profit = ((coin1amountd * coinprice) - (coin1amountd * coinMap.get(num).get("price")));
                         coinMap.get(num).put("available", coinMap.get(num).get("available") - coin1amountd);
                         coin1amountd = 0.0;
                         output.write("Gain from transaction " + num + ": " + profit.toString());
                         output.newLine();
                     }
               }
           }
       }
       
       output.write("//");
       output.newLine();
       output.close();
       
       ObjectOutputStream numWriter = new ObjectOutputStream(new FileOutputStream("TransactionNumber"));
       numWriter.writeInt(transNum+1);
       numWriter.close();
       
       ObjectOutputStream tranWrite = new ObjectOutputStream(new FileOutputStream("transaction"));
       tranWrite.writeObject(transactions);
       tranWrite.close();
       return true;
                }
     
     public static void popUpErrorOwn(JFrame frame){
        JFrame doneBox = new JFrame("");
        JLabel doneLabel = new JLabel("Error: You don't own this coin", JLabel.CENTER);
        doneLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
        
        doneBox.add(doneLabel);
        doneBox.setSize(300, 100);
        doneBox.setLocationRelativeTo(frame);
        doneBox.setAlwaysOnTop(true);
        doneBox.setVisible(true);
    }
     
     public static void popUpErrorOverLimit(JFrame frame){
        JFrame doneBox = new JFrame("");
        JLabel doneLabel = new JLabel("Error: You don't own this much", JLabel.CENTER);
        doneLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
        
        doneBox.add(doneLabel);
        doneBox.setSize(300, 100);
        doneBox.setLocationRelativeTo(frame);
        doneBox.setAlwaysOnTop(true);
        doneBox.setVisible(true);
     }
     
}


