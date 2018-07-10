DROP TABLE movies;
DROP TABLE shows;
DROP TABLE rooms;
DROP TABLE seats;

CREATE TABLE movies (
    movieID int NOT NULL GENERATED ALWAYS AS IDENTITY,
    title varchar (50) NOT NULL,
    country varchar (30) NOT NULL,
    dubbed char (1) NOT NULL,
    director varchar (20) NOT NULL,
    synopsis varchar (250) NOT NULL,
    playtime int NOT NULL,
    maxPlays int NOT NULL,
    rating int NOT NULL,
    PRIMARY KEY (movieID)
);

CREATE TABLE shows (
    showID int NOT NULL GENERATED ALWAYS AS IDENTITY,
    movieID int NOT NULL,
    startTime TIMESTAMP NOT NULL,
    roomID int NOT NULL,
    PRIMARY KEY (showID)
);

CREATE TABLE rooms (
    roomID int NOT NULL,
    roomName varchar (20) NOT NULL,
    numOfRows int NOT NULL,
    numOfColumns int NOT NULL,
    PRIMARY KEY (roomID)
);

CREATE TABLE seats (
    seatID int NOT NULL GENERATED ALWAYS AS IDENTITY,
    showID int,
    rowNum int,
    columnNum int,
    status char (1),
    PRIMARY KEY (seatID)
);

INSERT INTO movies (title, country, dubbed, director, synopsis, playtime, maxPlays, rating)
VALUES
    ('The Shawshank Redemption', 'USA', 'Y', 'Frank Darabont', 
    'Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.', 
    142, 5, 3),
    ('The Godfather', 'USA', 'Y', 'Francis Ford Coppola', 
    'The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.', 
    175, 1, 3),
    ('The Godfather: Part II', 'USA', 'Y', 'Francis Ford Coppola', 
    'The early life and career of Vito Corleone in 1920s New York City is portrayed, while his son, Michael, expands and tightens his grip on the family crime syndicate.', 
    202, 3, 3),
    ('The Dark Knight', 'USA', 'N', 'Christopher Nolan', 
    'When the menace known as the Joker emerges from his mysterious past, he wreaks havoc and chaos on the people of Gotham. The Dark Knight must accept one of the greatest psychological and physical tests of his ability to fight injustice.', 
    152, 10, 2),
    ('12 Angry Men', 'USA', 'Y', 'Sidney Lumet', 
    'A jury holdout attempts to prevent a miscarriage of justice by forcing his colleagues to reconsider the evidence.', 
    96, 3, 1),
    ('Schindler''s List', 'USA', 'N', 'Steven Spielberg', 
    'In German-occupied Poland during World War II, Oskar Schindler gradually becomes concerned for his Jewish workforce after witnessing their persecution by the Nazi Germans.', 
    195, 6, 2),
    ('The Lord of the Rings: The Return of the King', 'USA', 'Y', 'Peter Jackson', 
    'Gandalf and Aragorn lead the World of Men against Sauron''s army to draw his gaze from Frodo and Sam as they approach Mount Doom with the One Ring.', 
    201, 6, 1),
    ('Pulp Fiction', 'USA', 'N', 'Quentin Tarantino', 
    'The lives of two mob hitmen, a boxer, a gangster''s wife, and a pair of diner bandits intertwine in four tales of violence and redemption.', 
    154, 5, 3),
    ('The Good, the Bad and the Ugly', 'USA', 'Y', 'Sergio Leone', 
    'A bounty hunting scam joins two men in an uneasy alliance against a third in a race to find a fortune in gold buried in a remote cemetery.', 
    178, 3, 2),
    ('Fight Club', 'USA', 'N', 'David Fincher', 
    'An insomniac office worker, looking for a way to change his life, crosses paths with a devil-may-care soapmaker, forming an underground fight club that evolves into something much, much more.', 
    139, 6, 3);

INSERT INTO shows (movieID, startTime, roomID)
VALUES
    (5, '2018-06-06 16:00:00', 701),
    (2, '2018-05-20 22:00:00', 702),
    (1, '2018-05-27 21:00:00', 701),
    (3, '2018-05-04 21:00:00', 702),
    (4, '2018-06-07 19:00:00', 701),
    (5, '2018-06-06 16:00:00', 704),
    (6, '2018-06-26 19:00:00', 702),
    (7, '2018-06-05 15:00:00', 701),
    (8, '2018-06-06 21:00:00', 702),
    (9, '2018-06-10 17:00:00', 703),
    (10, '2018-06-11 22:00:00', 701);

INSERT INTO rooms (roomID, roomName, numOfRows, numOfColumns)
VALUES
    (701, 'Main Room', 16, 18),
    (702, 'Red Room', 12, 14),
    (703, 'VIP Room', 4, 4),
    (704, 'Green Room', 10, 10);

-- status == 'Y' : foglalt
-- status == 'N' : szabad
-- INSERT INTO seats (showID, rowNum, columnNum, status)
-- VALUES
--     (101, 1, 1, 'N'),
--     (101, 1, 2, 'N'),
--     (101, 1, 3, 'N'),
--     (101, 1, 4, 'N'),
--     (101, 2, 1, 'N'),
--     (101, 2, 2, 'N'),
--     (101, 2, 3, 'N'),
--     (101, 2, 4, 'N'),
--     (101, 3, 1, 'N'),
--     (101, 3, 2, 'N'),
--     (101, 3, 3, 'N'),
--     (101, 3, 4, 'N'),
--     (101, 4, 1, 'N'),
--     (101, 4, 2, 'N'),
--     (101, 4, 3, 'N'),
--     (101, 4, 4, 'N');