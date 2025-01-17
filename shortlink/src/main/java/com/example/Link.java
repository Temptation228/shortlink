package com.example;

public class Link {
    private int id;
    private String uuid;
    private String originalUrl;
    private String shortUrl;
    private Integer maxClicks;
    private long expirationTime;
    private int createdAt;
    private int clicks;

    public Link(int id, String uuid, String originalUrl, String shortUrl, Integer maxClicks, long expirationTime, int createdAt, int clicks) {
        this.id = id;
        this.uuid = uuid;
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
        this.maxClicks = maxClicks;
        this.expirationTime = expirationTime;
        this.createdAt = createdAt;
        this.clicks = clicks;
    }

    public int getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public Integer getMaxClicks() {
        return maxClicks;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public int getClicks() {
        return clicks;
    }
}

