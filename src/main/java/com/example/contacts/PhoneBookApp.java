package com.example.contacts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneBookApp {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/mysql?useUnicode=true&characterEncoding=UTF-8";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(
                JDBC_URL,
                AppConfig.getUsername(),
                AppConfig.getPassword())) {

            DatabaseManager databaseManager = new DatabaseManager(connection);
            databaseManager.createTableIfNotExists();

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("1. Добавить контакт");
                System.out.println("2. Редактировать контакт");
                System.out.println("3. Удалить контакт");
                System.out.println("4. Просмотреть все контакты");
                System.out.println("5. Поиск контактов");
                System.out.println("6. Выйти");
                System.out.print("Выберите опцию: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // consume the newline

                switch (choice) {
                    case 1:
                        addContact(databaseManager, scanner);
                        break;
                    case 2:
                        editContact(databaseManager, scanner);
                        break;
                    case 3:
                        deleteContact(databaseManager, scanner);
                        break;
                    case 4:
                        viewContacts(databaseManager);
                        break;
                    case 5:
                        searchContacts(databaseManager, scanner);
                        break;
                    case 6:
                        System.out.println("Выход из программы.");
                        System.exit(0);
                    default:
                        System.out.println("Неверный выбор. Пожалуйста, попробуйте снова.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addContact(DatabaseManager databaseManager, Scanner scanner) throws SQLException {
        System.out.print("Введите имя: ");
        String name = scanner.nextLine();
        while (name.trim().isEmpty()) {
            System.out.println("Имя не может быть пустым. Пожалуйста, введите имя заново: ");
            name = scanner.nextLine();
        }

        System.out.print("Введите номер телефона: ");

        Pattern regex = Pattern.compile("^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$");

        String phoneNumber = scanner.nextLine();
        Matcher regexMatcher = regex.matcher(phoneNumber);
        while (!regexMatcher.matches()) {
            System.out.print("Неправильный формат номера телефона. Введите снова: ");
            phoneNumber = scanner.nextLine();
        }

        databaseManager.addContact(name, phoneNumber);
    }

    private static void viewContacts(DatabaseManager databaseManager) throws SQLException {
        List<Contact> contacts = databaseManager.getAllContacts();
        System.out.println("Контакты:");
        for (Contact contact : contacts) {
            System.out.println(contact);
        }
    }

    private static void editContact(DatabaseManager databaseManager, Scanner scanner) throws SQLException {
        viewContacts(databaseManager);

        System.out.print("Введите ID контакта, который вы хотите отредактировать: ");
        int contactId = scanner.nextInt();
        scanner.nextLine(); // consume the newline

        System.out.print("Введите новое имя: ");
        String newName = scanner.nextLine();
        System.out.print("Введите новый номер телефона: ");
        String newPhoneNumber = scanner.nextLine();

        databaseManager.editContact(contactId, newName, newPhoneNumber);
    }

    private static void deleteContact(DatabaseManager databaseManager, Scanner scanner) throws SQLException {
        viewContacts(databaseManager);

        System.out.print("Введите ID контакта, который вы хотите удалить: ");
        int contactId = scanner.nextInt();
        scanner.nextLine(); // consume the newline

        // Предложение подтверждения удаления
        System.out.print("Вы уверены, что хотите удалить контакт? (y/n): ");
        String confirmation = scanner.nextLine().toLowerCase();

        if (confirmation.equals("y")) {
            databaseManager.deleteContact(contactId);
        } else {
            System.out.println("Удаление отменено.");
        }
    }

    private static void searchContacts(DatabaseManager databaseManager, Scanner scanner) throws SQLException {
        System.out.println("Выберите опцию поиска:");
        System.out.println("1. Поиск по имени");
        System.out.println("2. Поиск по номеру телефона");
        System.out.print("Ваш выбор: ");

        int searchOption = scanner.nextInt();
        scanner.nextLine(); // consume the newline

        switch (searchOption) {
            case 1:
                System.out.print("Введите имя для поиска: ");
                String nameQuery = scanner.nextLine();
                List<Contact> contactsByName = databaseManager.searchContactsByName(nameQuery);
                printSearchResults(contactsByName);
                break;
            case 2:
                System.out.print("Введите номер телефона для поиска: ");
                String phoneNumberQuery = scanner.nextLine();
                List<Contact> contactsByPhoneNumber = databaseManager.searchContactsByPhoneNumber(phoneNumberQuery);
                printSearchResults(contactsByPhoneNumber);
                break;
            default:
                System.out.println("Неверный выбор. Поиск отменен.");
        }
    }

    private static void printSearchResults(List<Contact> contacts) {
        if (!contacts.isEmpty()) {
            System.out.println("Результаты поиска:");
            for (Contact contact : contacts) {
                System.out.println(contact);
            }
        } else {
            System.out.println("По вашему запросу ничего не найдено.");
        }
    }
}