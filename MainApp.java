/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.mainapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class MainApp {

    // Simulación de base de datos
    static ArrayList<Admin> admins = new ArrayList<>();
    static ArrayList<User> users = new ArrayList<>();
    static ArrayList<Clothing> clothingItems = new ArrayList<>();
    static ArrayList<Combination> combinations = new ArrayList<>(); // Arreglo para combinaciones

    public static void main(String[] args) {
        // Inicialización de datos
        admins.add(new Admin("admin", hashPassword("1234"), "1111"));
        users.add(new User("jose", hashPassword("5678")));
        clothingItems.add(new Clothing("Camisa", "Camisa Azul"));
        clothingItems.add(new Clothing("Pantalón", "Pantalón Negro"));

        // Mostrar pantalla de login
        new LoginScreen();
    }

    // Método para hashear contraseñas
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}

// Clases de usuarios, ropa y combinaciones
class Admin {
    String username;
    String passwordHash;
    String pin;
    int intentosFallidos = 0;
    boolean bloqueado = false;

    Admin(String username, String passwordHash, String pin) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.pin = pin;
    }
}

class User {
    String username;
    String passwordHash;
    int intentosFallidos = 0;
    boolean bloqueado = false;

    User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }
}

class Clothing {
    String type; // "Pantalón" o "Camisa"
    String name;

    Clothing(String type, String name) {
        this.type = type;
        this.name = name;
    }
}

class Combination {
    String shirt;
    String pants;

    Combination(String shirt, String pants) {
        this.shirt = shirt;
        this.pants = pants;
    }

    @Override
    public String toString() {
        return "Camisa: " + shirt + ", Pantalón: " + pants;
    }
}

// Pantalla de login
class LoginScreen extends JFrame {
    JTextField usernameField;
    JPasswordField passwordField;
    JTextField pinField;

    public LoginScreen() {
        setTitle("Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 2));

        JLabel usernameLabel = new JLabel("Usuario:");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordField = new JPasswordField();
        JLabel pinLabel = new JLabel("PIN (Solo Admin):");
        pinField = new JTextField();

        JButton loginButton = new JButton("Iniciar Sesión");
        loginButton.addActionListener(new LoginAction());

        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(pinLabel);
        add(pinField);
        add(new JLabel());
        add(loginButton);

        setVisible(true);
    }

    class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String pin = pinField.getText();

            // Validar entrada
            if (!username.matches("[a-zA-Z]+")) {
                JOptionPane.showMessageDialog(null, "El usuario debe contener solo texto.");
                return;
            }

            if (!password.matches("\\d+")) {
                JOptionPane.showMessageDialog(null, "La contraseña debe contener solo números.");
                return;
            }

            // Validar Administradores
            for (Admin admin : MainApp.admins) {
                if (admin.username.equals(username)) {
                    if (admin.bloqueado) {
                        JOptionPane.showMessageDialog(null, "El usuario está bloqueado.");
                        return;
                    }
                    if (admin.passwordHash.equals(MainApp.hashPassword(password)) &&
                            admin.pin.equals(pin)) {
                        admin.intentosFallidos = 0; // Reiniciar intentos fallidos
                        JOptionPane.showMessageDialog(null, "Bienvenido Administrador!");
                        new AdminScreen(admin);
                        dispose();
                        return;
                    } else {
                        admin.intentosFallidos++;
                        if (admin.intentosFallidos >= 3) {
                            admin.bloqueado = true;
                            JOptionPane.showMessageDialog(null, "Usuario bloqueado por intentos fallidos.");
                        } else {
                            JOptionPane.showMessageDialog(null, "Credenciales incorrectas.");
                        }
                        return;
                    }
                }
            }

            // Validar Usuarios
            for (User user : MainApp.users) {
                if (user.username.equals(username)) {
                    if (user.bloqueado) {
                        JOptionPane.showMessageDialog(null, "El usuario está bloqueado.");
                        return;
                    }
                    if (user.passwordHash.equals(MainApp.hashPassword(password))) {
                        user.intentosFallidos = 0; // Reiniciar intentos fallidos
                        JOptionPane.showMessageDialog(null, "Bienvenido Usuario!");
                        new UserScreen();
                        dispose();
                        return;
                    } else {
                        user.intentosFallidos++;
                        if (user.intentosFallidos >= 3) {
                            user.bloqueado = true;
                            JOptionPane.showMessageDialog(null, "Usuario bloqueado por intentos fallidos.");
                        } else {
                            JOptionPane.showMessageDialog(null, "Credenciales incorrectas.");
                        }
                        return;
                    }
                }
            }

            // Usuario no encontrado
            JOptionPane.showMessageDialog(null, "El usuario no existe.");
        }
    }
}

