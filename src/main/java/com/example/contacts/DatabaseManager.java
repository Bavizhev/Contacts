package com.example.contacts;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DatabaseManager {
    private Connection connection;

    public DatabaseManager(Connection connection) {
        this.connection = connection;
    }

    public void createTableIfNotExists() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String createTableQuery = "CREATE TABLE IF NOT EXISTS contacts (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL," +
                    "phone_number VARCHAR(20) NOT NULL)";
            statement.executeUpdate(createTableQuery);
        }
    }

    public void addContact(String name, String phoneNumber) throws SQLException {
        String insertQuery = "INSERT INTO contacts (name, phone_number) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, phoneNumber);
            preparedStatement.executeUpdate();
            System.out.println("Контакт успешно добавлен.");
        }
    }

    public List<Contact> getAllContacts() throws SQLException {
        List<Contact> contacts = new ArrayList<>();
        String selectQuery = "SELECT * FROM contacts";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectQuery)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String phoneNumber = resultSet.getString("phone_number");
                contacts.add(new Contact(id, name, phoneNumber));
            }
        }
        return contacts;
    }

    public void editContact(int contactId, String newName, String newPhoneNumber) throws SQLException {
        String updateQuery = "UPDATE contacts SET name = ?, phone_number = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, newPhoneNumber);
            preparedStatement.setInt(3, contactId);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Контакт успешно обновлен.");
            } else {
                System.out.println("Контакт с ID " + contactId + " не найден.");
            }
        }
    }

    public void deleteContact(int contactId) throws SQLException {
        String deleteQuery = "DELETE FROM contacts WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setInt(1, contactId);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Контакт успешно удален.");
            } else {
                System.out.println("Контакт с ID " + contactId + " не найден.");
            }
        }
    }

    public List<Contact> searchContactsByName(String nameQuery) throws SQLException {
        List<Contact> contacts = new ArrayList<>();
        String searchQuery = "SELECT * FROM contacts WHERE name LIKE ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(searchQuery)) {
            preparedStatement.setString(1, "%" + nameQuery + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String phoneNumber = resultSet.getString("phone_number");
                contacts.add(new Contact(id, name, phoneNumber));
            }
        }
        return contacts;
    }

    public List<Contact> searchContactsByPhoneNumber(String phoneNumberQuery) throws SQLException {
        List<Contact> contacts = new ArrayList<>();
        String searchQuery = "SELECT * FROM contacts WHERE phone_number LIKE ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(searchQuery)) {
            preparedStatement.setString(1, "%" + phoneNumberQuery + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String phoneNumber = resultSet.getString("phone_number");
                contacts.add(new Contact(id, name, phoneNumber));
            }
        }
        return contacts;
    }
}
