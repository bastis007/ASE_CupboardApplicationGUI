import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class CupboardApplicationGUI {

    JFrame frame;

    public CupboardApplicationGUI() throws IOException, InterruptedException {
        frame = new JFrame();
        GraphicsEnvironment graphics = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = graphics.getDefaultScreenDevice();
        device.setFullScreenWindow(frame);
        frame.setTitle("Personal digital Cupboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        JButton retrieveButton = new JButton("Get Cupboard");
        retrieveButton.addActionListener(e -> {
            try {getCupboardData();}
            catch (IOException | InterruptedException ex) {ex.printStackTrace();}
        });
        JButton addButton = new JButton("Add Item");
        addButton.addActionListener(e -> {
            addGarmentInputField();
        });
        JButton deleteButton = new JButton("Delete Item");
        buttonPanel.add(retrieveButton);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        String data[][] = getCupboardData();
        String columns[] = getTableColumns();
        JTable table = new JTable();
        DefaultTableModel model = new DefaultTableModel(data, columns);
        table.setModel(model);
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(table, BorderLayout.CENTER);
        frame.add(panel);
        frame.setVisible(true);
    }

    private void addGarmentInputField() {
        JFrame jf = new JFrame ();
        jf.setSize (350, 300);
        BorderLayout borderLayout = new BorderLayout();
        jf.setLayout (borderLayout);
        GridLayout gridLayout = new GridLayout(4,2);
        JPanel panel = new JPanel();
        panel.setLayout(gridLayout);
        JLabel labelId = new JLabel ("Enter ID (optional)");
        JTextField fieldId = new JTextField (10);
        JLabel labelType = new JLabel ("Enter Type)");
        String[] optionsToChoose = {"T-Shirt", "Jeans", "Socks", "Underpants", "Hoodie", "None"};
        JComboBox<String> fieldType = new JComboBox<>(optionsToChoose);
        fieldType.setBounds(80, 50, 140, 20);
        JLabel labelSize = new JLabel ("Enter Size");
        JTextField fieldSize = new JTextField (10);
        JLabel labelColour = new JLabel ("Enter Colour");
        JTextField fieldColour = new JTextField (10);
        JButton jb = new JButton ("Submit");
        jb.addActionListener (e -> {
            String id = fieldId.getText();
            String type = (String) fieldType.getSelectedItem();
            String size = fieldSize.getText();
            String colour = fieldColour.getText();
            if(size.isEmpty()){
                JOptionPane.showMessageDialog(null,"Size must not be empty", "Insert Error", JOptionPane.ERROR_MESSAGE);
            }
            else if(colour.isEmpty()){
                JOptionPane.showMessageDialog(null,"Colour must not be empty", "Insert Error", JOptionPane.ERROR_MESSAGE);
            }
            else {
                try {
                    String body;
                    if(id.isEmpty()){
                        body = "{\"type\":\"" + type + "\", \"size\":\"" + size + "\", \"colour\":\"" + colour + "\"}";
                    }
                    else{
                        body = "{\"id\":\"" + id + "\", \"type\":\"" + type + "\", \"size\":\"" + size + "\", \"colour\":\"" + colour + "\"}";
                    }
                    addGarment(body);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        panel.add(labelId);
        panel.add(fieldId);
        panel.add(labelType);
        panel.add(fieldType);
        panel.add(labelSize);
        panel.add(fieldSize);
        panel.add(labelColour);
        panel.add(fieldColour);
        jf.add(panel, BorderLayout.CENTER);
        jf.add (jb, BorderLayout.SOUTH);
        jf.setVisible (true);
    }

    private void addGarment(String body) throws IOException {
        String url = "http://localhost:8080/garment/";
        URL urlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        try(OutputStream os = connection.getOutputStream()) {
            byte[] input = body.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        int responseCode = connection.getResponseCode();
        if(responseCode == 201){
            System.out.println("Response Code : " + responseCode);
            JOptionPane.showMessageDialog(null,"New garment succesfully added");
        }
        if(responseCode > 201){
            System.out.println("Response Code : " + responseCode);
            JOptionPane.showMessageDialog(null,"Internal error while inserting the garment. Please try again", "Insert Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String[] getTableColumns() {
        String[] columns = {"ID", "Type", "Size", "Colour"};
        return columns;
    }

    private String[][] getTableData() {
        String[][] data = {{"tshirt1", "T-Shirt", "L", "Green"}, {"socks1", "Socks", "44-46", "Black"}};
        return data;
    }

    private String[][] getCupboardData() throws IOException, InterruptedException {
        String url = "http://localhost:8080/garment/";
        URL urlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code : " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) {
           BufferedReader inputReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = inputReader.readLine()) != null) {
                response.append(inputLine);
            }
            inputReader.close();
            String tableContent = response.toString();
            String[] table = tableContent.split("\"id\":");
            int l = 0;
            String[][] data = new String[table.length][4];
            for (String s : table) {
                String[] tab = s.split(",\"_links\":");
                for (int j = 0; j < tab.length; j++) {
                    if (j % 2 != 0) {
                        tab[j] = null;
                    }
                    if (j + 1 == tab.length) {
                        tab[j] = null;
                    }
                }
                for (int j = 0; (j + 1) < tab.length; j++) {
                    if (tab[j] == null && j < tab.length) {
                        tab[j] = tab[j + 1];
                    }
                    if (tab[j] == null) {
                        tab[j] = tab[j - 1];
                    }
                    String[] t = tab[j].split(",");
                    for (int k = 0; k < t.length; k++) {
                        t[k] = t[k].replace("\"type\":", "");
                        t[k] = t[k].replace("\"size\":", "");
                        t[k] = t[k].replace("\"colour\":", "");
                        t[k] = t[k].replace("\"", "");
                        t[k] = t[k].replace("\"", "");
                        data[l][k] = t[k];
                        if (k == 3) {
                            l++;
                        }
                    }
                }
            }
            return data;
        }
        return null;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new CupboardApplicationGUI();
    }
}