// Pantalla de usuario
class UserScreen extends JFrame {
    public UserScreen() {
        setTitle("Panel de Usuario");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        JButton createCombinationButton = new JButton("Crear Combinación");
        createCombinationButton.addActionListener(e -> {
            ArrayList<String> shirts = new ArrayList<>();
            ArrayList<String> pants = new ArrayList<>();

            for (Clothing item : MainApp.clothingItems) {
                if (item.type.equalsIgnoreCase("Camisa")) {
                    shirts.add(item.name);
                } else if (item.type.equalsIgnoreCase("Pantalón")) {
                    pants.add(item.name);
                }
            }

            String selectedShirt = (String) JOptionPane.showInputDialog(
                    null,
                    "Selecciona una camisa:",
                    "Camisas",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    shirts.toArray(),
                    shirts.isEmpty() ? null : shirts.get(0)
            );

            String selectedPants = (String) JOptionPane.showInputDialog(
                    null,
                    "Selecciona un pantalón:",
                    "Pantalones",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    pants.toArray(),
                    pants.isEmpty() ? null : pants.get(0)
            );

            if (selectedShirt != null && selectedPants != null) {
                MainApp.combinations.add(new Combination(selectedShirt, selectedPants));
                JOptionPane.showMessageDialog(null, "¡Combinación creada exitosamente!");
            } else {
                JOptionPane.showMessageDialog(null, "No se seleccionaron prendas.");
            }
        });

        JButton logoutButton = new JButton("Cerrar Sesión");
        logoutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Sesión cerrada.");
            new LoginScreen();
            dispose();
        });

        add(createCombinationButton);
        add(logoutButton);

        setVisible(true);
    }
}

// Pantalla de administrador
class AdminScreen extends JFrame {
    Admin admin;

    public AdminScreen(Admin admin) {
        this.admin = admin;
        setTitle("Panel de Administrador");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        JButton createUserButton = new JButton("Crear Usuario");
        createUserButton.addActionListener(e -> {
            String username = JOptionPane.showInputDialog("Nombre de usuario:");
            String password = JOptionPane.showInputDialog("Contraseña (solo números):");
            if (!password.matches("\\d+")) {
                JOptionPane.showMessageDialog(null, "La contraseña debe contener solo números.");
                return;
            }
            MainApp.users.add(new User(username, MainApp.hashPassword(password)));
            JOptionPane.showMessageDialog(null, "Usuario creado exitosamente.");
        });

        JButton createClothingButton = new JButton("Crear Ropa");
        createClothingButton.addActionListener(e -> {
            String type = JOptionPane.showInputDialog("Tipo de ropa (Pantalón o Camisa):");
            if (!type.equalsIgnoreCase("Pantalón") && !type.equalsIgnoreCase("Camisa")) {
                JOptionPane.showMessageDialog(null, "Tipo de ropa inválido.");
                return;
            }
            String name = JOptionPane.showInputDialog("Nombre de la prenda:");
            MainApp.clothingItems.add(new Clothing(type, name));
            JOptionPane.showMessageDialog(null, "Prenda creada exitosamente.");
        });

        JButton logoutButton = new JButton("Cerrar Sesión");
        logoutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Sesión cerrada.");
            new LoginScreen();
            dispose();
        });

        add(createUserButton);
        add(createClothingButton);
        add(logoutButton);

        setVisible(true);
    }
}
