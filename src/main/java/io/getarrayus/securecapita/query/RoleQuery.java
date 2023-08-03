package io.getarrayus.securecapita.query;

public class RoleQuery {

    public static final String SELECT_ROLE_BY_NAME_QUERY = "SELECT * FROM Roles WHERE name = :roleName";
    public static final String INSERT_ROLE_TO_USER_QUERY = "INSERT INTO UserRoles (user_id, role_id) VALUES (:userId, :roleId)";
    public static final String INSERT_ROLE_QUERY = "INSERT INTO Roles (name, permission) VALUES (:name, :permission)";
}
