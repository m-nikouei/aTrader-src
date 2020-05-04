package aTGUI;

import javax.swing.table.AbstractTableModel;

public class MTM extends AbstractTableModel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] columnNames;
    private Object[][] data;
    private int rNum = 0;
    private int cNum = 0;
    
    public MTM(String[] CN){
    	columnNames = CN;
    	cNum = CN.length;
    	rNum = 0;
    	data = new Object[rNum][cNum];
    }
    
    public MTM(String[] CN, Object[][] d){
    	if (CN.length == d[0].length){
    		columnNames = CN;
    		data = d;
    		rNum = d.length;
    		cNum = CN.length;
    	}else
    		throw new RuntimeException();
    
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    //public Class getColumnClass(int c) {
    //    return getValueAt(0, c).getClass();
    //}

    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if (col < 2) {
            return false;
        } else {
            return true;
        }
    }
    
    public int searchColumns(String CName){
    	int columnN = -1;
    	for (int i = 0; i < cNum; i++)
    		if (columnNames[i].equals(CName)){
    			columnN = i;
    			break;
    		}
    	return columnN;		
    }

    public int searchTable(int columnN, Object o){
    	int rowNum = -1;
    	if (columnN >= 0) 
    		for (int i = 0; i < rNum; i++)
    			if (data[i][columnN].equals(o))
    				rowNum = i;
    	return rowNum;
    }
    
    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);
    }
    
    public void addNewRow(Object[] r){
    	if (r.length == cNum){
    		Object[][] odata = data;
    		data = new Object[rNum + 1][cNum];
    		for (int i = 0; i < rNum; i++)
    			data[i] = odata[i];
    		for (int i = 0; i < cNum; i++)
    			data[rNum][i] = r[i];
    		rNum++;
    		fireTableDataChanged();
    	}else
    		throw new RuntimeException();
    }
    
    public void RemoveRow(int rowNum){
    	if (0 <= rowNum && rowNum < rNum){
    		Object[][] odata = new Object[rNum - 1][cNum];
    		int k = 0;
    		for (int i = 0; i < rNum; i++){
    			if (i != rowNum){
    				odata[k] = data[i];
    				k++;
    			}
    		}
    		rNum = rNum - 1;
    		data = odata;
    		fireTableDataChanged();
       	}else
       		throw new RuntimeException();
    }
    
    public void UpdateRow(int rowNum, Object[] r){
    	if (0 <= rowNum && rowNum < rNum){
    		data[rowNum] = r;
    		fireTableDataChanged();
    	}else
       		throw new RuntimeException();
    }
    
    public void clearTable(){
    	data = new Object[0][cNum];
    	rNum = 0;
    	fireTableDataChanged();
    }
    
    public String[][] getData(){
    	String[][] sData = new String[data.length][data[0].length];
    	for (int i = 0; i < data.length; i++)
    		for (int j = 0; j < data[0].length; j++)
    			sData[i][j] = data[i][j].toString();
    	return sData;
    }
    
    public String[] getColumn(int cNum){
    	String[] c = new String[data.length];
    	for (int i = 0; i < data.length; i++)
    		c[i] = data[i][cNum].toString();
    	return c;
    }
}
