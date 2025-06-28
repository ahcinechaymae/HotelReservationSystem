package com.example.hotel;

import java.util.*;

enum RoomType {
    STANDARD, JUNIOR, SUITE
}

class Room {
    int roomNumber;
    RoomType type;
    int pricePerNight;

    Room(int roomNumber, RoomType type, int pricePerNight) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.pricePerNight = pricePerNight;
    }
}

class User {
    int userId;
    int balance;

    User(int userId, int balance) {
        this.userId = userId;
        this.balance = balance;
    }
}

class Booking {
    User user;
    Room room;
    Date checkIn;
    Date checkOut;
    int totalPrice;
    Date bookingDate;

    Booking(User user, Room room, Date checkIn, Date checkOut, int totalPrice) {
        this.user = new User(user.userId, user.balance);
        this.room = new Room(room.roomNumber, room.type, room.pricePerNight);
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalPrice = totalPrice;
        this.bookingDate = new Date();
    }
}

public class Service {
    ArrayList<Room> rooms = new ArrayList<>();
    ArrayList<User> users = new ArrayList<>();
    ArrayList<Booking> bookings = new ArrayList<>();

    void setRoom(int roomNumber, RoomType roomType, int roomPricePerNight) {
        for (Room r : rooms) {
            if (r.roomNumber == roomNumber) {
                r.type = roomType;
                r.pricePerNight = roomPricePerNight;
                return;
            }
        }
        rooms.add(0, new Room(roomNumber, roomType, roomPricePerNight));
    }

    void setUser(int userId, int balance) {
        for (User u : users) {
            if (u.userId == userId) {
                u.balance = balance;
                return;
            }
        }
        users.add(0, new User(userId, balance));
    }

    void bookRoom(int userId, int roomNumber, Date checkIn, Date checkOut) {
        if (!checkIn.before(checkOut)) {
            System.out.println("Invalid dates");
            return;
        }

        User user = null;
        Room room = null;

        for (User u : users) if (u.userId == userId) user = u;
        for (Room r : rooms) if (r.roomNumber == roomNumber) room = r;

        if (user == null || room == null) {
            System.out.println("User or room not found");
            return;
        }

        for (Booking b : bookings) {
            if (b.room.roomNumber == roomNumber && datesOverlap(checkIn, checkOut, b.checkIn, b.checkOut)) {
                System.out.println("Room not available");
                return;
            }
        }

        long nights = (checkOut.getTime() - checkIn.getTime()) / (1000 * 60 * 60 * 24);
        int totalCost = (int)nights * room.pricePerNight;

        if (user.balance < totalCost) {
            System.out.println("Insufficient balance");
            return;
        }

        user.balance -= totalCost;
        bookings.add(0, new Booking(user, room, checkIn, checkOut, totalCost));
        System.out.println("Booking successful");
    }

    boolean datesOverlap(Date aStart, Date aEnd, Date bStart, Date bEnd) {
        return !aEnd.before(bStart) && !aStart.after(bEnd);
    }

    void printAll() {
        System.out.println("\n-- Rooms --");
        for (Room r : rooms) {
            System.out.println("Room " + r.roomNumber + " | Type: " + r.type + " | Price: " + r.pricePerNight);
        }
        System.out.println("\n-- Bookings --");
        for (Booking b : bookings) {
            System.out.println("User: " + b.user.userId + ", Room: " + b.room.roomNumber + ", Type: " + b.room.type +
                    ", Price/night: " + b.room.pricePerNight + ", From: " + b.checkIn + " to: " + b.checkOut +
                    ", Total: " + b.totalPrice);
        }
    }

    void printAllUsers() {
        System.out.println("\n-- Users --");
        for (User u : users) {
            System.out.println("User ID: " + u.userId + " | Balance: " + u.balance);
        }
    }

    public static void main(String[] args) throws Exception {
        Service service = new Service();

        service.setRoom(1, RoomType.STANDARD, 1000);
        service.setRoom(2, RoomType.JUNIOR, 2000);
        service.setRoom(3, RoomType.SUITE, 3000);

        service.setUser(1, 5000);
        service.setUser(2, 10000);

        Calendar cal = Calendar.getInstance();

        cal.set(2026, Calendar.JUNE, 30); Date in1 = cal.getTime();
        cal.set(2026, Calendar.JULY, 7); Date out1 = cal.getTime();
        service.bookRoom(1, 2, in1, out1);

        service.bookRoom(1, 2, out1, in1);

        cal.set(2026, Calendar.JULY, 7); Date in2 = cal.getTime();
        cal.set(2026, Calendar.JULY, 8); Date out2 = cal.getTime();
        service.bookRoom(1, 1, in2, out2);

        cal.set(2026, Calendar.JULY, 9); Date out3 = cal.getTime();
        service.bookRoom(2, 1, in2, out3);

        cal.set(2026, Calendar.JULY, 7); Date in4 = cal.getTime();
        cal.set(2026, Calendar.JULY, 8); Date out4 = cal.getTime();
        service.bookRoom(2, 3, in4, out4);

        service.setRoom(1, RoomType.SUITE, 10000);

        service.printAllUsers();
        service.printAll();
    }
}
