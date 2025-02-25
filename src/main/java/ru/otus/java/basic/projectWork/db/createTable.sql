CREATE TABLE roles (
	id serial4 NOT NULL,
	"name" VARCHAR(255) NOT NULL,
	CONSTRAINT roles_pkey PRIMARY KEY (id)
);

CREATE TABLE users(
id serial PRIMARY KEY,
password VARCHAR(255) NOT NULL,
login VARCHAR(255) NOT NULL UNIQUE,
username VARCHAR(255) NOT NULL UNIQUE);

CREATE TABLE users_to_roles(
user_id INT NOT NULL,
role_id INT NOT NULL,
PRIMARY KEY (user_id, role_id),
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE);

CREATE TABLE bans (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    ban_start TIMESTAMP NOT NULL,
    ban_end TIMESTAMP,
    reason VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE rooms (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    owner_id INT NOT NULL,
    creation_date TIMESTAMP NOT NULL,
    last_activity TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE room_users (
    room_id INT NOT NULL,
    user_id INT NOT NULL,
    PRIMARY KEY (room_id, user_id),
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE room_messages (
    id SERIAL PRIMARY KEY,
    room_id INT NOT NULL,
    sender VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE
);

CREATE TABLE room_invites (
    room_id INT NOT NULL,
    user_id INT NOT NULL,
    invited_by INT NOT NULL,
    PRIMARY KEY (room_id, user_id),
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (invited_by) REFERENCES users(id) ON DELETE CASCADE
);

 CREATE TABLE last_rooms (
        user_id INTEGER PRIMARY KEY,
        room_name VARCHAR(255) NOT NULL,
        FOREIGN KEY (user_id) REFERENCES users(id)
    );