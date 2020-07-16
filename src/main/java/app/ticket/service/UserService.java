package app.ticket.service;

import app.ticket.entity.User;

public interface UserService {
    User getUserByUsername(String username);
}
