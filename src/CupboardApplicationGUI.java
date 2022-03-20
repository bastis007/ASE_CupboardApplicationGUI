import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class CupboardApplicationGUI {

    JFrame frame;
    JTable table;

    public CupboardApplicationGUI() throws IOException, InterruptedException {
        frame = new JFrame();
        GraphicsEnvironment graphics = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = graphics.getDefaultScreenDevice();
        device.setFullScreenWindow(frame);
        frame.setTitle("Personal digital Cupboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        table = createJTable();
        setUpTableData();
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JPanel buttonPanel = createJButtons(table);
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(table, BorderLayout.CENTER);
        frame.add(panel);
        frame.setVisible(true);
    }

    private void deleteGarment(String id) throws IOException {
        String url = "http://localhost:8080/garment/" + id + "/";
        String requestMethod = "DELETE";
        HttpURLConnection connection = getHttpPOSTURLConnection(url, requestMethod);
        id = "{\"id\":\"" + id + "\"}";
        try(OutputStream os = connection.getOutputStream()) {
            byte[] input = id.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        httpResponse(connection);
    }

    private void updateGarmentInputField(String id, String type, String size, String colour) {
        JFrame jf = new JFrame ();
        jf.setSize (350, 300);
        BorderLayout borderLayout = new BorderLayout();
        jf.setLayout (borderLayout);
        GridLayout gridLayout = new GridLayout(4,2);
        JPanel panel = new JPanel();
        panel.setLayout(gridLayout);
        JLabel labelId = new JLabel ("ID");
        JLabel fieldId = new JLabel(id);
        JLabel labelType = new JLabel ("Type)");
        JLabel fieldType = new JLabel(type);
        JLabel labelSize = new JLabel ("Enter Size");
        JTextField fieldSize = new JTextField (10);
        JLabel labelColour = new JLabel ("Enter Colour");
        JTextField fieldColour = new JTextField (10);
        panel.add(labelId);
        panel.add(fieldId);
        panel.add(labelType);
        panel.add(fieldType);
        panel.add(labelSize);
        panel.add(fieldSize);
        panel.add(labelColour);
        panel.add(fieldColour);
        JButton jb = new JButton ("Submit");
        jf.add(panel, BorderLayout.CENTER);
        jf.add (jb, BorderLayout.SOUTH);
        jf.setVisible (true);
        jb.addActionListener (e -> {
            String[] newSize = {fieldSize.getText()};
            String[] newColour = {fieldColour.getText()};
            if(newSize[0].isEmpty() && newColour[0].isEmpty()){
                JOptionPane.showMessageDialog(null,"Everything seems to be up to date", "Update Warning", JOptionPane.WARNING_MESSAGE);
            }
            else if(newColour[0].isEmpty()){
                newColour[0] = colour;
            }
            else if(newSize[0].isEmpty()){
                newSize[0] = size;
            }
            try {
                String body = "{\"id\":\"" + id + "\", \"type\":\"" + type + "\", \"size\":\"" + newSize[0] + "\", \"colour\":\"" + newColour[0] + "\"}";
                updateGarment(body, id);
            } catch (IOException ex) {ex.printStackTrace();}
            updateTable(table);
        });
    }

    private void updateGarment(String body, String id) throws IOException {
        String url = "http://localhost:8080/garment/" + id + "/";
        String requestMethod = "PUT";
        HttpURLConnection connection = getHttpPOSTURLConnection(url, requestMethod);
        try(OutputStream os = connection.getOutputStream()) {
            byte[] input = body.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        httpResponse(connection);
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
            updateTable(table);
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
        String requestMethod = "POST";
        HttpURLConnection connection = getHttpPOSTURLConnection(url, requestMethod);
        try(OutputStream os = connection.getOutputStream()) {
            byte[] input = body.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        httpResponse(connection);
    }

    public String[] getTableColumns() {
        return new String[]{"ID", "Type", "Size", "Colour"};
    }

    public String[] getCupboard() throws IOException {
        HttpURLConnection connection = getHttpGETUrlConnection();
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code : " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) {
            StringBuffer response = getInputStream(connection);
            String tableContent = response.toString();
            String[] table = tableContent.split("\"id\":");
            table = extractData(table);
            httpResponse(connection);
            return table;
        }
        httpResponse(connection);
        return null;
    }

    private HttpURLConnection getHttpGETUrlConnection() throws IOException {
        String url = "http://localhost:8080/garment/";
        URL urlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        connection.setRequestMethod("GET");
        return connection;
    }

    private HttpURLConnection getHttpPOSTURLConnection(String url, String requestMethod) throws IOException {
        URL urlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        connection.setRequestMethod(requestMethod);
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        return connection;
    }

    private StringBuffer getInputStream(HttpURLConnection connection) throws IOException {
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = inputReader.readLine()) != null) {
            response.append(inputLine);
        }
        inputReader.close();
        return response;
    }

    private void httpResponse(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code : " + responseCode);
        if(responseCode == 200){
            //Do nothing, everything worked fine, but we don't need to tell the user
        }
        else if(responseCode == 201){
            JOptionPane.showMessageDialog(null,"Successfully added new Garment");
        }
        if(responseCode == 204){
            JOptionPane.showMessageDialog(null,"Successfully deleted Garment");
        }
        if(responseCode > 204){
            JOptionPane.showMessageDialog(null,"Internal error. Please try again. If this error continues please contact the administrator", "Digital Cupboard - Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String[] extractData(String[] table) {
        String[] data = new String[table.length*4];
        int l = 0;
        for (int i = 0; i < table.length; i++) {
            String[] tab = table[i].split(",\"_links\":");
            for (int j = 0; j < tab.length; j++) {
                if (j % 2 != 0) {
                    tab[j] = null;
                }
                if (j + 1 == tab.length) {
                    tab[j] = null;
                }
            }
            for (int j = 0; (j + 1) < tab.length; j++) {
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
                    data[k + l] = t[k];
                }
                l += 4;
            }
        }
        return data;
    }

    private JTable createJTable(){
        String[] columns = getTableColumns();
        if (table == null) {
            table = new JTable() {
                public boolean isCellEditable(int nRow, int nCol) {
                    return false;
                }
            };
        }
        DefaultTableModel contactTableModel = (DefaultTableModel) table.getModel();
        contactTableModel.setColumnIdentifiers(columns);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return table;
    }

    public void setUpTableData() throws IOException, InterruptedException {
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        String[] data = getCupboard();
        for(int i = 0; i < data.length; i += 4){
            tableModel.addRow(new Object[]{data[i + 0], data[i + 1], data[i + 2], data[i + 3]});
        }
        table.setModel(tableModel);
        int count = tableModel.getRowCount();
        tableModel.removeRow(count-1);
    }

    private JPanel createJButtons(JTable table) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        JButton retrieveButton = createRetrieveButton();
        JButton addButton = createAddButton();
        JButton deleteButton = createDeleteButton(table);
        JButton updateButton = createUpdateButton(table);
        buttonPanel.add(retrieveButton);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateButton);
        return buttonPanel;
    }

    private JButton createUpdateButton(JTable table) {
        JButton updateButton = new JButton("Update Item");
        updateButton.addActionListener(e -> {
            int index = table.getSelectedRow();
            if (index == -1) {
                JOptionPane.showMessageDialog(null, "Please select an entry", "Digital Cupboard - Error", JOptionPane.ERROR_MESSAGE);
            } else {
                String id = (String) table.getModel().getValueAt(index, 0);
                String type = (String) table.getModel().getValueAt(index, 1);
                String size = (String) table.getModel().getValueAt(index, 2);
                String colour = (String) table.getModel().getValueAt(index, 3);
                updateGarmentInputField(id, type, size, colour);
            }
            updateTable(table);
        });
        return updateButton;
    }

    private JButton createDeleteButton(JTable table) {
        JButton deleteButton = new JButton("Delete Item");
        deleteButton.addActionListener(e -> {
            int index = table.getSelectedRow();
            if (index == -1) {
                JOptionPane.showMessageDialog(null, "Please select an entry", "Digital Cupboard - Error", JOptionPane.ERROR_MESSAGE);
            } else {
                String id = (String) table.getModel().getValueAt(index, 0);
                try {
                    deleteGarment(id);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            updateTable(table);
        });
        return deleteButton;
    }

    private JButton createAddButton() {
        JButton addButton = new JButton("Add Item");
        addButton.addActionListener(e -> addGarmentInputField());
        return addButton;
    }

    private JButton createRetrieveButton() {
        JButton retrieveButton = new JButton("Get Cupboard");
        retrieveButton.addActionListener(e -> updateTable(table));
        return retrieveButton;
    }

    private void updateTable(JTable table) {
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        tableModel.setRowCount(0);
        tableModel.fireTableDataChanged();
        try {
            setUpTableData();
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new CupboardApplicationGUI();
    }
}