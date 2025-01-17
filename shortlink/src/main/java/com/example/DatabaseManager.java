package com.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {
    private final String url = "jdbc:sqlite:uuids.db";

    public DatabaseManager() {
        createNewDatabase();
    }

    private void createNewDatabase() {
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String createUsersTable = "CREATE TABLE IF NOT EXISTS users (\n"
                        + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                        + " uuid TEXT NOT NULL UNIQUE,\n"
                        + " password TEXT NOT NULL\n"
                        + ");";

                String createLinksTable = "CREATE TABLE IF NOT EXISTS links (\n"
                        + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                        + " uuid TEXT NOT NULL,\n"
                        + " original_url TEXT NOT NULL,\n"
                        + " short_url TEXT NOT NULL UNIQUE,\n"
                        + " max_clicks INTEGER,\n"
                        + " expiration_time INTEGER,\n"
                        + " created_at INTEGER DEFAULT (strftime('%s', 'now')),\n"
                        + " clicks INTEGER DEFAULT 0\n"
                        + ");";

                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createUsersTable);
                    stmt.execute(createLinksTable);
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при создании БД: " + e.getMessage());
        }
    }

    public boolean checkUUIDAndPassword(String uuid, String password) {
        String sql = "SELECT uuid FROM users WHERE uuid = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Ошибка в процессе аутентификации: " + e.getMessage());
            return false;
        }
    }

    public String saveUser(String password) {
        String uuid = UUID.randomUUID().toString();
        String sql = "INSERT INTO users(uuid, password) VALUES(?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            System.out.println("Пользователь сохранен: UUID = " + uuid);
            return uuid;
        } catch (SQLException e) {
            System.err.println("Ошибка при сохранении пользователя: " + e.getMessage());
            return null;
        }
    }

    public void saveLink(String uuid, String originalUrl, String shortUrl, Integer maxClicks, long expirationTime) {
        String sql = "INSERT INTO links(uuid, original_url, short_url, max_clicks, expiration_time) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            pstmt.setString(2, originalUrl);
            pstmt.setString(3, shortUrl);
            pstmt.setObject(4, maxClicks);
            pstmt.setLong(5, expirationTime);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка при сохранении ссылки: " + e.getMessage());
        }
    }

    public Link getLinkByShortUrl(String shortUrl) {
        String sql = "SELECT * FROM links WHERE short_url = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, shortUrl);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Link(
                        rs.getInt("id"),
                        rs.getString("uuid"),
                        rs.getString("original_url"),
                        rs.getString("short_url"),
                        rs.getObject("max_clicks", Integer.class),
                        rs.getInt("expiration_time"),
                        rs.getInt("created_at"),
                        rs.getInt("clicks")
                );
            }
        } catch (SQLException e) {
            System.err.println("Ошибка получения ссылки: " + e.getMessage());
        }
        return null;
    }

    public void incrementClickCount(int linkId) {
        String sql = "UPDATE links SET clicks = clicks + 1 WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, linkId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка обновления ссылки: " + e.getMessage());
        }
    }

    public void deleteExpiredLinks() {
        String sql = "DELETE FROM links WHERE (strftime('%s', 'now') - created_at > expiration_time) " +
                "OR (max_clicks IS NOT NULL AND clicks >= max_clicks)";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении недействительных ссылок: " + e.getMessage());
        }
    }
    public List<Link> getAllLinksByUUID(String uuid) {
        String sql = "SELECT * FROM links WHERE uuid = ?";
        List<Link> links = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Link link = new Link(
                        rs.getInt("id"),
                        rs.getString("uuid"),
                        rs.getString("original_url"),
                        rs.getString("short_url"),
                        rs.getObject("max_clicks", Integer.class),
                        rs.getInt("expiration_time"),
                        rs.getInt("created_at"),
                        rs.getInt("clicks")
                );
                links.add(link);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка получения ссылок по UUID: " + e.getMessage());
        }
        return links;
    }
    public void deleteLinkById(int linkId) {
        String sql = "DELETE FROM links WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, linkId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Ссылка с ID " + linkId + " была удалена.");
            } else {
                System.out.println("Не найдено ссылки c ID " + linkId + ".");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении ссылки: " + e.getMessage());
        }
    }
}