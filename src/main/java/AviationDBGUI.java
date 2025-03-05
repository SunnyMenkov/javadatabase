import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AviationDBGUI extends JFrame {
    private AviationDBApp app;
    private String userRole; // Роль пользователя (admin, pilot, guest)

    public AviationDBGUI(AviationDBApp app, String userRole) {
        this.app = app;
        this.userRole = userRole;
        setTitle("Aviation DB Management - " + userRole);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(10, 2)); // Добавим строку для новой кнопки

        // Создаем кнопки
        JButton createDBButton = new JButton("Create Database");
        JButton dropDBButton = new JButton("Drop Database");
        JButton createTablesButton = new JButton("Create Tables");
        JButton clearTableButton = new JButton("Clear Table");
        JButton addPilotButton = new JButton("Add Pilot");
        JButton findPilotButton = new JButton("Find Pilot");
        JButton updatePilotButton = new JButton("Update Pilot");
        JButton deletePilotButton = new JButton("Delete Pilot");
        JButton viewDataButton = new JButton("View Data");
        JButton createUserButton = new JButton("Create User"); // Новая кнопка

        // Добавляем кнопки на панель
        panel.add(createDBButton);
        panel.add(dropDBButton);
        panel.add(createTablesButton);
        panel.add(clearTableButton);
        panel.add(addPilotButton);
        panel.add(findPilotButton);
        panel.add(updatePilotButton);
        panel.add(deletePilotButton);
        panel.add(viewDataButton);
        panel.add(createUserButton);

        // Ограничиваем функционал в зависимости от роли
        if (!userRole.equals("admin")) {
            createDBButton.setEnabled(false);
            dropDBButton.setEnabled(false);
            createTablesButton.setEnabled(false);
            clearTableButton.setEnabled(false);
            updatePilotButton.setEnabled(false);
            deletePilotButton.setEnabled(false);
            createUserButton.setEnabled(false); // Только для admin
        }
        if (userRole.equals("guest")) {
            addPilotButton.setEnabled(false);
        }

        // Добавляем панель на форму
        add(panel, BorderLayout.CENTER);

        // Обработчики для кнопок
        createDBButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    app.createDatabase();
                    JOptionPane.showMessageDialog(null, "Database created successfully!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        dropDBButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    app.dropDatabase();
                    JOptionPane.showMessageDialog(null, "Database dropped successfully!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        createTablesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    app.createTables();
                    JOptionPane.showMessageDialog(null, "Tables created successfully!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        clearTableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tableName = JOptionPane.showInputDialog("Enter table name to clear:");
                if (tableName != null && !tableName.isEmpty()) {
                    try {
                        app.clearTable(tableName);
                        JOptionPane.showMessageDialog(null, "Table cleared successfully!");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        addPilotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = JOptionPane.showInputDialog("Enter pilot ID:");
                String surname = JOptionPane.showInputDialog("Enter pilot surname:");
                String squad = JOptionPane.showInputDialog("Enter pilot squad:");
                String limitHours = JOptionPane.showInputDialog("Enter pilot limit hours:");

                if (id != null && surname != null && squad != null && limitHours != null) {
                    try {
                        app.addPilot(Integer.parseInt(id), surname, squad, Integer.parseInt(limitHours));
                        JOptionPane.showMessageDialog(null, "Pilot added successfully!");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        findPilotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String surname = JOptionPane.showInputDialog("Enter pilot surname to find:");
                if (surname != null && !surname.isEmpty()) {
                    try {
                        ResultSet resultSet = app.findPilotBySurname(surname);
                        StringBuilder result = new StringBuilder();
                        while (resultSet.next()) {
                            result.append("ID: ").append(resultSet.getInt("ИДЕНТИФИКАТОР"))
                                    .append(", Surname: ").append(resultSet.getString("ФАМИЛИЯ"))
                                    .append(", Squad: ").append(resultSet.getString("АВИАОТРЯД"))
                                    .append(", Limit Hours: ").append(resultSet.getInt("ЛИМИТ_ЧАСОВ"))
                                    .append("\n");
                        }
                        JOptionPane.showMessageDialog(null, result.toString(), "Search Results", JOptionPane.INFORMATION_MESSAGE);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        updatePilotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = JOptionPane.showInputDialog("Enter pilot ID to update:");
                String surname = JOptionPane.showInputDialog("Enter new surname:");
                String squad = JOptionPane.showInputDialog("Enter new squad:");
                String limitHours = JOptionPane.showInputDialog("Enter new limit hours:");

                if (id != null && surname != null && squad != null && limitHours != null) {
                    try {
                        app.updatePilot(Integer.parseInt(id), surname, squad, Integer.parseInt(limitHours));
                        JOptionPane.showMessageDialog(null, "Pilot updated successfully!");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        deletePilotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String surname = JOptionPane.showInputDialog("Enter pilot surname to delete:");
                if (surname != null && !surname.isEmpty()) {
                    try {
                        app.deletePilotBySurname(surname);
                        JOptionPane.showMessageDialog(null, "Pilot deleted successfully!");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        viewDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tableName = JOptionPane.showInputDialog("Enter table name to view (ПИЛОТ, ШТУРМАН, РЕЙС, ПОЛЕТ):");
                if (tableName != null && !tableName.isEmpty()) {
                    try {
                        ResultSet resultSet = app.viewTableData(tableName);
                        StringBuilder result = new StringBuilder();
                        ResultSetMetaData metaData = resultSet.getMetaData();
                        int columnCount = metaData.getColumnCount();

                        // Добавляем заголовки столбцов
                        for (int i = 1; i <= columnCount; i++) {
                            result.append(metaData.getColumnName(i)).append("\t");
                        }
                        result.append("\n");

                        // Добавляем данные
                        while (resultSet.next()) {
                            for (int i = 1; i <= columnCount; i++) {
                                result.append(resultSet.getString(i)).append("\t");
                            }
                            result.append("\n");
                        }

                        JOptionPane.showMessageDialog(null, result.toString(), "Table Data: " + tableName, JOptionPane.INFORMATION_MESSAGE);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Обработчик для кнопки "Create User"
        createUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Диалоговое окно для ввода данных нового пользователя
                JTextField usernameField = new JTextField();
                JPasswordField passwordField = new JPasswordField();
                JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"admin", "pilot", "guest"});

                Object[] fields = {
                        "Username:", usernameField,
                        "Password:", passwordField,
                        "Role:", roleComboBox
                };

                int result = JOptionPane.showConfirmDialog(null, fields, "Create User", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    String username = usernameField.getText();
                    String password = new String(passwordField.getPassword());
                    String role = (String) roleComboBox.getSelectedItem();

                    try {
                        app.createUser(username, password, role);
                        JOptionPane.showMessageDialog(null, "User created successfully!");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Делаем окно видимым
        setVisible(true);
    }

    public static void main(String[] args) {
        // Окно авторизации
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        Object[] fields = {
                "Username:", usernameField,
                "Password:", passwordField
        };
        int result = JOptionPane.showConfirmDialog(null, fields, "Login", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            try {
                String url = "jdbc:postgresql://localhost:5432/AviationDB";
                Connection testConnection = DriverManager.getConnection(url, username, password);
                testConnection.close();

                AviationDBApp app = new AviationDBApp(url, username, password);
                String userRole = "guest";
                if (username.equals("admin")) {
                    userRole = "admin";
                } else if (username.equals("pilot")) {
                    userRole = "pilot";
                }

                new AviationDBGUI(app, userRole);
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Login failed: Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}