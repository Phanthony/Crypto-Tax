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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Functions{
    
    private static List<String> coinList;
    private static List<String>coinList2;
    private static JsonArray coin100;
    private static JsonArray coin200;
    
    public static String connection() throws MalformedURLException, IOException{
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
        coinList.add("USD");
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
        String currentPath = file.getAbsolutePath() + "\\information.txt";
        File information = new File(currentPath);
        if(!information.exists()){
            information.createNewFile();
            BufferedWriter output = new BufferedWriter(new FileWriter(currentPath, true));
            output.write("Current 0");
            output.newLine();
            output.newLine();
            output.newLine();
            output.write("//");
            output.newLine();
            output.close();
        }
        return currentPath;
    }
    
   public static void USDCoin(String coin1, String coin2, String coin1amount, String coin2amount) throws IOException {
       String currentPath = connection();
       BufferedWriter output = new BufferedWriter(new FileWriter(currentPath, true));
       
       LocalDate localdate = LocalDate.now();
       DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("yyyy LLLL dd");
       String date = localdate.format(dateformat);
       output.write(date);
       output.newLine();
       
       output.write("$" + coin1amount + " to " + coin2amount + " " + coin2);
       output.newLine();
       
       if(coinList.contains(coin2)){
           Double coinprice = (coin100.get(coinList.indexOf(coin2)-1).getAsJsonObject().get("quotes").getAsJsonObject().get("USD").getAsJsonObject().get("price")).getAsDouble();
           output.write(coin2 + ": " + coinprice );
           output.newLine();
       }
       else{
           Double coinprice = (coin100.get(coinList2.indexOf(coin2)-1).getAsJsonObject().get("quotes").getAsJsonObject().get("USD").getAsJsonObject().get("price")).getAsDouble();
           output.write(coin2 + ": " + coinprice );
           output.newLine();
       }
       
       output.write("available " + coin2amount);
       output.newLine();
       output.write("//");
       output.newLine();
       output.close();
                }
   
    public static void CoinCoin(String coin1, String coin2, String coin1amount, String coin2amount) throws IOException {
       String currentPath = connection();
       BufferedWriter output = new BufferedWriter(new FileWriter(currentPath, true));
       
       LocalDate localdate = LocalDate.now();
       DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("yyyy LLLL dd");
       String date = localdate.format(dateformat);
       output.write(date);
       output.newLine();
       
       output.write(coin1amount + " " + coin1 + " to " + coin2amount + " " + coin2);
       output.newLine();
       
       if(coinList.contains(coin1)){
           Double coinprice = (coin100.get(coinList.indexOf(coin1)-1).getAsJsonObject().get("quotes").getAsJsonObject().get("USD").getAsJsonObject().get("price")).getAsDouble();
           output.write(coin1 + ": " + coinprice );
           output.newLine();
       }
       else{
           Double coinprice = (coin100.get(coinList2.indexOf(coin1)-1).getAsJsonObject().get("quotes").getAsJsonObject().get("USD").getAsJsonObject().get("price")).getAsDouble();
           output.write(coin1 + ": " + coinprice );
           output.newLine();
       }
       
          if(coinList.contains(coin2)){
           Double coinprice = (coin100.get(coinList.indexOf(coin2)-1).getAsJsonObject().get("quotes").getAsJsonObject().get("USD").getAsJsonObject().get("price")).getAsDouble();
           output.write(coin2 + ": " + coinprice );
           output.newLine();
       }
       else{
           Double coinprice = (coin100.get(coinList2.indexOf(coin2)-1).getAsJsonObject().get("quotes").getAsJsonObject().get("USD").getAsJsonObject().get("price")).getAsDouble();
           output.write(coin2 + ": " + coinprice );
           output.newLine();
       }
       
       output.write("available " + coin2amount);
       output.newLine();
       output.write("//");
       output.newLine();
       output.close();
                }
    
     public static void CoinUSD(String coin1, String coin2, String coin1amount, String coin2amount) throws IOException {
       String currentPath = connection();
       BufferedWriter output = new BufferedWriter(new FileWriter(currentPath, true));
       
       LocalDate localdate = LocalDate.now();
       DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("yyyy LLLL dd");
       String date = localdate.format(dateformat);
       output.write(date);
       output.newLine();
       
       output.write(coin1amount + " " + coin1 + " to $" + coin2amount);
       output.newLine();
       
       if(coinList.contains(coin1)){
           Double coinprice = (coin100.get(coinList.indexOf(coin1)-1).getAsJsonObject().get("quotes").getAsJsonObject().get("USD").getAsJsonObject().get("price")).getAsDouble();
           output.write(coin1 + ": " + coinprice );
           output.newLine();
       }
       else{
           Double coinprice = (coin100.get(coinList2.indexOf(coin1)-1).getAsJsonObject().get("quotes").getAsJsonObject().get("USD").getAsJsonObject().get("price")).getAsDouble();
           output.write(coin1 + ": " + coinprice );
           output.newLine();
       }
       
       output.write("//");
       output.newLine();
       output.close();
                }
}