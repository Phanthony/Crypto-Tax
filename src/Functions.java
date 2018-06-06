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
import java.awt.GridLayout;

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
import javax.swing.JComboBox;
import javax.swing.JButton;

import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Functions implements WindowListener, ActionListener{
    
    private static List<String> coinList;
    private static List<String>coinList2;
    private static JsonArray coin100;
    private static JsonArray coin200;
    private static String currentPath;
    private static int transNum;
    
    private String selectedCoin;
    private JFrame availFrame;
    private JButton availButton;
    private JFrame frame;
    private JComboBox coinDropList;
    
    public Functions(JFrame frame){
        this.frame = frame;
    }
    
    public Map <String, Map<Integer, Map<String, Double>>> connection() throws MalformedURLException, IOException, ClassNotFoundException{
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
        
        String transactionPath = file.getAbsolutePath() + "\\Transaction.data";
        File transactionsFile = new File(transactionPath);
        if (!transactionsFile.exists()){
            ObjectOutputStream tranWriter = new ObjectOutputStream(new FileOutputStream(transactionPath) );
            Map <String, Map<Integer, Map<String, Double>>> transactions = new HashMap<>();
            tranWriter.writeObject(transactions);
            tranWriter.writeInt(1);
            tranWriter.close();
        }
        ObjectInputStream tranOpen = new ObjectInputStream(new FileInputStream(transactionPath));
        Map <String, Map<Integer, Map<String, Double>>> transaction = (Map <String, Map<Integer, Map<String, Double>>>) tranOpen.readObject();
        transNum = tranOpen.readInt();
        tranOpen.close();
        
        return transaction;
    }
    
   public boolean USDCoin(String coin1, String coin2, String coin1amount, String coin2amount) throws IOException, MalformedURLException, ClassNotFoundException {
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
           output.write("Price of " + coin2 + " : $" + coinprice );
           output.newLine();
       }
       else{
           coinprice = (coin200.get(coinList2.indexOf(coin2)).getAsJsonObject().get("quotes").getAsJsonObject().get("USD").getAsJsonObject().get("price")).getAsDouble();
           output.write("Price of " + coin2 + " : $" + coinprice );
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
           ObjectOutputStream tranWrite = new ObjectOutputStream(new FileOutputStream("Transaction.data"));
           tranWrite.writeObject(transactions);
           tranWrite.writeInt(transNum+1);
           tranWrite.close();
       }
       else{
           Map<String, Double> placeholderInside = new HashMap<>();
           placeholderInside.put("price", coinprice);
           placeholderInside.put("available", Double.parseDouble(coin2amount));
           transactions.get(coin2).put(transNum, placeholderInside);
           ObjectOutputStream tranWrite = new ObjectOutputStream(new FileOutputStream("Transaction.data"));
           tranWrite.writeObject(transactions);
           tranWrite.writeInt(transNum+1);
           tranWrite.close();
       }
       
       return true;
                }
   
    public boolean CoinCoin(String coin1, String coin2, String coin1amount, String coin2amount) throws IOException, MalformedURLException, ClassNotFoundException {
        Map <String, Map<Integer, Map<String, Double>>> transactions = connection();
       BufferedWriter output = new BufferedWriter(new FileWriter(currentPath, true));
       
       Double coin1amountd = Double.parseDouble(coin1amount);
       Map<Integer, Map<String, Double>> coinMap = transactions.get(coin1);
       if (coinMap == null){
           popUpErrorOwn();
           return false;
       }
       
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
           output.write("Price of " + coin1 + " : $" + coinprice );
           output.newLine();
       }
       else{
           coinprice = (coin200.get(coinList2.indexOf(coin1)).getAsJsonObject().get("quotes").getAsJsonObject().get("USD").getAsJsonObject().get("price")).getAsDouble();
           output.write("Price of " + coin1 + " : $" + coinprice );
           output.newLine();
       }
       
       if(coinList.contains(coin2)){
           coin2price = (coin100.get(coinList.indexOf(coin2)).getAsJsonObject().get("quotes").getAsJsonObject().get("USD").getAsJsonObject().get("price")).getAsDouble();
           output.write("Price of " + coin2 + " : $" + coin2price );
           output.newLine();
       }
       else{
           coin2price = (coin200.get(coinList2.indexOf(coin2)).getAsJsonObject().get("quotes").getAsJsonObject().get("USD").getAsJsonObject().get("price")).getAsDouble();
           output.write("Price of " + coin2 + " : $" + coin2price );
           output.newLine();
       }
       
       if (transactions.get(coin2)==null){
           Map<Integer, Map<String, Double>> placeholderOutside = new HashMap<>();
           Map<String, Double> placeholderInside = new HashMap<>();
           placeholderInside.put("price", coin2price);
           placeholderInside.put("available", Double.parseDouble(coin2amount));
           placeholderOutside.put(transNum, placeholderInside);
           transactions.put(coin2, placeholderOutside);
       }
       else{
           Map<String, Double> placeholderInside = new HashMap<>();
           placeholderInside.put("price", coin2price);
           placeholderInside.put("available", Double.parseDouble(coin2amount));
           transactions.get(coin2).put(transNum, placeholderInside);
       }
          
       ArrayList<Integer> transactionList = new ArrayList<>(coinMap.keySet());
       Collections.sort(transactionList);
       
       Double totalCoins = 0.0;
       for(int num : transactionList){
           totalCoins += coinMap.get(num).get("available");
       }
       
       if(Double.parseDouble(coin1amount) > totalCoins){
           popUpErrorOverLimit();
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
       
       ObjectOutputStream tranWrite = new ObjectOutputStream(new FileOutputStream("Transaction.data"));
       tranWrite.writeObject(transactions);
       tranWrite.writeInt(transNum+1);
       tranWrite.close();
       return true;
                }
    
     public boolean CoinUSD(String coin1, String coin2, String coin1amount, String coin2amount) throws IOException, MalformedURLException, ClassNotFoundException {
       Map <String, Map<Integer, Map<String, Double>>> transactions = connection();
       BufferedWriter output = new BufferedWriter(new FileWriter(currentPath, true));
       
       Double coin1amountd = Double.parseDouble(coin1amount);
       Map<Integer, Map<String, Double>> coinMap = transactions.get(coin1);
       if (coinMap == null){
           popUpErrorOwn();
           return false;
       }
       
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
           output.write("Price of " + coin1 + " : $" + coinprice );
           output.newLine();
       }
       else{
           coinprice = (coin200.get(coinList2.indexOf(coin1)).getAsJsonObject().get("quotes").getAsJsonObject().get("USD").getAsJsonObject().get("price")).getAsDouble();
           output.write("Price of " + coin1 + ": $" + coinprice );
           output.newLine();
       }
       
       ArrayList<Integer> transactionList = new ArrayList<>(coinMap.keySet());
       Collections.sort(transactionList);
       
       Double totalCoins = 0.0;
       for(int num : transactionList){
           totalCoins += coinMap.get(num).get("available");
       }
       
       if(Double.parseDouble(coin1amount) > totalCoins){
           popUpErrorOverLimit();
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
       
       ObjectOutputStream tranWrite = new ObjectOutputStream(new FileOutputStream("Transaction.data"));
       tranWrite.writeObject(transactions);
       tranWrite.writeInt(transNum+1);
       tranWrite.close();
       return true;
                }
     
     public void transactionPopUp () throws IOException, MalformedURLException, ClassNotFoundException{
         
          Map <String, Map<Integer, Map<String, Double>>> transactions = connection();
         
          availFrame = new JFrame("Available Coins");
          availFrame.getContentPane().setLayout(new GridLayout(2,1));  
          
          if (transactions.isEmpty()){
              JFrame error = new JFrame("Error");
              JLabel errorL = new JLabel("You don't have any coins", JLabel.CENTER);
              errorL.setFont(new Font("Times New Roman", Font.BOLD, 20));
              error.add(errorL);
              error.setLocationRelativeTo(frame);
              error.setAlwaysOnTop(true);
              error.setSize(350, 100);
              error.setVisible(true);
              return;
          }
          
          ArrayList<String> transCoin = new ArrayList<>(transactions.keySet());
          Collections.sort(transCoin);
          transCoin.add(0, "");
          coinDropList = new JComboBox(transCoin.toArray());
          AutoCompletion.enable(coinDropList);
          
          availButton = new JButton("Show Coin Availability");
         
          availFrame.setSize(500,150);
          availFrame.setLocationRelativeTo(frame);
          availFrame.add(coinDropList);
          availFrame.add(availButton);
          availFrame.setVisible(true);
          
                   frame.setVisible(false);
                   
          availFrame.addWindowListener(this);
          coinDropList.addActionListener(this);
          availButton.addActionListener(this);
     }
     
     public void availCoins (String coin) throws IOException, MalformedURLException, ClassNotFoundException{
          Map <String, Map<Integer, Map<String, Double>>> transactions = connection();
          ArrayList<Integer> transList = new ArrayList<>(transactions.get(coin).keySet());
          Collections.sort(transList);
          
          Font font = new Font("Times New Roman", Font.BOLD, 20);
          
          JFrame main = new JFrame(coin);
          JPanel container = new JPanel(new GridLayout(0,1));
          
          for(int num : transList){
              JPanel pan = new JPanel(new GridLayout(0,1));
              JLabel tranNum = new JLabel("Transaction Number: " + num);
              tranNum.setFont(font);
              JLabel available = new JLabel("Available: " + transactions.get(coin).get(num).get("available"));
              available.setFont(font);
              JLabel price = new JLabel("Price bought at: " + transactions.get(coin).get(num).get("price"));
              price.setFont(font);
              pan.add(tranNum);
              pan.add(price);
              pan.add(available);
              pan.add(new JLabel(""));
              container.add(pan);
          }
          JScrollPane scrollbar = new JScrollPane(container);
          main.add(scrollbar);
          
          main.setLocationRelativeTo(availFrame);
          main.setSize(370,500);
          main.setVisible(true);
     }
     
     public void popUpErrorOwn(){
        JFrame doneBox = new JFrame("Error");
        JLabel doneLabel = new JLabel("You don't own this coin", JLabel.CENTER);
        doneLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
        
        doneBox.add(doneLabel);
        doneBox.setSize(350, 100);
        doneBox.setLocationRelativeTo(frame);
        doneBox.setAlwaysOnTop(true);
        doneBox.setVisible(true);
    }
     
     public void popUpErrorOverLimit(){
        JFrame doneBox = new JFrame("Error");
        JLabel doneLabel = new JLabel("You don't own this much", JLabel.CENTER);
        doneLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
        
        doneBox.add(doneLabel);
        doneBox.setSize(350, 100);
        doneBox.setLocationRelativeTo(frame);
        doneBox.setAlwaysOnTop(true);
        doneBox.setVisible(true);
     }
     
     
    @Override
    public void windowOpened(WindowEvent e) {
       return;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if(e.getSource() == availFrame){
        frame.setVisible(true);
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {
      return;
    }

    @Override
    public void windowIconified(WindowEvent e) {
        return;
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        return;
    }

    @Override
    public void windowActivated(WindowEvent e) {
        return;
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
       return;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == availButton){
            try {
                availCoins(selectedCoin);
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(Functions.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(e.getSource() == coinDropList){
            selectedCoin = (String)coinDropList.getSelectedItem();
        }
    }
     
}


