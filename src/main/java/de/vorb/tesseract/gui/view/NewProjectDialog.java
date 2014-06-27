package de.vorb.tesseract.gui.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import de.vorb.tesseract.gui.event.LocaleChangeListener;
import de.vorb.tesseract.gui.event.ProjectChangeListener;
import de.vorb.tesseract.gui.util.Resources;
import de.vorb.tesseract.gui.view.i18n.Labels;

import java.awt.Toolkit;

public class NewProjectDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final JPanel contentPanel = new JPanel();
    private final JTextField tfScanDir;

    private final List<ProjectChangeListener> listeners = new LinkedList<ProjectChangeListener>();

    private final JButton btCancel;
    private final JButton btOK;
    private final JLabel lblScanDirectory;

    /**
     * Create the dialog.
     * 
     * @param owner
     */
    public NewProjectDialog(final Window owner) {
        super(owner);
        setTitle(Labels.getLabel(getLocale(), "new_project_dialog_title"));
        setIconImage(Toolkit.getDefaultToolkit().getImage(
                NewProjectDialog.class.getResource("/logos/logo_16.png")));

        setMinimumSize(new Dimension(500, 130));

        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[] { 0, 0, 0, 0 };
        gbl_contentPanel.rowHeights = new int[] { 0, 0 };
        gbl_contentPanel.columnWeights = new double[] { 0.0, 1.0, 0.0,
                Double.MIN_VALUE };
        gbl_contentPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        contentPanel.setLayout(gbl_contentPanel);
        {
            lblScanDirectory = new JLabel(Labels.getLabel(getLocale(),
                    "scan_dir"));
            GridBagConstraints gbc_lblScans = new GridBagConstraints();
            gbc_lblScans.insets = new Insets(0, 0, 0, 5);
            gbc_lblScans.anchor = GridBagConstraints.EAST;
            gbc_lblScans.gridx = 0;
            gbc_lblScans.gridy = 0;
            contentPanel.add(lblScanDirectory, gbc_lblScans);
        }
        {
            tfScanDir = new JTextField();

            // TODO remove
            tfScanDir.setText("E:\\Masterarbeit\\Ressourcen\\DE-20__32_AM_49000_L869_G927-1\\sauvola");

            GridBagConstraints gbc_tfScanDir = new GridBagConstraints();
            gbc_tfScanDir.insets = new Insets(0, 0, 0, 5);
            gbc_tfScanDir.fill = GridBagConstraints.HORIZONTAL;
            gbc_tfScanDir.gridx = 1;
            gbc_tfScanDir.gridy = 0;
            contentPanel.add(tfScanDir, gbc_tfScanDir);
            tfScanDir.setColumns(10);
        }
        {
            JButton button = new JButton("...");
            GridBagConstraints gbc_button = new GridBagConstraints();
            gbc_button.gridx = 2;
            gbc_button.gridy = 0;
            contentPanel.add(button, gbc_button);

            makePathChooser(tfScanDir, button);
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                btOK = new JButton();
                btOK.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        projectChanged();
                        NewProjectDialog.this.dispose();
                    }
                });
                btOK.setActionCommand("OK");
                buttonPane.add(btOK);
                getRootPane().setDefaultButton(btOK);
            }
            {
                btCancel = new JButton();
                btCancel.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        NewProjectDialog.this.dispose();
                    }
                });
                btCancel.setActionCommand("Cancel");
                buttonPane.add(btCancel);
            }
        }
        btCancel.setText(Labels.getLabel(getLocale(), "btn_cancel"));
        btOK.setText(Labels.getLabel(getLocale(), "btn_ok"));

        this.setResizable(false);
    }

    private void makePathChooser(final JTextField tfPath,
            final JButton btnChoosePath) {

        File dir = new File("E:\\Masterarbeit\\Ressourcen");
        if (!dir.isDirectory())
            dir = null;
        final File startDir = dir;

        btnChoosePath.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final JFileChooser dirChooser = new JFileChooser(startDir);
                dirChooser.setMultiSelectionEnabled(false);
                dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int state = dirChooser.showOpenDialog(NewProjectDialog.this);
                if (state == JFileChooser.APPROVE_OPTION) {
                    final File selection = dirChooser.getSelectedFile();
                    tfPath.setText(selection.getAbsolutePath());
                } else if (state == JFileChooser.ERROR_OPTION) {
                    JOptionPane.showMessageDialog(dirChooser,
                            "Please select a directory", "Invalid selection",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public void addProjectChangeListener(ProjectChangeListener listener) {
        listeners.add(listener);
    }

    public void removeProjectChangeListener(ProjectChangeListener listener) {
        listeners.remove(listener);
    }

    private void projectChanged() {
        final Path scanDir = Paths.get(tfScanDir.getText());

        for (ProjectChangeListener l : listeners) {
            l.projectChanged(scanDir);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        setLocationRelativeTo(getParent());
        super.setVisible(visible);
    }
}