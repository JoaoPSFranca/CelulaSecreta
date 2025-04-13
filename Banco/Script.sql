DROP DATABASE IF EXISTS cara_cara;
CREATE DATABASE cara_cara;
USE cara_cara;

CREATE TABLE game_session (
	session_id INT NOT NULL AUTO_INCREMENT,
    start_time DATETIME NOT NULL,
    end_time DATETIME,
    difficulty_level INT DEFAULT 1,
    PRIMARY KEY (session_id)
);

CREATE TABLE cell_function (
	function_id INT NOT NULL AUTO_INCREMENT,
    function_name VARCHAR(100) NOT NULL,
    function_desc TEXT NOT NULL,
    PRIMARY KEY (function_id)
);

CREATE TABLE kingdom (
	king_id INT NOT NULL AUTO_INCREMENT,
    king_name VARCHAR(50) NOT NULL,
    king_desc TEXT NOT NULL,
    PRIMARY KEY (king_id)
);

CREATE TABLE property (
	prop_id INT NOT NULL AUTO_INCREMENT,
    prop_name TEXT NOT NULL,
    PRIMARY KEY (prop_id)
);

CREATE TABLE organelle (
	org_id INT NOT NULL AUTO_INCREMENT,
    org_name VARCHAR(100) NOT NULL,
    org_desc TEXT NOT NULL,
    PRIMARY KEY (org_id)
);

CREATE TABLE card (
	card_id INT NOT NULL AUTO_INCREMENT,
    card_name VARCHAR(100) NOT NULL,
    card_desc TEXT NOT NULL,
    card_is_cell BOOLEAN NOT NULL,
    card_image BLOB,
    card_difficulty_level INT DEFAULT 1,
    card_active BOOLEAN NOT NULL DEFAULT TRUE,
	king_id INT NOT NULL,
    PRIMARY KEY (card_id),
    FOREIGN KEY (king_id) REFERENCES kingdom (king_id)
);

CREATE TABLE team (
	team_id INT NOT NULL AUTO_INCREMENT,
    team_name VARCHAR(100) NOT NULL,
    is_winner BOOLEAN DEFAULT FALSE,
	session_id INT NOT NULL, 
    assigned_card_id INT, 
    guessed_card_id INT,
    PRIMARY KEY (team_id),
    FOREIGN KEY (session_id) REFERENCES game_session (session_id),
    FOREIGN KEY (assigned_card_id) REFERENCES card (card_id),
    FOREIGN KEY (guessed_card_id) REFERENCES card (card_id) 
);

CREATE TABLE game_question (
	question_id INT NOT NULL AUTO_INCREMENT,
    session_id INT NOT NULL,
    team_id INT NOT NULL,
    question_text TEXT NOT NULL,
    question_answer VARCHAR(3),
    question_time DATETIME NOT NULL,
    PRIMARY KEY (question_id, session_id),
    FOREIGN KEY (session_id) REFERENCES game_session (session_id),
    FOREIGN KEY (team_id) REFERENCES team (team_id)
);

CREATE TABLE card_property (
	prop_id INT NOT NULL,
    card_id INT NOT NULL,
    card_prop_desc TEXT NOT NULL,
	PRIMARY KEY (prop_id, card_id),
    FOREIGN KEY (prop_id) REFERENCES property (prop_id),
    FOREIGN KEY (card_id) REFERENCES card (card_id)
);

CREATE TABLE card_organelle (
	org_id INT NOT NULL,
    card_id INT NOT NULL,
    PRIMARY KEY (org_id, card_id),
    FOREIGN KEY (org_id) REFERENCES organelle (org_id),
    FOREIGN KEY (card_id) REFERENCES card (card_id)
);

CREATE TABLE card_function (
	function_id INT NOT NULL,
    card_id INT NOT NULL,
    PRIMARY KEY (function_id, card_id),
    FOREIGN KEY (function_id) REFERENCES cell_function (function_id),
    FOREIGN KEY (card_id) REFERENCES card (card_id)
);

CREATE TABLE game_card_status (
    status_id INT NOT NULL AUTO_INCREMENT,
    session_id INT NOT NULL,
    card_id INT NOT NULL,
    team_id INT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deactivation_time DATETIME,
    deactivation_question_id INT,
    PRIMARY KEY (status_id),
    FOREIGN KEY (session_id) REFERENCES game_session (session_id),
    FOREIGN KEY (card_id) REFERENCES card (card_id),
    FOREIGN KEY (deactivation_question_id, session_id) REFERENCES game_question (question_id, session_id),
    FOREIGN KEY (team_id) REFERENCES team (team_id)
);