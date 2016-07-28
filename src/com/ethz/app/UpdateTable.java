package com.ethz.app;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

public class UpdateTable {

    public static void main(String[] args) {
        new UpdateTable();
    }

    public UpdateTable() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }

                UpdatableTableModel model = new UpdatableTableModel();

                JTable table = new JTable();
                table.setModel(model);

                table.getColumn("Progress").setCellRenderer(new ProgressCellRender());

                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.add(new JScrollPane(table));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                FileFinderWorker worker = new FileFinderWorker(model);
                worker.execute();

            }
        });
    }
}

     class ProgressCellRender extends JProgressBar implements TableCellRenderer {

        /**
		 * 
		 */
		private static final long serialVersionUID = 252326950500858559L;

		@Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            int progress = 0;
            if (value instanceof Float) {
                progress = Math.round(((Float) value) * 100f);
            } else if (value instanceof Integer) {
                progress = (int) value;
            }
            setValue(progress);
            return this;
        }
    }

     class RowData {

        private File file;
        private String type;
        private long length;
        private float status;

        public RowData(File file, String type) {
            this.file = file;
            this.type = type;
            this.length = file.length();
            this.status = 0f;
        }

        public File getFile() {
            return file;
        }

        public long getLength() {
            return length;
        }

        public float getStatus() {
            return status;
        }

        public String getType() {
            return type;
        }

        public void setStatus(float status) {
            this.status = status;
        }
    }

     class UpdatableTableModel extends AbstractTableModel {

        private List<RowData> rows;
        private Map<File, RowData> mapLookup;

        public UpdatableTableModel() {
            rows = new ArrayList<>(25);
            mapLookup = new HashMap<>(25);
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public String getColumnName(int column) {
            String name = "??";
            switch (column) {
                case 0:
                    name = "source";
                    break;
                case 1:
                    name = "Url";
                    break;
                case 2:
                    name = "View Data";
                    break;
                case 3:
                    name = "Progress";
                    break;
            }
            return name;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            RowData rowData = rows.get(rowIndex);
            Object value = null;
            switch (columnIndex) {
                case 0:
                    value = rowData.getFile();
                    break;
                case 1:
                    value = rowData.getType();
                    break;
                case 2:
                    value = rowData.getLength();
                    break;
                case 3:
                    value = rowData.getStatus();
                    break;
            }
            return value;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            RowData rowData = rows.get(rowIndex);
            switch (columnIndex) {
                case 3:
                    if (aValue instanceof Float) {
                        rowData.setStatus((float) aValue);
                    }
                    break;
            }
        }

        public void addFile(File file) {
            RowData rowData = new RowData(file, "A File");
            mapLookup.put(file, rowData);
            rows.add(rowData);
            fireTableRowsInserted(rows.size() - 1, rows.size() - 1);
        }

        protected void updateStatus(File file, int progress) {
            RowData rowData = mapLookup.get(file);
            if (rowData != null) {
                int row = rows.indexOf(rowData);
                float p = (float) progress / 100f;
                setValueAt(p, row, 3);
                fireTableCellUpdated(row, 3);
            }
        }
    }

     class FileFinderWorker extends SwingWorker<List<File>, File> {

        private UpdatableTableModel model;

        public FileFinderWorker(UpdatableTableModel model) {
            this.model = model;
        }

        @Override
        protected void process(List<File> chunks) {
            for (File file : chunks) {
                model.addFile(file);
            }
        }

        @Override
        protected List<File> doInBackground() throws Exception {
            File files[] = new File(System.getProperty("user.dir")).listFiles();
            List<File> lstFiles = new ArrayList<>(Arrays.asList(files));
            for (File file : lstFiles) {
                // You could actually publish the entire array, but I'm doing this
                // deliberatly ;)
                publish(file);
            }
            return lstFiles;
        }

        @Override
        protected void done() {
            try {
                List<File> files = get();
                for (File file : files) {
                    new FileReaderWorker(model, file).execute();
                }
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        }
    }

     class FileReaderWorker extends SwingWorker<File, File> {

        private File currentFile;
        private UpdatableTableModel model;

        public FileReaderWorker(UpdatableTableModel model, File file) {
            this.currentFile = file;
            this.model = model;

            addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals("progress")) {
                        FileReaderWorker.this.model.updateStatus(currentFile, (int) evt.getNewValue());
                    }
                }
            });

        }

        @Override
        protected File doInBackground() throws Exception {
            if (currentFile.isFile()) {
                setProgress(0);
                long fileLength = currentFile.length();
                BufferedReader reader = null;
                char[] cbuf = new char[1024];
                try {
                    reader = new BufferedReader(new FileReader(currentFile));
                    int bytesRead = -1;
                    int totalBytesRead = 0;
                    while ((bytesRead = reader.read(cbuf)) != -1) {
                        totalBytesRead += bytesRead;
                        int progress = (int) Math.round(((double) totalBytesRead / (double) fileLength) * 100d);
                        setProgress(progress);
                        Thread.sleep(25);
                    }
                    setProgress(100);
                } catch (Exception e) {
                    e.printStackTrace();
                    setProgress(100);
                } finally {
                    try {
                        reader.close();
                    } catch (Exception e) {
                    }
                }
            } else {
                setProgress(100);
            }
            return currentFile;
        }
    }
