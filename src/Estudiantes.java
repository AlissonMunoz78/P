import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Estudiantes extends JFrame {
    private JLabel resultadoLabel;
    private JButton consultarButton;
    private JTextField cedulaField;
    private String url = "jdbc:mysql://localhost:3306/estudiantes2024a";
    private String user = "root";
    private String password = "123456";

    public Estudiantes() {
        super("Consulta de Estudiantes");

        resultadoLabel = new JLabel("Resultado:");
        consultarButton = new JButton("Consultar");
        cedulaField = new JTextField(20);

        consultarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                consultarEstudiante();
            }
        });

        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.add(new JLabel("Ingrese la cédula del estudiante:"));
        panel.add(cedulaField);
        panel.add(consultarButton);
        panel.add(resultadoLabel);

        getContentPane().add(panel, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 150);
        setLocationRelativeTo(null);
    }

    public void mostrarInterfaz() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setVisible(true);
            }
        });
    }

    private void consultarEstudiante() {
        String cedulaBuscada = cedulaField.getText().trim();

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String query = "SELECT nombre, b1, b2 FROM estudiantes WHERE cedula = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cedulaBuscada);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String nombreEstudiante = resultSet.getString("nombre");
                double notaB1 = resultSet.getDouble("b1");
                double notaB2 = resultSet.getDouble("b2");
                double sumaNotas = notaB1 + notaB2;

                String mensaje = "Nombre del estudiante: " + nombreEstudiante + "<br>";
                mensaje += "Notas sumadas (b1 + b2): " + sumaNotas + "<br>";

                if (sumaNotas > 28) {
                    mensaje += "El estudiante aprueba el semestre.";
                } else {
                    double notaNecesaria = 28 - sumaNotas;
                    mensaje += "El estudiante reprueba.<br>";
                    mensaje += "Faltan " + String.format("%.2f", notaNecesaria) + " puntos para llegar a 28 y aprobar el semestre.";
                }

                resultadoLabel.setText("<html>" + mensaje + "</html>");
            } else {
                resultadoLabel.setText("No se encontró ningún estudiante con la cédula " + cedulaBuscada);
            }

        } catch (SQLException e) {
            resultadoLabel.setText("Error al ejecutar la consulta: " + e.getMessage());
        }
    }
}
