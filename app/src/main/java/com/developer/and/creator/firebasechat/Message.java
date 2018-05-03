package com.developer.and.creator.firebasechat;

import java.util.Date;

public class Message {
    private String textMessage;
    private String author;
    private long timeMessage;

    //создаем конструктор двух переменных Сообещния и автора
    public Message(String textMessage, String author) {
        this.textMessage = textMessage;
        this.author = author;

        //заносим время, дату
        timeMessage = new Date().getTime();
    }

    //создаем пустой конструктор
    public Message() {

    }

    //создаем геттеры и сеттеры
    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getTimeMessage() {
        return timeMessage;
    }

    public void setTimeMessage(long timeMessage) {
        this.timeMessage = timeMessage;
    }
}
