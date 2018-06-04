
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author APhan
 */
public class Menu implements ActionListener, FocusListener{
    
    private JComboBox coinListBox1;
    private JComboBox coinListBox2;
    private JTextField amount1;
    private JTextField amount2;
    private JButton transaction;
    
    private String coin1;
    private String coin2;
    
    private String coinamount1;
    private String  coinamount2;
    
    public void start() throws IOException{
        String sURL = "https://api.coinmarketcap.com/v2/ticker/?limit=100&structure=array";      
        URL url = new URL(sURL);
        URLConnection request = url.openConnection();
        request.connect();      
        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
        JsonObject rootobj = root.getAsJsonObject();
        JsonArray rootarr = rootobj.getAsJsonArray("data");
        
        List<String> coinList = new ArrayList<>();
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
        
        for (int z = 0; z < 100; z++){
            coinList.add(rootarr200.get(z).getAsJsonObject().get("symbol").getAsString());
        }
              
        String[] coinarray = coinList.toArray(new String[0]);
        Arrays.sort(coinarray);
        
        JFrame MainFrame = new JFrame ("Crypto Taxes");
        MainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MainFrame.getContentPane().setLayout(new GridLayout(2, 3));
        
        coinListBox1 = new JComboBox(coinarray);
        coinListBox2 = new JComboBox(coinarray);
        AutoCompletion.enable(coinListBox1);
        AutoCompletion.enable(coinListBox2);
        
        amount1 = new JTextField();
        amount2 = new JTextField();
        
        JLabel newLabel = new JLabel("To", JLabel.CENTER);
        
        transaction = new JButton("Add Transaction");
        
        MainFrame.add(coinListBox1);
        MainFrame.add(newLabel);
        MainFrame.add(coinListBox2);
        MainFrame.add(amount1);
        MainFrame.add(transaction);
        MainFrame.add(amount2);
        MainFrame.setSize(500, 150);
        MainFrame.setVisible(true);
        
        coinListBox1.addActionListener(this);
        coinListBox2.addActionListener(this);
        amount1.addFocusListener(this);
        amount2.addFocusListener(this);
        transaction.addActionListener(this);
        
    }
    
    @Override
    public void actionPerformed(ActionEvent action){
        if (action.getSource() == coinListBox1){
            coin1 = (String)coinListBox1.getSelectedItem();
        }
        if (action.getSource() == coinListBox2){
            coin2 = (String)coinListBox2.getSelectedItem();
        }

        if (action.getSource() == transaction){
            if (coin1.equals("USD") && !coin2.equals("USD")){
                try {
                    Functions.USDCoin(coin1, coin2, coinamount1, coinamount2);
                } catch (IOException ex) {
                    Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else if (!coin1.equals("USD") && coin2.equals("USD")){
                try {
                    Functions.CoinUSD(coin1, coin2, coinamount1, coinamount2);
                } catch (IOException ex) {
                    Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else{
                try {
                    Functions.CoinCoin(coin1, coin2, coinamount1, coinamount2);
                } catch (IOException ex) {
                    Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }    
    }
    
    @Override
    public void focusLost(FocusEvent action){
         if (action.getSource() == amount1){
            coinamount1 = (amount1.getText());
        }
        if (action.getSource() == amount2){
            coinamount2 = (amount2.getText());
        }
        
    }
    
    @Override
    public void focusGained(FocusEvent action){
        return;
    }
}