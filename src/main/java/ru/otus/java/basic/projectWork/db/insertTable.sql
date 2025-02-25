INSERT INTO roles (id, name) VALUES (1,'admin'), (2,'user');

INSERT INTO users (password,login,username) VALUES
('admin','admin','ADMIN');

INSERT INTO  users_to_roles(user_id, role_id) VALUES
(1,1);

INSERT INTO rooms (name, password, owner_id, creation_date) VALUES ('Общая комната', NULL, 1, NOW());

