import javax.swing.table.AbstractTableModel;

public class CupboardTableModel extends AbstractTableModel {
    private String[] columnData = null;
    private Object[][] data = null;

    @Override
    public int getRowCount() {
        return 0;
    }

    @Override
    public int getColumnCount() {
        return 0;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return null;
    }
}
