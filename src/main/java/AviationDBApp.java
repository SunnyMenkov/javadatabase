import java.sql.*;

public class AviationDBApp {
    private Connection connection;

    // Конструктор для подключения к базе данных
    public AviationDBApp(String url, String user, String password) throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver"); // Регистрация драйвера PostgreSQL
        connection = DriverManager.getConnection(url, user, password);
        System.out.println("Connected to the database!");
    }

    // Метод для создания базы данных
    public void createDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE DATABASE AviationDB");
            System.out.println("Database created successfully!");
        }
    }

    // Метод для удаления базы данных
    public void dropDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP DATABASE IF EXISTS AviationDB");
            System.out.println("Database dropped successfully!");
        }
    }

    // Метод для создания таблиц
    public void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Создание таблицы ПИЛОТ
            stmt.execute("CREATE TABLE IF NOT EXISTS ПИЛОТ (" +
                    "ИДЕНТИФИКАТОР SERIAL PRIMARY KEY, " +
                    "ФАМИЛИЯ TEXT NOT NULL, " +
                    "АВИАОТРЯД TEXT NOT NULL, " +
                    "ЛИМИТ_ЧАСОВ INT NOT NULL)");

            // Создание таблицы ШТУРМАН
            stmt.execute("CREATE TABLE IF NOT EXISTS ШТУРМАН (" +
                    "ИДЕНТИФИКАТОР SERIAL PRIMARY KEY, " +
                    "ФАМИЛИЯ TEXT NOT NULL, " +
                    "АВИАОТРЯД TEXT NOT NULL, " +
                    "ЛИМИТ_ЧАСОВ INT NOT NULL)");

            // Создание таблицы РЕЙС
            stmt.execute("CREATE TABLE IF NOT EXISTS РЕЙС (" +
                    "ИДЕНТИФИКАТОР SERIAL PRIMARY KEY, " +
                    "ПУНКТ_НАЗНАЧЕНИЯ TEXT NOT NULL, " +
                    "ОБСЛ_ПЕРСОНАЛ TEXT NOT NULL, " +
                    "ВРЕМЯ_ПОЛЕТА INT NOT NULL, " +
                    "КОЭФФИЦИЭНТ_СЛОЖНОСТИ INT NOT NULL)");

            // Создание таблицы ПОЛЕТ
            stmt.execute("CREATE TABLE IF NOT EXISTS ПОЛЕТ (" +
                    "НОМЕР_ЗАПИСИ SERIAL PRIMARY KEY, " +
                    "ДАТА TEXT NOT NULL, " +
                    "ПИЛОТ INT REFERENCES ПИЛОТ(ИДЕНТИФИКАТОР), " +
                    "ШТУРМАН INT REFERENCES ШТУРМАН(ИДЕНТИФИКАТОР), " +
                    "РЕЙС INT REFERENCES РЕЙС(ИДЕНТИФИКАТОР), " +
                    "ЧИСЛО_ВЫЛЕТОВ INT NOT NULL, " +
                    "ЧИСЛО_ЧАСОВ INT NOT NULL)");

            System.out.println("Tables created successfully!");
        }
    }

    // Метод для очистки таблицы
    public void clearTable(String tableName) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM " + tableName);
            System.out.println("Table " + tableName + " cleared successfully!");
        }
    }

    // Метод для добавления нового пилота
    public void addPilot(int id, String surname, String squad, int limitHours) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO ПИЛОТ (ИДЕНТИФИКАТОР, ФАМИЛИЯ, АВИАОТРЯД, ЛИМИТ_ЧАСОВ) VALUES (?, ?, ?, ?)")) {
            stmt.setInt(1, id);
            stmt.setString(2, surname);
            stmt.setString(3, squad);
            stmt.setInt(4, limitHours);
            stmt.executeUpdate();
            System.out.println("Pilot added successfully!");
        }
    }

    // Метод для поиска пилота по фамилии
    public ResultSet findPilotBySurname(String surname) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM ПИЛОТ WHERE ФАМИЛИЯ = ?");
        stmt.setString(1, surname);
        return stmt.executeQuery();
    }

    // Метод для обновления данных пилота
    public void updatePilot(int id, String surname, String squad, int limitHours) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "UPDATE ПИЛОТ SET ФАМИЛИЯ = ?, АВИАОТРЯД = ?, ЛИМИТ_ЧАСОВ = ? WHERE ИДЕНТИФИКАТОР = ?")) {
            stmt.setString(1, surname);
            stmt.setString(2, squad);
            stmt.setInt(3, limitHours);
            stmt.setInt(4, id);
            stmt.executeUpdate();
            System.out.println("Pilot updated successfully!");
        }
    }

    // Метод для закрытия соединения с базой данных
    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
            System.out.println("Connection closed.");
        }
    }

    // Метод для удаления пилота по фамилии
    public void deletePilotBySurname(String surname) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM ПИЛОТ WHERE ФАМИЛИЯ = ?")) {
            stmt.setString(1, surname);
            stmt.executeUpdate();
            System.out.println("Pilot deleted successfully!");
        }
    }
    public ResultSet viewTableData(String tableName) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeQuery("SELECT * FROM " + tableName);
    }



    public void createUser(String username, String password, String role) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Создание пользователя
            stmt.execute("CREATE USER " + username + " WITH PASSWORD '" + password + "'");

            // Назначение прав в зависимости от роли
            switch (role) {
                case "admin":
                    stmt.execute("GRANT ALL PRIVILEGES ON DATABASE AviationDB TO " + username);
                    stmt.execute("GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO " + username);
                    stmt.execute("GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO " + username);
                    break;
                case "pilot":
                    stmt.execute("GRANT CONNECT ON DATABASE AviationDB TO " + username);
                    stmt.execute("GRANT SELECT, INSERT, UPDATE ON TABLE ПИЛОТ TO " + username);
                    stmt.execute("GRANT SELECT ON TABLE ПОЛЕТ TO " + username);
                    break;
                case "guest":
                    stmt.execute("GRANT CONNECT ON DATABASE AviationDB TO " + username);
                    stmt.execute("GRANT SELECT ON ALL TABLES IN SCHEMA public TO " + username);
                    break;
                default:
                    throw new SQLException("Invalid role: " + role);
            }
        }
    }

    // Пример использования
    public static void main(String[] args) {
        try {
            // Укажите правильные параметры подключения
            String url = "jdbc:postgresql://localhost:5432/postgres";
            String user = "your_username"; // Например, "postgres"
            String password = "your_password"; // Ваш пароль

            AviationDBApp app = new AviationDBApp(url, user, password);

            // Создание базы данных
            app.createDatabase();

            // Подключение к новой базе данных
            url = "jdbc:postgresql://localhost:5432/AviationDB";
            app = new AviationDBApp(url, user, password);

            // Создание таблиц
            app.createTables();

            // Пример использования методов
            app.addPilot(1, "Ляпин", "Нижегородский", 50);
            ResultSet rs = app.findPilotBySurname("Ляпин");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("ИДЕНТИФИКАТОР") +
                        ", Фамилия: " + rs.getString("ФАМИЛИЯ") +
                        ", Авиаотряд: " + rs.getString("АВИАОТРЯД") +
                        ", Лимит часов: " + rs.getInt("ЛИМИТ_ЧАСОВ"));
            }
            app.updatePilot(1, "Ляпин", "Московский", 55);
            app.deletePilotBySurname("Ляпин");
            app.clearTable("ПИЛОТ");

            // Закрытие соединения
            app.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}