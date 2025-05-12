DROP DATABASE IF EXISTS cara_cara;
CREATE DATABASE cara_cara;
USE cara_cara;

CREATE TABLE funcao_celula (
	funcao_id INT NOT NULL AUTO_INCREMENT,
	funcao_nome VARCHAR(100) NOT NULL,
    funcao_desc TEXT NOT NULL,
    PRIMARY KEY (funcao_id)
);

CREATE TABLE jogador (
	jogador_id INT NOT NULL AUTO_INCREMENT,
    jogador_nome VARCHAR(100) NOT NULL,
    vencedor TINYINT(1) DEFAULT '0',
    PRIMARY KEY (jogador_id)
);

CREATE TABLE organela (
	organela_id INT NOT NULL AUTO_INCREMENT,
    organela_nome VARCHAR(100) NOT NULL,
    organela_desc TEXT NOT NULL,
    PRIMARY KEY (organela_id)
);

CREATE TABLE propriedade (
	prop_id INT NOT NULL AUTO_INCREMENT,
    prop_nome TEXT NOT NULL,
    PRIMARY KEY (prop_id)
);

CREATE TABLE reino (
	reino_id INT NOT NULL AUTO_INCREMENT,
    reino_nome VARCHAR(50) NOT NULL,
    reino_desc TEXT NOT NULL,
    PRIMARY KEY (reino_id)
);

CREATE TABLE sessao_jogo (
	sessao_id INT NOT NULL AUTO_INCREMENT,
    sessao_inicio DATETIME NOT NULL,
    sessao_fim DATETIME,
    nivel_dificuldade INT DEFAULT 1,
    PRIMARY KEY (sessao_id)
);

CREATE TABLE carta (
	carta_id INT NOT NULL AUTO_INCREMENT,
    carta_nome VARCHAR(100) NOT NULL,
    carta_desc TEXT NOT NULL,
    carta_celula TINYINT(1) NOT NULL,
    carta_nivel_dificuldade INT DEFAULT 1,
    reino_id INT NOT NULL,
    PRIMARY KEY (carta_id),
    FOREIGN KEY (reino_id) REFERENCES reino (reino_id)
);

CREATE TABLE carta_organela (
	organela_id INT NOT NULL,
    carta_id INT NOT NULL,
    PRIMARY KEY (organela_id, carta_id),
    FOREIGN KEY (organela_id) REFERENCES organela (organela_id),
    FOREIGN KEY (carta_id) REFERENCES carta (carta_id)
);

CREATE TABLE funcao_carta (
	funcao_id INT NOT NULL,
    carta_id INT NOT NULL,
    PRIMARY KEY (funcao_id, carta_id),
    FOREIGN KEY (funcao_id) REFERENCES funcao_celula (funcao_id),
    FOREIGN KEY (carta_id) REFERENCES carta (carta_id)
);

CREATE TABLE propriedade_carta (
	prop_id INT NOT NULL,
    carta_id INT NOT NULL,
    carta_prop_desc TEXT NOT NULL,
    PRIMARY KEY (prop_id, carta_id),
    FOREIGN KEY (prop_id) REFERENCES propriedade (prop_id),
    FOREIGN KEY (carta_id) REFERENCES carta (carta_id)
);

CREATE TABLE times (
	sessao_id INT NOT NULL,
    jogador_id INT NOT NULL,
    PRIMARY KEY (sessao_id, jogador_id),
    FOREIGN KEY (sessao_id) REFERENCES sessao_jogo (sessao_id),
    FOREIGN KEY (jogador_id) REFERENCES jogador (jogador_id)
);

CREATE TABLE questao (
	questao_id INT NOT NULL,
    sessao_id INT NOT NULL,
    jogador_id INT NOT NULL,
    questao_texto TEXT NOT NULL,
    questao_resposta VARCHAR(3) NOT NULL,
    PRIMARY KEY (questao_id, sessao_id, jogador_id),
    FOREIGN KEY (sessao_id, jogador_id) REFERENCES times (sessao_id, jogador_id)
);

CREATE TABLE jogo_carta_status (
	jogador_id INT NOT NULL,
    sessao_id INT NOT NULL,
    carta_id INT NOT NULL,
    ativa TINYINT(1) DEFAULT '1',
    sorteada TINYINT(1) DEFAULT '0',
    advinhada TINYINT(1) DEFAULT '0',
    questao_id INT NOT NULL,
    PRIMARY KEY (jogador_id, sessao_id, carta_id),
    FOREIGN KEY (jogador_id, sessao_id) REFERENCES times (jogador_id, sessao_id),
    FOREIGN KEY (carta_id) REFERENCES carta (carta_id),
    FOREIGN KEY (questao_id) REFERENCES questao (questao_id)
);
