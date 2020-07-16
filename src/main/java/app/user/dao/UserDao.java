package app.user.dao;

import app.ticket.entity.*;
import app.user.entity.*;

import java.util.List;

public interface UserDao {

    Ticket insertOne(Ticket ticket);
}