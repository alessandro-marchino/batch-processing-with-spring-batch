DROP TABLE IF EXISTS student;

CREATE TABLE student (
  id bigint NOT NULL AUTO_INCREMENT,
  first_name varchar(45),
  last_name varchar(45),
  email varchar(45),
  PRIMARY KEY (`id`)
);

INSERT INTO student (first_name, last_name, email)
VALUES
  ('John','Smith','john@gmail.com'),
  ('Sachin','Dave','sachin@gmail.com'),
  ('Peter','Mark','peter@gmail.com'),
  ('Martin','Smith','martin@gmail.com'),
  ('Raj','Patel','raj@gmail.com'),
  ('Virat','Yadav','virat@gmail.com'),
  ('Prabhas','Shirke','prabhas@gmail.com'),
  ('Tina','Kapoor','tina@gmail.com'),
  ('Mona','Sharma','mona@gmail.com'),
  ('Rahul','Varma','rahul@gmail.com');
