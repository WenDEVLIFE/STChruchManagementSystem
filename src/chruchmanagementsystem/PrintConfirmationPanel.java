package chruchmanagementsystem;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

public class PrintConfirmationPanel extends JFrame {

    private JPanel panelToPrint;

    public PrintConfirmationPanel(Map<String, Object> reservation) {
        setTitle("Church Reservation Confirmation");
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        panelToPrint = new JPanel();
        panelToPrint.setBackground(Color.LIGHT_GRAY);
        panelToPrint.setLayout(new BoxLayout(panelToPrint, BoxLayout.Y_AXIS));
        panelToPrint.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelToPrint.add(new JLabel("--------------------------------------------------", SwingConstants.CENTER));
        panelToPrint.add(centerLabel("Church Reservation Confirmation"));
        panelToPrint.add(new JLabel("--------------------------------------------------"));

        panelToPrint.add(new JLabel("Reservation ID : " + reservation.get("reservationID")));
        panelToPrint.add(new JLabel("Event Type     : " + reservation.get("event")));
        panelToPrint.add(new JLabel("Name           : " + reservation.get("name")));
        panelToPrint.add(new JLabel("Date & Time    : " + reservation.get("date") + " " + reservation.get("time")));
        panelToPrint.add(new JLabel("Status         : " + reservation.get("status")));

        panelToPrint.add(Box.createVerticalStrut(10));
        panelToPrint.add(new JLabel("For inquiries, contact: 0912-345-6789"));
        panelToPrint.add(new JLabel("--------------------------------------------------"));

        JButton saveBtn = new JButton("Save to File");
        saveBtn.addActionListener(e -> saveToFile(reservation));

        add(panelToPrint, BorderLayout.CENTER);
        add(saveBtn, BorderLayout.SOUTH);
        setVisible(true);
    }

    private JLabel centerLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        return label;
    }

    private void saveToFile(Map<String, Object> reservation) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Confirmation as PDF");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().endsWith(".pdf")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
            }

            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
                document.open();

                // Add content to the PDF
                document.add(new Paragraph("--------------------------------------------------"));
                document.add(new Paragraph("Church Reservation Confirmation", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
                document.add(new Paragraph("--------------------------------------------------"));
                document.add(new Paragraph("Reservation ID : " + reservation.get("reservationID")));
                document.add(new Paragraph("Event Type     : " + reservation.get("event")));
                document.add(new Paragraph("Name           : " + reservation.get("name")));
                document.add(new Paragraph("Date & Time    : " + reservation.get("date") + " " + reservation.get("time")));
                document.add(new Paragraph("Status         : " + reservation.get("status")));
                document.add(new Paragraph("\nFor inquiries, contact: 0912-345-6789"));
                document.add(new Paragraph("--------------------------------------------------"));

                document.close();
                JOptionPane.showMessageDialog(this, "PDF saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}